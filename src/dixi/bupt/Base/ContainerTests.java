package dixi.bupt.Base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author chendixi
 * Created on 2023-03-20
 */
public class ContainerTests {

    @Test
    public void singletonListTest() {
        List<Object> objects = Collections.singletonList(null);
        Assert.assertTrue(CollectionUtils.isNotEmpty(objects));
        for (Object obj : objects) {
            if (null == obj) {
                System.out.println("null object");
            }
        }
    }

    @Test
    public void testPutNullToMap() {
        Map<String, String> mapContainer = new HashMap<>();
    }

    @Test
    public void testEmptyListStreamAllMatch() {
        List<String> testList = new ArrayList<>();
        boolean result = testList.stream().allMatch(s -> true);
        System.out.println(result);
    }

    @Test
    public void testEmptyListStreamAnyMatch() {
        List<String> testList = new ArrayList<>();
        boolean result = testList.stream().anyMatch(s -> true);
        System.out.println(result);
    }

    @Test
    public void testString() {
        // 测试字符串format
        int d = 13;
        String s = String.format("APP请求频率过高 %d", null);
        System.out.println(s);
    }

    @Test
    public void testEmptyListToMap() {
        List<String> empty = new ArrayList<>();
        Map<String, Object> businessInfoDegradable = empty.stream()
                .collect(Collectors.toMap(s -> s, s -> s));
        System.out.println(businessInfoDegradable);
    }

    @Test
    public void testSelect() {
        int s = true ? a() : b();
    }

    private int a() {
        System.out.println("a");
        return 1;
    }

    private int b() {
        System.out.println("b");
        return 2;
    }
}
