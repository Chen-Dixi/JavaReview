package dixi.bupt.compiler;

import java.lang.reflect.Method;

/**
 * 演示ClassLoader动态编译运行技术的主程序
 * <p>
 * 运行步骤：
 * 1. 创建MemoryClassLoader（自定义类加载器）
 * 2. 创建DynamicCompiler（动态编译器）
 * 3. 编译Java源码字符串 → 字节码
 * 4. 字节码注册到ClassLoader
 * 5. ClassLoader加载类
 * 6. 反射创建实例并调用方法
 * <p>
 * 运行命令：
 * javac demo/*.java && java demo.Main
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("========== 动态编译运行Demo ==========\n");

        // Step 1: 创建自定义ClassLoader
        System.out.println("[Step 1] 创建MemoryClassLoader");
        MemoryClassLoader classLoader = new MemoryClassLoader();

        // Step 2: 创建动态编译器
        System.out.println("[Step 2] 创建DynamicCompiler");
        DynamicCompiler compiler = new DynamicCompiler(classLoader);

        // Step 3: 准备要编译的Java源码
        String className = "demo.HelloGreeter";
        String sourceCode = "package demo;\n"
                + "\n"
                + "public class HelloGreeter implements dixi.bupt.compiler.Greeter {\n"
                + "    @Override\n"
                + "    public String greet(String name) {\n"
                + "        return \"你好, \" + name + \"! 我是动态编译的类！\";\n"
                + "    }\n"
                + "}";


        System.out.println("[Step 3] 准备编译源码:");
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println(sourceCode.lines().map(line -> "│ " + line).reduce("", (a, b) -> a + "\n" + b));
        System.out.println("└────────────────────────────────────────┘\n");

        // Step 4: 编译源码
        System.out.println("[Step 4] 执行编译...");
        boolean success = compiler.compile(className, sourceCode);

        if (!success) {
            System.err.println("编译失败！");
            return;
        }

        // Step 5: 切换线程的ContextClassLoader（模拟Container的作用）
        System.out.println("\n[Step 5] 切换ContextClassLoader");
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            // Step 6: 加载并执行编译后的类
            System.out.println("[Step 6] 加载并执行类\n");

            // 方式1：使用ClassLoader加载
            Class<?> clazz = classLoader.loadClass(className);
            System.out.println("[ClassLoader] 类加载成功: " + clazz.getName());
            System.out.println("[ClassLoader] 类加载器: " + clazz.getClassLoader());

            // 创建实例
            Object instance = clazz.getDeclaredConstructor().newInstance();
            System.out.println("[反射] 创建实例成功: " + instance);

            // 调用方法
            Method method = clazz.getMethod("greet", String.class);
            String result = (String) method.invoke(instance, "张三");
            System.out.println("[反射] 方法调用结果: " + result);

            // 方式2：通过接口调用（更优雅）
            System.out.println("\n--- 通过接口调用 ---");
            Greeter greeter = (Greeter) instance;
            System.out.println("接口调用结果: " + greeter.greet("李四"));

        } finally {
            // 恢复原来的ContextClassLoader
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            System.out.println("\n[Step 7] 恢复原ContextClassLoader");
        }

        // 演示：编译多个类并隔离
        System.out.println("\n========== 演示类隔离 ==========\n");
        demonstrateClassIsolation();

        System.out.println("\n========== Demo完成 ==========");
    }

    /**
     * 演示类隔离：同一个类名，不同版本
     */
    private static void demonstrateClassIsolation() throws Exception {
        // 创建两个独立的ClassLoader
        MemoryClassLoader loader1 = new MemoryClassLoader();
        MemoryClassLoader loader2 = new MemoryClassLoader();

        DynamicCompiler compiler1 = new DynamicCompiler(loader1);
        DynamicCompiler compiler2 = new DynamicCompiler(loader2);

        // 相同类名，不同实现
        String className = "demo.VersionDemo";

        String version1Code = "package demo;\n"
                + "public class VersionDemo {\n"
                + "    public String getVersion() { return \"v1.0\"; }\n"
                + "}";

        String version2Code = "package demo;\n"
                + "public class VersionDemo {\n"
                + "    public String getVersion() { return \"v2.0\"; }\n"
                + "}";

        compiler1.compile(className, version1Code);
        compiler2.compile(className, version2Code);

        // 加载两个版本
        Class<?> class1 = loader1.loadClass(className);
        Class<?> class2 = loader2.loadClass(className);

        // 创建实例并调用
        Object obj1 = class1.getDeclaredConstructor().newInstance();
        Object obj2 = class2.getDeclaredConstructor().newInstance();

        Method method1 = class1.getMethod("getVersion");
        Method method2 = class2.getMethod("getVersion");

        String v1 = (String) method1.invoke(obj1);
        String v2 = (String) method2.invoke(obj2);

        System.out.println("ClassLoader 1 加载的类版本: " + v1);
        System.out.println("ClassLoader 2 加载的类版本: " + v2);

        // 关键：同一个类名，但是不同的Class对象
        System.out.println("\n类隔离验证:");
        System.out.println("class1 == class2 ? " + class1.equals(class2));
        System.out.println("class1 ClassLoader: " + class1.getClassLoader());
        System.out.println("class2 ClassLoader: " + class2.getClassLoader());
    }
}