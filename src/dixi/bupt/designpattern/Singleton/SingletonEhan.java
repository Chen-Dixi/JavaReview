package dixi.bupt.designpattern.Singleton;

// 饿汉式。线程安全。
public class SingletonEhan {
    //直接实例化的方式丢失了延迟实例化带来的节约资源的好处。
    private static SingletonEhan uniqueInstance = new SingletonEhan();

    private SingletonEhan(){

    }

    public static SingletonEhan getUniqueInstance(){
        return uniqueInstance;
    }
}
