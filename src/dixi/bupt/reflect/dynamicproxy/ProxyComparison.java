package dixi.bupt.reflect.dynamicproxy;

/**
 * 动态代理对比分析
 * <p>
 * 本类用于对比JDK动态代理和CGLIB动态代理的区别和使用场景
 * <p>
 * Created on 2025-10-16
 */
public class ProxyComparison {

    public static void main(String[] args) {
        System.out.println("=== 动态代理对比分析 ===\n");

        printComparison();

        System.out.println("\n=== 运行JDK动态代理示例 ===");
        DynamicProxyTest.main(args);

        System.out.println("\n=== 运行CGLIB动态代理示例 ===");
        CglibProxyTest.main(args);
    }

    private static void printComparison() {
        System.out.println("JDK动态代理 vs CGLIB动态代理对比：\n");

        System.out.println("1. 实现原理：");
        System.out.println("   JDK动态代理：基于接口，使用反射机制生成代理类");
        System.out.println("   CGLIB：基于继承，使用ASM字节码生成框架生成子类\n");

        System.out.println("2. 使用条件：");
        System.out.println("   JDK动态代理：目标类必须实现接口");
        System.out.println("   CGLIB：目标类不需要实现接口，但不能是final类\n");

        System.out.println("3. 性能对比：");
        System.out.println("   JDK动态代理：创建代理对象速度快，方法调用相对较慢");
        System.out.println("   CGLIB：创建代理对象速度慢，方法调用相对较快\n");

        System.out.println("4. 限制：");
        System.out.println("   JDK动态代理：只能代理接口方法");
        System.out.println("   CGLIB：不能代理final方法和static方法\n");

        System.out.println("5. 使用场景：");
        System.out.println("   JDK动态代理：Spring AOP中对实现了接口的Bean进行代理");
        System.out.println("   CGLIB：Spring AOP中对没有实现接口的Bean进行代理\n");

        System.out.println("6. 代理对象特点：");
        System.out.println("   JDK动态代理：代理对象与目标对象实现相同接口，但不是同一个类");
        System.out.println("   CGLIB：代理对象是目标对象的子类，instanceof关系成立\n");
    }
}
