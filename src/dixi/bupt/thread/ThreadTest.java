package dixi.bupt.thread;

public class ThreadTest implements Runnable{

    public static  void main(String[] args){
        new ThreadTest();
    }

    public ThreadTest (){
        ThreadTest task = new ThreadTest();
        new Thread(task).start();
    }

    public void run(){
        System.out.print("asd");
    }


}
