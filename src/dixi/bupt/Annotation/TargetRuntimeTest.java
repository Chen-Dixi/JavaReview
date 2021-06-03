package dixi.bupt.Annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class TargetRuntimeTest {

    @Tuple({1,2})
    public String name = "123";
    @Tuple({3,2})
    public String address = "Chognqing";
    public static void main(String[] args) {
        Field[] fields = TargetRuntimeTest.class.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        for(Field field : fields) {
            Tuple tuple = field.getAnnotation(Tuple.class);
            if(tuple!=null) {
                sb.append(Modifier.toString(field.getModifiers())).append(" ").append(field.getType().getSimpleName()).append(" ").append(field.getName()).append("\n");
                sb.append("注解值：").append(Arrays.toString(tuple.value())).append("\n");

            }
        }
        System.out.println(sb);
    }
}
