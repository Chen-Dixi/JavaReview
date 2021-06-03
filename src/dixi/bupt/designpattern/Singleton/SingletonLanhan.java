package dixi.bupt.designpattern.Singleton;

public class SingletonLanhan {
    private static SingletonLanhan uniqueInstance;

    public static synchronized SingletonLanhan getUniqueInstance(){
        if(uniqueInstance==null){
            uniqueInstance = new SingletonLanhan();
        }
        return uniqueInstance;
    }

}
