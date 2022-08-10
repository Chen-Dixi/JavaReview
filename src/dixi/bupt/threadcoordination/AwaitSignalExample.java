package dixi.bupt.threadcoordination;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AwaitSignalExample {
    /*
    可以在 Condition 上调用 await() 方法使线程等待，其它线程调用 signal() 或 signalAll() 方法唤醒等待的线程。

相比于 wait() 这种等待方式，await() 可以指定等待的条件，因此更加灵活。

使用 Lock 来获取一个 Condition 对象。
     */
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void before(){
        lock.lock();
        try {
            System.out.println("before");
            condition.signal();
        }finally {
            lock.unlock();
        }
    }

    public void after(){
        lock.lock();
        try{
            condition.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        System.out.println("after");
    }

    public static void main(String[] args){
        AwaitSignalExample example = new AwaitSignalExample();
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(()->example.after());
        service.execute(()->example.before());

        service.shutdown();
    }
}
