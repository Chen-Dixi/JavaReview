package dixi.bupt.reflect.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK 动态代理示例
 * <p>
 * 核心概念：
 * 1. 目标对象（Target Object）：被代理的真实对象，实现了业务逻辑
 * 2. 代理对象（Proxy Object）：由JDK动态生成的对象，实现了相同的接口
 * 3. 代理处理器（InvocationHandler）：定义代理对象如何处理方法调用
 * <p>
 * Created on 2025-10-16
 */
public class DynamicProxyTest {

    // 定义业务接口
    interface IHello {
        void sayHello(String name);

        String getMessage();
    }

    // 目标对象：实现业务逻辑的真实对象
    static class HelloImpl implements IHello {
        @Override
        public void sayHello(String name) {
            System.out.println("Hello, " + name + "!");
        }

        @Override
        public String getMessage() {
            return "This is a message from HelloImpl";
        }
    }

    // 代理处理器：定义代理逻辑
    static class HelloInvocationHandler implements InvocationHandler {
        private final Object target; // 目标对象

        public HelloInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("=== 代理开始 ===");
            System.out.println("调用方法: " + method.getName());
            System.out.println("方法参数: " + java.util.Arrays.toString(args));

            // 前置处理
            long startTime = System.currentTimeMillis();

            // 调用目标对象的方法
            Object result = method.invoke(target, args);

            // 后置处理
            long endTime = System.currentTimeMillis();
            System.out.println("方法执行时间: " + (endTime - startTime) + "ms");
            System.out.println("=== 代理结束 ===\n");

            return result;
        }
    }

    /**
     * 必须有接口（就像翻译必须懂双方语言）
     * 代理和被代理对象都实现同一接口
     * 在运行时动态创建代理类
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("=== JDK 动态代理示例 ===\n");


        // 1. 创建目标对象
        IHello target = new HelloImpl();
        System.out.println("目标对象类型: " + target.getClass().getName());

        // 2. 创建代理处理器
        HelloInvocationHandler handler = new HelloInvocationHandler(target);

        // 3. 创建代理对象
        // 进行过 验证、优化、缓存、
        IHello proxy = (IHello) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),  // 类加载器
                target.getClass().getInterfaces(),   // 接口数组
                handler                              // 代理处理器
        );

        System.out.println("代理对象类型: " + proxy.getClass().getName());
        System.out.println("代理对象是否为IHello实例: " + (proxy instanceof IHello));
        System.out.println("代理对象是否为HelloImpl实例: " + (proxy instanceof HelloImpl));
        System.out.println();

        // 4. 通过代理对象调用方法
        System.out.println("=== 通过代理对象调用方法 ===");
        proxy.sayHello("张三");

        String message = proxy.getMessage();
        System.out.println("返回结果: " + message);

        // 5. 直接调用目标对象方法进行对比
        System.out.println("=== 直接调用目标对象方法 ===");
        target.sayHello("李四");
        System.out.println("返回结果: " + target.getMessage());

        // 6. 分析代理对象的特性
        analyzeProxyObject(proxy);
    }

    private static void analyzeProxyObject(Object proxy) {
        System.out.println("\n=== 代理对象分析 ===");
        Class<?> proxyClass = proxy.getClass();

        System.out.println("代理类名: " + proxyClass.getName());
        System.out.println("代理类父类: " + proxyClass.getSuperclass().getName());

        System.out.println("实现的接口:");
        for (Class<?> intf : proxyClass.getInterfaces()) {
            System.out.println("  - " + intf.getName());
        }

        System.out.println("声明的方法:");
        for (Method method : proxyClass.getDeclaredMethods()) {
            System.out.println("  - " + method.getName());
        }
    }
}
