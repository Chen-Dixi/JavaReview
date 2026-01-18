package dixi.bupt.datastructure.advanced;

import java.util.Random;

/**
 * 跳表, 常用于数据库索引 和 Redis
 * 支持 O(log n) 的查找、插入、删除操作，基于有序链表的多级索引结构
 *
 * @author chendixi
 * Created on 2025-03-02
 */
public class SkipList<K extends Comparable<K>, V> {

    private static final int MAX_LEVEL = 32;
    /**
     * 层数增加概率，P=0.25 时期望层数约为 1.33，与 Redis 的 zset 一致
     */
    private static final double P = 0.25;

    private final Random random;
    private final Node<K, V> head;
    private int level;
    private int size;

    /**
     * 跳表节点，每个节点在 [0, level] 层都有前向指针
     */
    private static class Node<K, V> {
        final K key;
        V value;
        final Node<K, V>[] forward;

        @SuppressWarnings("unchecked")
        Node(K key, V value, int nodeLevel) {
            this.key = key;
            this.value = value;
            this.forward = new Node[nodeLevel + 1];
        }
    }

    public SkipList() {
        this(new Random());
    }

    /**
     * 可注入 Random，便于单测时固定随机种子
     */
    public SkipList(Random random) {
        this.random = random;
        this.head = new Node<>(null, null, MAX_LEVEL);
        this.level = 0;
        this.size = 0;
    }

    /**
     * 随机生成新节点的层数 (0 ~ MAX_LEVEL-1)
     */
    private int randomLevel() {
        int lvl = 0;
        while (lvl < MAX_LEVEL - 1 && random.nextDouble() < P) {
            lvl++;
        }
        return lvl;
    }

    /**
     * 查找 key 对应的 value，不存在返回 null
     */
    public V get(K key) {
        Node<K, V> x = head;
        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].key.compareTo(key) < 0) {
                x = x.forward[i];
            }
        }
        x = x.forward[0];
        return (x != null && x.key.compareTo(key) == 0) ? x.value : null;
    }

    /**
     * 插入或更新：若 key 已存在则更新 value
     *
     * @return 若为更新返回旧 value，否则返回 null
     */
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        Node<K, V>[] update = (Node<K, V>[]) new Node<?, ?>[MAX_LEVEL + 1];
        Node<K, V> x = head;

        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].key.compareTo(key) < 0) {
                x = x.forward[i];
            }
            update[i] = x;
        }
        x = x.forward[0];

        if (x != null && x.key.compareTo(key) == 0) {
            V old = x.value;
            x.value = value;
            return old;
        }

        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level + 1; i <= newLevel; i++) {
                update[i] = head;
            }
            level = newLevel;
        }

        Node<K, V> newNode = new Node<>(key, value, newLevel);
        for (int i = 0; i <= newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
        size++;
        return null;
    }

    /**
     * 删除指定 key
     *
     * @return 若存在返回其 value，否则返回 null
     */
    @SuppressWarnings("unchecked")
    public V remove(K key) {
        Node<K, V>[] update = (Node<K, V>[]) new Node<?, ?>[MAX_LEVEL + 1];
        Node<K, V> x = head;

        for (int i = level; i >= 0; i--) {
            while (x.forward[i] != null && x.forward[i].key.compareTo(key) < 0) {
                x = x.forward[i];
            }
            update[i] = x;
        }
        x = x.forward[0];

        if (x == null || x.key.compareTo(key) != 0) {
            return null;
        }

        for (int i = 0; i <= level; i++) {
            if (update[i].forward[i] != x) {
                break;
            }
            update[i].forward[i] = x.forward[i];
        }

        while (level > 0 && head.forward[level] == null) {
            level--;
        }
        size--;
        return x.value;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 返回当前跳表的最大层数（不含 head 所在层），仅用于调试或观测
     */
    public int currentLevel() {
        return level;
    }
}
