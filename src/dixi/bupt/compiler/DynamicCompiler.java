package dixi.bupt.compiler;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * 动态Java编译器
 *
 * 关键JVM原理：
 * 1. 使用javax.tools.JavaCompiler API（JDK内置编译器）
 * 2. 自定义JavaFileObject实现内存中的源码和字节码
 * 3. 编译后的字节码直接注册到ClassLoader
 */
public class DynamicCompiler {

    private final JavaCompiler compiler;
    private final MemoryClassLoader classLoader;

    public DynamicCompiler(MemoryClassLoader classLoader) {
        // 获取JDK内置的Java编译器
        this.compiler = ToolProvider.getSystemJavaCompiler();
        this.classLoader = classLoader;

        if (this.compiler == null) {
            throw new IllegalStateException("无法获取Java编译器，请确保使用JDK运行而非JRE");
        }
    }

    /**
     * 编译Java源码字符串
     *
     * @param className 全限定类名
     * @param sourceCode Java源码
     * @return 编译是否成功
     */
    public boolean compile(String className, String sourceCode) {
        // 收集编译诊断信息
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        // 创建标准文件管理器
        StandardJavaFileManager standardFileManager =
                compiler.getStandardFileManager(diagnostics, null, null);

        // 使用自定义的文件管理器（输出到内存）
        MemoryFileManager fileManager =
                new MemoryFileManager(standardFileManager, classLoader);

        // 创建源码对象（从字符串）
        List<JavaFileObject> sourceFiles = new ArrayList<>();
        sourceFiles.add(new StringSourceFileObject(className, sourceCode));

        // 编译选项
        List<String> options = new ArrayList<>();
        options.add("-encoding");
        options.add("UTF-8");

        // 执行编译任务
        JavaCompiler.CompilationTask task = compiler.getTask(
                null,           // 输出流（null表示使用System.err）
                fileManager,    // 文件管理器
                diagnostics,    // 诊断收集器
                options,        // 编译选项
                null,           // 要编译的类名（null表示编译所有）
                sourceFiles     // 源文件
        );

        boolean success = task.call();

        if (!success) {
            System.err.println("[Compiler] 编译失败:");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.err.println("  " + diagnostic);
            }
        } else {
            // 关键：编译成功后，将字节码注册到ClassLoader
            for (Map.Entry<String, MemoryClassFileObject> entry : fileManager.classFiles.entrySet()) {
                String compiledClassName = entry.getKey();
                byte[] bytes = entry.getValue().getBytes();
                if (bytes != null) {
                    classLoader.registerClass(compiledClassName, bytes);
                    System.out.println("[Compiler] 注册类: " + compiledClassName);
                }
            }
            System.out.println("[Compiler] 编译成功: " + className);
        }

        return success;
    }

    /**
     * 内存中的Java源码文件对象
     * 将字符串形式的源码包装成JavaFileObject
     */
    static class StringSourceFileObject extends SimpleJavaFileObject {
        private final String sourceCode;

        protected StringSourceFileObject(String className, String sourceCode) {
            // 使用URI scheme "string" 表示内存中的源码
            super(URI.create("string:///" + className.replace('.', '/') + ".java"), Kind.SOURCE);
            this.sourceCode = sourceCode;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return sourceCode;
        }
    }

    /**
     * 内存中的字节码文件对象
     * 编译器输出字节码到此对象
     */
    static class MemoryClassFileObject extends SimpleJavaFileObject {
        private final String className;
        private byte[] bytes;

        protected MemoryClassFileObject(String className) {
            super(URI.create("bytes:///" + className.replace('.', '/') + ".class"), Kind.CLASS);
            this.className = className;
        }

        @Override
        public OutputStream openOutputStream() {
            return new ByteArrayOutputStream() {
                @Override
                public void close() throws IOException {
                    super.close();
                    bytes = this.toByteArray();
                }
            };
        }

        public String getClassName() {
            return className;
        }

        public byte[] getBytes() {
            return bytes;
        }
    }

    /**
     * 自定义文件管理器
     * 拦截编译器输出，将字节码直接写入内存
     */
    static class MemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final MemoryClassLoader classLoader;
        private final Map<String, MemoryClassFileObject> classFiles = new HashMap<>();

        protected MemoryFileManager(StandardJavaFileManager fileManager, MemoryClassLoader classLoader) {
            super(fileManager);
            this.classLoader = classLoader;
        }

        /**
         * 关键方法：编译器输出class文件时调用
         * 我们将字节码保存到内存，并注册到ClassLoader
         */
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className,
                                                   JavaFileObject.Kind kind, FileObject sibling) throws IOException {

            if (kind == JavaFileObject.Kind.CLASS) {
                MemoryClassFileObject classFile = new MemoryClassFileObject(className);
                classFiles.put(className, classFile);

                // 注册到ClassLoader（稍后在编译完成后获取字节码）
                return classFile;
            }

            return super.getJavaFileForOutput(location, className, kind, sibling);
        }

        /**
         * 提供类路径给编译器
         * 让编译器能找到引用的类
         */
        @Override
        public ClassLoader getClassLoader(Location location) {
            return classLoader;
        }
    }
}