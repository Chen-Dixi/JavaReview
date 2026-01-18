package dixi.bupt.datastructure.advanced;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * 线程安全的 LRU（Least Recently Used）缓存实现。
 * <p>
 * 使用 ConcurrentHashMap 存储键值对，双向链表维护访问顺序：
 * 头部为最近使用（MRU），尾部为最久未使用（LRU），超出容量时淘汰尾部元素。
 * </p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author chendixi
 * Created on 2025-03-08
 */
public class ConcurrentLRUCache<K, V> {

    private final int capacity;
    private final ConcurrentHashMap<K, Node<K, V>> map;
    private final Node<K, V> head;
    private final Node<K, V> tail;
    private final ReentrantLock lock;
    private final Consumer<EvictionEntry<K, V>> evictionListener;

    /**
     * 创建一个指定容量的 LRU 缓存。
     *
     * @param capacity 最大容量，必须大于 0
     * @throws IllegalArgumentException 当 capacity <= 0
     */
    public ConcurrentLRUCache(int capacity) {
        this(capacity, null);
    }

    /**
     * 创建一个指定容量、带淘汰监听器的 LRU 缓存。
     *
     * @param capacity         最大容量，必须大于 0
     * @param evictionListener 元素被淘汰时的回调，可为 null
     * @throws IllegalArgumentException 当 capacity <= 0
     */
    public ConcurrentLRUCache(int capacity, Consumer<EvictionEntry<K, V>> evictionListener) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive, got: " + capacity);
        }
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>(capacity + 1, 0.75f);
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.head.next = tail;
        this.tail.prev = head;
        this.lock = new ReentrantLock();
        this.evictionListener = evictionListener;
    }

    /**
     * 获取指定键对应的值；若存在则将其移至「最近使用」位置。
     *
     * @param key 键
     * @return 对应的值，不存在则 null
     */
    public V get(K key) {
        if (key == null) {
            throw new NullPointerException("key must not be null");
        }
        lock.lock();
        try {
            Node<K, V> node = map.get(key);
            if (node == null) {
                return null;
            }
            moveToHead(node);
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 若键不存在则返回 defaultValue，不改变访问顺序。
     *
     * @param key          键
     * @param defaultValue 键不存在时的默认值
     * @return 值或默认值
     */
    public V getOrDefault(K key, V defaultValue) {
        V v = get(key);
        return v != null ? v : defaultValue;
    }

    /**
     * 放入键值对。若键已存在则更新值并移至最近使用；否则在头部插入新节点，
     * 若超出容量则淘汰最久未使用的项并触发淘汰回调。
     *
     * @param key   键，不能为 null
     * @param value 值，不能为 null
     * @throws NullPointerException key 或 value 为 null
     */
    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("key and value must not be null");
        }
        lock.lock();
        try {
            Node<K, V> node = map.get(key);
            if (node != null) {
                node.value = value;
                moveToHead(node);
                return;
            }
            node = new Node<>(key, value);
            map.put(key, node);
            addToHead(node);
            if (map.size() > capacity) {
                Node<K, V> evicted = removeTail();
                map.remove(evicted.key);
                if (evictionListener != null) {
                    evictionListener.accept(new EvictionEntry<>(evicted.key, evicted.value));
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 键不存在时才放入，不会更新已有键的访问顺序。
     *
     * @param key   键
     * @param value 值
     * @return 若放入成功返回 null，若键已存在返回旧值
     */
    public V putIfAbsent(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("key and value must not be null");
        }
        lock.lock();
        try {
            Node<K, V> node = map.get(key);
            if (node != null) {
                return node.value;
            }
            node = new Node<>(key, value);
            map.put(key, node);
            addToHead(node);
            if (map.size() > capacity) {
                Node<K, V> evicted = removeTail();
                map.remove(evicted.key);
                if (evictionListener != null) {
                    evictionListener.accept(new EvictionEntry<>(evicted.key, evicted.value));
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 移除指定键及其对应值。
     *
     * @param key 键
     * @return 若存在则返回被移除的值，否则 null
     */
    public V remove(K key) {
        if (key == null) {
            throw new NullPointerException("key must not be null");
        }
        lock.lock();
        try {
            Node<K, V> node = map.remove(key);
            if (node == null) {
                return null;
            }
            removeNode(node);
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 判断是否包含指定键。
     *
     * @param key 键
     * @return 是否包含
     */
    public boolean containsKey(K key) {
        return key != null && map.containsKey(key);
    }

    /**
     * 当前缓存中的键值对数量。
     *
     * @return 数量，0 &le; size &le; capacity
     */
    public int size() {
        return map.size();
    }

    /**
     * 是否为空。
     *
     * @return 无元素时为 true
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * 清空缓存，不触发淘汰回调。
     */
    public void clear() {
        lock.lock();
        try {
            map.clear();
            head.next = tail;
            tail.prev = head;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取最大容量。
     *
     * @return capacity
     */
    public int getCapacity() {
        return capacity;
    }

    // ---------- 链表操作（需在 lock 内调用） ----------

    private void addToHead(Node<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }

    private Node<K, V> removeTail() {
        Node<K, V> node = tail.prev;
        removeNode(node);
        return node;
    }

    // ---------- 内部结构 ----------

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * 淘汰时的键值对快照，供 {@link Consumer} 使用。
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    public static class EvictionEntry<K, V> {
        private final K key;
        private final V value;

        public EvictionEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
