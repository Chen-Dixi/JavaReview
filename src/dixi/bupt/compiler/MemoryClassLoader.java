package dixi.bupt.compiler;


import java.util.HashMap;
import java.util.Map;

/**
 * 自定义ClassLoader，从内存中加载类
 * <p>
 * 关键JVM原理：
 * 1. 继承ClassLoader
 * 2. 重写findClass方法
 * 3. 使用defineClass将字节码转为Class对象
 */
public class MemoryClassLoader extends ClassLoader {

    // 存储编译后的字节码：类名 -> 字节码
    private final Map<String, byte[]> classBytes = new HashMap<>();

    public MemoryClassLoader() {
        // 使用当前线程的ContextClassLoader作为父加载器
        super(Thread.currentThread().getContextClassLoader());
    }

    /**
     * 注册编译后的字节码
     */
    public void registerClass(String className, byte[] bytes) {
        classBytes.put(className, bytes);
    }

    /**
     * 核心方法：从内存中查找并加载类
     * <p>
     * JVM调用过程：
     * 1. 当调用Class.forName()或加载类时，JVM会先调用父加载器
     * 2. 父加载器找不到时，会调用此方法
     * 3. defineClass()是JVM本地方法，将字节码转为Class对象
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classBytes.get(name);
        if (bytes == null) {
            // 不是我们编译的类，交给父加载器处理
            return super.findClass(name);
        }

        // 检查是否已加载
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // 关键：使用defineClass将字节码转为Class对象
        System.out.println("[ClassLoader] 正在加载类: " + name);
        return defineClass(name, bytes, 0, bytes.length);
    }

    /**
     * 获取所有已注册的类
     */
    public Map<String, Class<?>> getAllClasses() throws ClassNotFoundException {
        Map<String, Class<?>> classes = new HashMap<>();
        for (String className : classBytes.keySet()) {
            classes.put(className, loadClass(className));
        }
        return classes;
    }
}
