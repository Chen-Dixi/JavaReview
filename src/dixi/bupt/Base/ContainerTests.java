package dixi.bupt.Base;

import java.util.Collections;
import java.util.List;

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
        for(Object obj : objects) {
            if (null == obj) {
                System.out.println("null object");
            }
        }
    }
}
