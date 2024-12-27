package dixi.bupt.classloader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 2024-12-08
 */
public class ClassLoaderTest {

    public static void main(String[] args) throws Exception {
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws  ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if (is == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };

        Object obj = myLoader.loadClass("dixi.bupt.classloader.ClassLoaderTest").getDeclaredConstructor().newInstance();
        System.out.println(obj.getClass());
        System.out.println(obj instanceof dixi.bupt.classloader.ClassLoaderTest);
    }
}
