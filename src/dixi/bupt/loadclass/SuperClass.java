package dixi.bupt.loadclass;

public class SuperClass {
    //6种主动引用之外，所有引用类型都不会触发初始化，称为被动引用。
    static{
        System.out.println("SuperClass init!");
    }
    public static int value = 123;
}
