package dixi.bupt.datastructure.advanced;

/**
 * 前缀树（字典树），用于高效的前缀匹配与单词检索
 * 支持 O(m) 的插入、查找、前缀匹配，m 为字符串长度
 * 典型应用：自动补全、拼写检查、IP 路由表
 *
 * @author chendixi
 * Created on 2025-03-02
 */
public class Trie {

    /**
     * Trie 树的节点类
     * 每个节点包含指向 26 个字母（a-z）的子节点指针
     * 以及标记该节点是否为某个单词的结尾
     */
    private static class TrieNode {
        // 指向下一个节点的引用数组（26 个字母）
        TrieNode[] children = new TrieNode[26];

        // 标记该节点是否为一个单词的结尾
        boolean isEndOfWord = false;

        // 该节点的字符值（用于调试，可选）
        char ch;

        TrieNode() {
        }

        TrieNode(char ch) {
            this.ch = ch;
        }
    }

    // Trie 树的根节点
    private TrieNode root;

    /**
     * 初始化 Trie 树
     */
    public Trie() {
        root = new TrieNode();
    }

    /**
     * 向 Trie 树中插入一个单词
     * 时间复杂度：O(m)，m 为单词长度
     * 空间复杂度：O(m*26) 最坏情况
     *
     * @param word 要插入的单词
     */
    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }

        TrieNode node = root;

        // 遍历单词的每个字符
        for (char c : word.toCharArray()) {
            // 计算字符对应的索引（'a' 对应 0，'b' 对应 1，...，'z' 对应 25）
            int index = c - 'a';

            // 如果该位置没有节点，创建新节点
            if (node.children[index] == null) {
                node.children[index] = new TrieNode(c);
            }

            // 移动到下一个节点
            node = node.children[index];
        }

        // 标记单词的结尾
        node.isEndOfWord = true;
    }

    /**
     * 精确查找一个单词是否存在于 Trie 树中
     * 时间复杂度：O(m)，m 为单词长度
     * 空间复杂度：O(1)
     *
     * @param word 要查找的单词
     * @return 如果单词存在返回 true，否则返回 false
     */
    public boolean search(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        TrieNode node = root;

        // 遍历单词的每个字符
        for (char c : word.toCharArray()) {
            int index = c - 'a';

            // 如果该位置没有节点，说明单词不存在
            if (node.children[index] == null) {
                return false;
            }

            // 移动到下一个节点
            node = node.children[index];
        }

        // 检查该节点是否被标记为单词结尾
        return node.isEndOfWord;
    }

    /**
     * 检查是否存在以给定前缀开头的单词
     * 时间复杂度：O(m)，m 为前缀长度
     * 空间复杂度：O(1)
     *
     * @param prefix 要检查的前缀
     * @return 如果存在以该前缀开头的单词返回 true，否则返回 false
     */
    public boolean startsWith(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return true;  // 空字符串是任何字符串的前缀
        }

        TrieNode node = root;

        // 遍历前缀的每个字符
        for (char c : prefix.toCharArray()) {
            int index = c - 'a';

            // 如果该位置没有节点，说明不存在该前缀
            if (node.children[index] == null) {
                return false;
            }

            // 移动到下一个节点
            node = node.children[index];
        }

        // 前缀路径存在
        return true;
    }

    /**
     * 删除 Trie 树中的一个单词
     * 时间复杂度：O(m)，m 为单词长度
     * 空间复杂度：O(m) 递归调用栈
     *
     * @param word 要删除的单词
     * @return 如果单词存在并成功删除返回 true，否则返回 false
     */
    public boolean delete(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        return deleteHelper(root, word, 0);
    }

    /**
     * 递归删除单词的辅助方法
     * 采用后序遍历的方式，从后往前删除节点
     *
     * @param node 当前节点
     * @param word 要删除的单词
     * @param index 当前处理的字符索引
     * @return 如果该节点应该被删除返回 true，否则返回 false
     */
    private boolean deleteHelper(TrieNode node, String word, int index) {
        // 递归基础情况：已经处理完所有字符
        if (index == word.length()) {
            // 如果该节点不是单词的结尾，说明单词不存在
            if (!node.isEndOfWord) {
                return false;
            }

            // 取消标记，表示该单词已被删除
            node.isEndOfWord = false;

            // 如果该节点没有子节点，可以删除该节点
            return node.children.length == 0 || isAllChildrenNull(node);
        }

        char c = word.charAt(index);
        int childIndex = c - 'a';

        // 检查子节点是否存在
        if (node.children[childIndex] == null) {
            return false;  // 单词不存在
        }

        TrieNode child = node.children[childIndex];

        // 递归删除子节点
        boolean shouldDeleteChild = deleteHelper(child, word, index + 1);

        if (shouldDeleteChild) {
            // 删除该子节点
            node.children[childIndex] = null;

            // 如果当前节点没有其他子节点且不是某个单词的结尾，该节点也可以被删除
            return isAllChildrenNull(node) && !node.isEndOfWord;
        }

        return false;
    }

    /**
     * 检查节点的所有子节点是否都为 null
     *
     * @param node 要检查的节点
     * @return 如果所有子节点都为 null 返回 true，否则返回 false
     */
    private boolean isAllChildrenNull(TrieNode node) {
        for (TrieNode child : node.children) {
            if (child != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取 Trie 树中所有以给定前缀开头的单词
     * 时间复杂度：O(N)，N 为树中所有节点数
     * 空间复杂度：O(M)，M 为结果列表中单词的数量
     *
     * @param prefix 给定的前缀
     * @return 所有以该前缀开头的单词列表
     */
    public java.util.List<String> getWordsWithPrefix(String prefix) {
        java.util.List<String> result = new java.util.ArrayList<>();

        if (prefix == null) {
            return result;
        }

        TrieNode node = root;

        // 遍历到前缀的最后一个节点
        for (char c : prefix.toCharArray()) {
            int index = c - 'a';

            if (node.children[index] == null) {
                return result;  // 前缀不存在
            }

            node = node.children[index];
        }

        // 从该节点开始深度优先搜索，收集所有单词
        dfs(node, prefix, result);

        return result;
    }

    /**
     * 深度优先搜索辅助方法，收集所有以当前路径开头的单词
     *
     * @param node 当前节点
     * @param path 当前路径
     * @param result 结果列表
     */
    private void dfs(TrieNode node, String path, java.util.List<String> result) {
        // 如果当前节点是单词结尾，加入结果
        if (node.isEndOfWord) {
            result.add(path);
        }

        // 遍历所有子节点
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                // 递归遍历子节点，添加对应的字符
                dfs(node.children[i], path + (char)('a' + i), result);
            }
        }
    }

    /**
     * 获取 Trie 树中所有的单词
     *
     * @return 所有单词列表
     */
    public java.util.List<String> getAllWords() {
        return getWordsWithPrefix("");
    }

    /**
     * 获取 Trie 树中单词的数量
     *
     * @return 单词数量
     */
    public int size() {
        return countWords(root);
    }

    /**
     * 计算节点及其子树中的单词数量
     *
     * @param node 当前节点
     * @return 单词数量
     */
    private int countWords(TrieNode node) {
        if (node == null) {
            return 0;
        }

        int count = node.isEndOfWord ? 1 : 0;

        // 遍历所有子节点
        for (TrieNode child : node.children) {
            count += countWords(child);
        }

        return count;
    }

    /**
     * 检查 Trie 树是否为空（除了根节点）
     *
     * @return 如果 Trie 树为空返回 true，否则返回 false
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * 清空 Trie 树
     */
    public void clear() {
        root = new TrieNode();
    }
}
