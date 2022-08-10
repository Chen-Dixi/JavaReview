package dixi.bupt.loadclass;

// 被动引用的例子
public class SubClass extends SuperClass{
    static{
        System.out.println("SubClass init");
    }
}
