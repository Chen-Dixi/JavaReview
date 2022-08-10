package dixi.bupt.designpattern.Singleton;

public class SingletonShuangChong {
    // 双重校验锁
    private static volatile SingletonShuangChong uniqueInstance;

    public static SingletonShuangChong getUniqueInstance(){
        if(uniqueInstance==null){
            synchronized (SingletonShuangChong.class){
                if(uniqueInstance==null){
                    uniqueInstance = new SingletonShuangChong();
                }
            }
        }
        return uniqueInstance;
    }
}
