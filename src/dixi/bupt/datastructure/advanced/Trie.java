package dixi.bupt.datastructure.advanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前缀树（字典树），用于高效的前缀匹配与单词检索
 * 支持 O(m) 的插入、查找、前缀匹配，m 为字符串长度
 * 典型应用：自动补全、拼写检查、IP 路由表
 *
 * @author chendixi
 * Created on 2025-03-02
 */
public class Trie {

    private final TrieNode root;
    private int size;

    /**
     * 内部节点，使用 Map 支持任意字符（含中文等）
     */
    private static class TrieNode {
        final Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord;
    }

    public Trie() {
        this.root = new TrieNode();
        this.size = 0;
    }

    /**
     * 插入单词，若已存在则仅更新 isEndOfWord，不重复计数
     */
    public void insert(String word) {
        if (word == null) {
            return;
        }
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        if (!node.isEndOfWord) {
            node.isEndOfWord = true;
            size++;
        }
    }

    /**
     * 精确查找：是否存在该完整单词
     */
    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * 是否存在以 prefix 为前缀的单词（含 prefix 自身为完整单词的情况）
     */
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    /**
     * 查找前缀/单词所对应的节点，不存在则返回 null
     */
    private TrieNode findNode(String s) {
        if (s == null) {
            return null;
        }
        TrieNode node = root;
        for (int i = 0; i < s.length(); i++) {
            node = node.children.get(s.charAt(i));
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /**
     * 删除单词。若词不存在则无效果；删除后会自动收缩不再代表任何词路径的节点
     */
    public boolean delete(String word) {
        if (word == null) {
            return false;
        }
        boolean[] removed = new boolean[1];
        remove(root, word, 0, removed);
        if (removed[0]) {
            size--;
            return true;
        }
        return false;
    }

    /**
     * 从 node 开始删除 word[index..)，返回当前节点是否可被父节点移除（无子女且非某词结尾）
     */
    private boolean remove(TrieNode node, String word, int index, boolean[] removed) {
        if (index == word.length()) {
            if (!node.isEndOfWord) {
                return false;
            }
            node.isEndOfWord = false;
            removed[0] = true;
            return node.children.isEmpty();
        }
        char c = word.charAt(index);
        TrieNode child = node.children.get(c);
        if (child == null) {
            return false;
        }
        boolean canRemove = remove(child, word, index + 1, removed);
        if (canRemove) {
            node.children.remove(c);
        }
        return node.children.isEmpty() && !node.isEndOfWord;
    }

    /**
     * 返回所有以 prefix 为前缀的完整单词；若 prefix 为 null 则按空串处理，即返回所有单词
     */
    public List<String> getWordsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        String p = prefix == null ? "" : prefix;
        TrieNode node = findNode(p);
        if (node == null) {
            return result;
        }
        collect(node, new StringBuilder(p), result);
        return result;
    }

    private void collect(TrieNode node, StringBuilder path, List<String> result) {
        if (node.isEndOfWord) {
            result.add(path.toString());
        }
        for (Map.Entry<Character, TrieNode> e : node.children.entrySet()) {
            path.append(e.getKey());
            collect(e.getValue(), path, result);
            path.setLength(path.length() - 1);
        }
    }

    /**
     * 当前 Trie 中不同完整单词的数量
     */
    public int size() {
        return size;
    }

    /**
     * 是否未存储任何单词
     */
    public boolean isEmpty() {
        return size == 0;
    }
}
