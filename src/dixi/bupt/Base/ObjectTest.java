package dixi.bupt.Base;

public class ObjectTest {

    /*
    hashcode

    equals

    toString

    notify

    wait

    clone，是Object的protected方法，重写的时候如果不改成public，就无法调用实例的clone方法

    getClass
     */
    static {

    }

    static{

    }
    public static class CloneExample implements Cloneable{
        private int a;
        private int b;

        @Override
        public CloneExample clone() throws CloneNotSupportedException{
            return (CloneExample)super.clone();
        }
    }

    public static void main(String[] args){
        CloneExample e1 = new CloneExample();
        try{
            CloneExample e2 = e1.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
    }
}
