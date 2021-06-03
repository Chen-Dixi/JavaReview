package dixi.bupt.loadclass;

public class NotInitialization {
    public static void main(String[] args){
        //System.out.println(SubClass.value); //这里不会触发子类SubClass的初始化。
        //SuperClass [] sca = new SuperClass[10]; //
        //System.out.println(ConstantClass.value); // 不会触发ConstantClass的类初始化。

        /* SuperClass 只被初始化一次
        SubClass subClass = new SubClass();
        SubClassNew subClassNew = new SubClassNew();
        */

    }
}
