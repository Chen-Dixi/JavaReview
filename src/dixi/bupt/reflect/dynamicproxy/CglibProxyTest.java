package dixi.bupt.reflect.dynamicproxy;

import java.lang.reflect.Method;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * CGLIB 动态代理示例
 * <p>
 * CGLIB vs JDK动态代理的区别：
 * 1. JDK动态代理：只能代理实现了接口的类，基于接口代理
 * 2. CGLIB：可以代理没有接口的类，基于继承代理（生成子类）
 * <p>
 * Created on 2025-10-16
 */
public class CglibProxyTest {

    // 目标类：不需要实现接口
    static class UserService {
        public void addUser(String username) {
            System.out.println("添加用户: " + username);
        }

        public String getUserInfo(Long userId) {
            return "用户信息: ID=" + userId + ", Name=张三";
        }

        public void deleteUser(Long userId) {
            System.out.println("删除用户: " + userId);
        }

        // final方法无法被代理
        public final void finalMethod() {
            System.out.println("这是final方法，无法被代理");
        }
    }

    // CGLIB方法拦截器
    static class UserServiceInterceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("=== CGLIB代理开始 ===");
            System.out.println("代理对象类型: " + obj.getClass().getName());
            System.out.println("调用方法: " + method.getName());
            System.out.println("方法参数: " + java.util.Arrays.toString(args));

            // 前置处理
            long startTime = System.currentTimeMillis();

            // 调用父类方法（目标方法）
            // 注意：这里使用proxy.invokeSuper而不是method.invoke
            // 直接调用父类方法，不使用反射
            Object result = proxy.invokeSuper(obj, args);

            // 后置处理
            long endTime = System.currentTimeMillis();
            System.out.println("方法执行时间: " + (endTime - startTime) + "ms");
            System.out.println("=== CGLIB代理结束 ===\n");

            return result;
        }
    }

    /**
     * 生成代理类较慢
     * 执行效率更高
     * 更灵活，不需要接口
     *
     * @param args
     */
    public static void main(String[] args) {
        // 设置CGLIB生成的代理类保存路径，用于查看字节码
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./cglib-generated-classes");

        System.out.println("=== CGLIB 动态代理示例 ===\n");

        // 1. 创建目标对象
        UserService target = new UserService();
        System.out.println("目标对象类型: " + target.getClass().getName());

        // 2. 创建CGLIB代理对象
        UserService proxy = createCglibProxy();
        System.out.println("代理对象类型: " + proxy.getClass().getName());
        System.out.println("代理对象父类: " + proxy.getClass().getSuperclass().getName());
        System.out.println("代理对象是否为UserService实例: " + (proxy instanceof UserService));
        System.out.println();

        // 3. 通过代理对象调用方法
        System.out.println("=== 通过代理对象调用方法 ===");
        proxy.addUser("张三");

        String userInfo = proxy.getUserInfo(123L);
        System.out.println("返回结果: " + userInfo);

        proxy.deleteUser(456L);

        // 4. 调用final方法（无法被代理）
        System.out.println("=== 调用final方法 ===");
        proxy.finalMethod();

        // 5. 直接调用目标对象方法进行对比
        System.out.println("=== 直接调用目标对象方法 ===");
        target.addUser("李四");
        System.out.println("返回结果: " + target.getUserInfo(789L));

        // 6. 分析代理对象
        analyzeProxyObject(proxy);

        System.out.println("\n=== 字节码文件生成位置 ===");
        System.out.println("代理类字节码已保存到: ./cglib-generated-classes/");
        System.out.println("可以使用javap命令查看字节码: javap -c -p 类名");
    }

    private static UserService createCglibProxy() {
        // 创建Enhancer对象
        Enhancer enhancer = new Enhancer();

        // 设置父类（目标类）
        enhancer.setSuperclass(UserService.class);

        // 设置回调（方法拦截器）
        enhancer.setCallback(new UserServiceInterceptor());

        // 创建代理对象
        return (UserService) enhancer.create();
    }

    private static void analyzeProxyObject(Object proxy) {
        System.out.println("\n=== CGLIB代理对象分析 ===");
        Class<?> proxyClass = proxy.getClass();

        System.out.println("代理类名: " + proxyClass.getName());
        System.out.println("代理类父类: " + proxyClass.getSuperclass().getName());

        System.out.println("实现的接口:");
        for (Class<?> intf : proxyClass.getInterfaces()) {
            System.out.println("  - " + intf.getName());
        }

        System.out.println("声明的方法数量: " + proxyClass.getDeclaredMethods().length);
        System.out.println("部分方法:");
        Method[] methods = proxyClass.getDeclaredMethods();
        for (int i = 0; i < Math.min(5, methods.length); i++) {
            System.out.println("  - " + methods[i].getName());
        }
        if (methods.length > 5) {
            System.out.println("  - ... 还有 " + (methods.length - 5) + " 个方法");
        }
    }
}
