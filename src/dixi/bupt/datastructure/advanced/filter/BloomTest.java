package dixi.bupt.datastructure.advanced.filter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 布隆过滤器测试类
 * @author chendixi
 * Created on 2025-01-19
 */
public class BloomTest {

    @Test
    public void testBasicAddAndContains() {
        Bloom bloom = new Bloom(10000, 3);

        // 添加元素
        bloom.add("apple");
        bloom.add("banana");
        bloom.add("orange");

        // 验证添加的元素能被查询到
        assertTrue("苹果应该在过滤器中", bloom.contains("apple"));
        assertTrue("香蕉应该在过滤器中", bloom.contains("banana"));
        assertTrue("橙子应该在过滤器中", bloom.contains("orange"));
    }

    @Test
    public void testNotContains() {
        Bloom bloom = new Bloom(10000, 3);

        // 添加元素
        bloom.add("apple");
        bloom.add("banana");

        // 验证未添加的元素不被查询到
        // 注意：布隆过滤器可能存在假阳性，但不会有假阴性
        assertFalse("西瓜不应该在过滤器中", bloom.contains("watermelon"));
        assertFalse("葡萄不应该在过滤器中", bloom.contains("grape"));
    }

    @Test
    public void testNullElement() {
        Bloom bloom = new Bloom(10000, 3);

        // null 不应该被添加
        bloom.add(null);

        // null 查询应该返回 false
        assertFalse("null 应该返回 false", bloom.contains(null));
    }

    @Test
    public void testDefaultConstructor() {
        Bloom bloom = new Bloom();

        // 验证默认参数
        assertEquals("默认位数组大小应为 8388608", 8388608, bloom.getBitSize());
        assertEquals("默认哈希函数个数应为 8", 8, bloom.getHashFunctionCount());
    }

    @Test
    public void testCustomConstructor() {
        int bitSize = 100000;
        int hashCount = 5;
        Bloom bloom = new Bloom(bitSize, hashCount);

        assertEquals("位数组大小应为 " + bitSize, bitSize, bloom.getBitSize());
        assertEquals("哈希函数个数应为 " + hashCount, hashCount, bloom.getHashFunctionCount());
    }

    @Test
    public void testInvalidConstructor() {
        // 测试非法参数
        try {
            new Bloom(-1, 3);
            fail("应该抛出 IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue("异常信息应包含参数验证", e.getMessage().contains("必须大于 0"));
        }

        try {
            new Bloom(1000, 0);
            fail("应该抛出 IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue("异常信息应包含参数验证", e.getMessage().contains("必须大于 0"));
        }
    }

    @Test
    public void testCardinality() {
        Bloom bloom = new Bloom(10000, 3);

        int initialCardinality = bloom.getCardinality();

        bloom.add("test1");
        bloom.add("test2");
        bloom.add("test3");

        // 添加元素后，设置为 1 的位数应该增加
        int afterCardinality = bloom.getCardinality();
        assertTrue("添加元素后，设置为 1 的位数应该增加", afterCardinality > initialCardinality);
    }

    @Test
    public void testClear() {
        Bloom bloom = new Bloom(10000, 3);

        bloom.add("test1");
        bloom.add("test2");

        assertTrue("清空前应该能查询到元素", bloom.contains("test1"));

        bloom.clear();

        // 清空后，原来的元素应该查询不到
        // 注意：清空是重置位数组，所以所有查询都会返回 false
        assertFalse("清空后查询应该返回 false", bloom.contains("test1"));
        assertFalse("清空后查询应该返回 false", bloom.contains("test2"));
        assertEquals("清空后，设置为 1 的位数应为 0", 0, bloom.getCardinality());
    }

    @Test
    public void testLargeScaleAddition() {
        Bloom bloom = new Bloom(100000, 5);

        // 添加大量元素
        int elementCount = 1000;
        for (int i = 0; i < elementCount; i++) {
            bloom.add("element_" + i);
        }

        // 验证所有添加的元素都能被查询到
        for (int i = 0; i < elementCount; i++) {
            assertTrue("元素 " + i + " 应该在过滤器中", bloom.contains("element_" + i));
        }
    }

    @Test
    public void testDifferentDataTypes() {
        Bloom bloom = new Bloom(10000, 3);

        // 添加不同类型的数据
        bloom.add("string_value");
        bloom.add("123");
        bloom.add("user@example.com");
        bloom.add("2025-01-19");

        // 验证都能被查询到
        assertTrue(bloom.contains("string_value"));
        assertTrue(bloom.contains("123"));
        assertTrue(bloom.contains("user@example.com"));
        assertTrue(bloom.contains("2025-01-19"));

        // 验证未添加的数据查询不到
        assertFalse(bloom.contains("456"));
        assertFalse(bloom.contains("unknown@example.com"));
    }

    @Test
    public void testDuplicateAddition() {
        Bloom bloom = new Bloom(10000, 3);

        int cardinalityBefore = bloom.getCardinality();

        bloom.add("duplicate");
        int cardinalityAfterFirst = bloom.getCardinality();

        bloom.add("duplicate");
        int cardinalityAfterSecond = bloom.getCardinality();

        // 重复添加同一元素，位数组的变化应该相同或无变化
        assertEquals("重复添加同一元素，位数组不应该增加", cardinalityAfterFirst, cardinalityAfterSecond);
    }

    @Test
    public void testEmptyStringElement() {
        Bloom bloom = new Bloom(10000, 3);

        bloom.add("");

        assertTrue("空字符串应该能被添加和查询", bloom.contains(""));
    }

    @Test
    public void testSpecialCharacters() {
        Bloom bloom = new Bloom(10000, 3);

        String specialStr = "!@#$%^&*()_+-=[]{}|;:'\",.<>?/~`";
        bloom.add(specialStr);

        assertTrue("特殊字符字符串应该能被处理", bloom.contains(specialStr));
    }

    @Test
    public void testUnicodeCharacters() {
        Bloom bloom = new Bloom(10000, 3);

        bloom.add("你好世界");
        bloom.add("こんにちは");
        bloom.add("مرحبا بالعالم");

        assertTrue("中文应该能被处理", bloom.contains("你好世界"));
        assertTrue("日文应该能被处理", bloom.contains("こんにちは"));
        assertTrue("阿拉伯文应该能被处理", bloom.contains("مرحبا بالعالم"));
    }
}
