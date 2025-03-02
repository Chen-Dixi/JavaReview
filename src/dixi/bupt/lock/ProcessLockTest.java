package dixi.bupt.lock;

public class ProcessLockTest implements Runnable {
    private static int i = 0;

    private synchronized void add(){
        i++;
    }

    @Override
    public void run(){
        for(int i=0;i<10000000;i++){
            add();
        }
    }

    public static void main(String[] args) throws InterruptedException{
        Thread thread1 = new Thread(new ProcessLockTest());
        Thread thread2 = new Thread(new ProcessLockTest());

        thread1.run();
        thread2.run();

        thread1.join();
        thread2.join();

        System.out.println(i);
    }
}
