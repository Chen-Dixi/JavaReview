package dixi.bupt.datastructure.advanced.filter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

/**
 * 布隆过滤器。用来辅助判断数据不在集合中
 * @author chendixi
 * Created on 2025-03-02
 */
public class Bloom {
    /**
     * 位数组大小（默认为 1MB = 8388608 位）
     */
    private final int bitSize;

    /**
     * 位数组
     */
    private final BitSet bitSet;

    /**
     * 哈希函数个数
     */
    private final int hashFunctionCount;

    /**
     * MD5 消息摘要算法
     */
    private static final String HASH_ALGORITHM = "MD5";

    /**
     * 构造函数，使用默认参数
     * 默认位数组大小：1MB（8388608 位）
     * 默认哈希函数个数：8
     */
    public Bloom() {
        this(8388608, 8);
    }

    /**
     * 构造函数
     *
     * @param bitSize 位数组大小（以位为单位）
     * @param hashFunctionCount 哈希函数个数
     */
    public Bloom(int bitSize, int hashFunctionCount) {
        if (bitSize <= 0 || hashFunctionCount <= 0) {
            throw new IllegalArgumentException("bitSize 和 hashFunctionCount 必须大于 0");
        }
        this.bitSize = bitSize;
        this.hashFunctionCount = hashFunctionCount;
        this.bitSet = new BitSet(bitSize);
    }

    /**
     * 添加元素到布隆过滤器
     *
     * @param element 要添加的元素
     */
    public void add(String element) {
        if (element == null) {
            return;
        }

        int[] hashValues = getHashValues(element);
        for (int hashValue : hashValues) {
            bitSet.set(hashValue);
        }
    }

    /**
     * 判断元素是否可能存在于集合中
     *
     * @param element 要查询的元素
     * @return 如果返回 false，元素一定不存在；如果返回 true，元素可能存在
     */
    public boolean contains(String element) {
        if (element == null) {
            return false;
        }

        int[] hashValues = getHashValues(element);
        for (int hashValue : hashValues) {
            if (!bitSet.get(hashValue)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取元素的多个哈希值
     *
     * @param element 元素
     * @return 哈希值数组
     */
    private int[] getHashValues(String element) {
        int[] hashValues = new int[hashFunctionCount];
        byte[] elementBytes = element.getBytes(StandardCharsets.UTF_8);

        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(elementBytes);
            byte[] digest = md.digest();

            // 使用不同的字节组合生成多个哈希值
            for (int i = 0; i < hashFunctionCount; i++) {
                int hashValue = 0;
                // 使用 digest 中的字节两两组合计算哈希值
                for (int j = 0; j < digest.length - 1; j += 2) {
                    hashValue = hashValue * 31 + (digest[(j + i) % digest.length] & 0xFF);
                }
                hashValues[i] = (hashValue & Integer.MAX_VALUE) % bitSize;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 算法不可用", e);
        }

        return hashValues;
    }

    /**
     * 获取布隆过滤器的位数组大小
     *
     * @return 位数组大小
     */
    public int getBitSize() {
        return bitSize;
    }

    /**
     * 获取哈希函数个数
     *
     * @return 哈希函数个数
     */
    public int getHashFunctionCount() {
        return hashFunctionCount;
    }

    /**
     * 获取已设置为 1 的位数
     *
     * @return 已设置为 1 的位数
     */
    public int getCardinality() {
        return bitSet.cardinality();
    }

    /**
     * 清空布隆过滤器
     */
    public void clear() {
        bitSet.clear();
    }
}
