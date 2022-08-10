package dixi.bupt.Base;

public class DataType {
    // byte 8
    // char 16
    // short 16
    // int 32
    // float 32
    // long 64
    // double 64
    // boolean/~ JVM 在编译时期将boolean转换为int
    public static void main(String[] args){
        buffer_builder();
    }

    // 缓存池
    private static void cachePoolTest(){
        // new Integer(123)每次都会新建一个对象，不会使用缓存池中已经有的123
        // Integer.valueOf(123)会使用缓存池中的对象，多次调用会得到同一个对象的使用
        Integer x = new Integer(123);
        Integer y = new Integer(123);
        System.out.println(x == y);
        Integer z = Integer.valueOf(123);
        Integer k = Integer.valueOf(123);
        System.out.println(z == k);

        // 自动装箱，编译器会在自动装箱的时候调用valueOf()
        // 多个值相同且在缓存池范围内的Integer实例使用自动装箱创建，那么就会引用相同的对象
        Integer xx = 123;
        Integer m = 123;
        Integer n = 123;

        // 基本类型有对应的缓冲池
        // boolean true ans false;
        // short values between -128 and 127
        // int values between -128 and 127
        // char in the range \u0000 to \u007F
        // 使用这些基本类型 **对应的包装类型** 时，数值范围在缓冲池范围内，就可以直接使用缓冲池对应的对象

        // Integer的缓冲池 IntegerCache很特殊，下届-128，上届127。
        // 启动JVM的时候， -XX:AutoBoxCacheMax= <size> 来制定这个缓冲池的大小
        // JVM初始化的时候会制定 java.lang.IntegerCache.high 来制定缓冲池大小。
    }

    // String
    private static void StringTest(){
        // 内部 private final byte[] value; byte不可变
        // 不可变的好处
        String a = "abc";
        String b = "abc";
        System.out.println(a == b);
    }

    // StringBuffer StringBuilder String
    private static void buffer_builder(){
        // # 可变性
        // String 不可变
        // StringBuilder, StringBuffer可变

        // # 线程安全
        // String 是线程安全的
        // StringBuilder 不是线程安全的
        // StringBuffer 是线程安全的，内部使用synchronized
        StringBuilder sb = new StringBuilder();
        StringBuffer sbf = new StringBuffer();
    }

    private static void String_Pool_Test(){

    }
}
