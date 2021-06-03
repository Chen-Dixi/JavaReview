package dixi.bupt.designpattern.Adapter;

public interface Duck{
    void quack();
}

public interface Turkey{
    void gobble();
}

public class WildTurkey implements Turkey{
    @Override
    public void gobble(){
        System.out.println("gobble");
    }
}


public class TurkeyAdapter implements Duck{
    Turkey turkey;

    public TurkeyAdapter(Turkey turkey)
    {
        this.turkey = turkey;
    }

    @Override
    public void quack()
    {
        this.turkey.gobble();//在适配器实现B接口方法函数里面调用实例的A接口方法。
    }
}

public class Client{
    public static void main(String[] args){
        Turckey turckey = new WildTurkey();
        Duck duck = new TurkeyAdapter(turckey);
        duck.gobble();
    }
}
