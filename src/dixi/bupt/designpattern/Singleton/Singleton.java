package dixi.bupt.designpattern.Singleton;

public class Singleton {

    //私有静态变量
    //线程不安全
    private static Singleton uniqueInstance;

    private Singleton() { //私有初始化函数
    }

    //公有 静态函数获取
    public static Singleton getUniqueInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Singleton();
        }
        return uniqueInstance;
    }
}

