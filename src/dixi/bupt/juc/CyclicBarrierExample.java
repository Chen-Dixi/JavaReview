package dixi.bupt.juc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyclicBarrierExample {
    /*
    用来控制多个线程互相等待，只有当多个线程都到达时，这些线程才会继续执行。

和 CountdownLatch 相似，都是通过维护计数器来实现的。线程执行 await() 方法之后计数器会减 1，并进行等待，直到计数器为 0，所有调用 await() 方法而在等待的线程才能继续执行。

CyclicBarrier 和 CountdownLatch 的一个区别是，CyclicBarrier 的计数器通过调用 reset() 方法可以循环使用，所以它才叫做循环屏障。

CyclicBarrier 有两个构造函数，其中 parties 指示计数器的初始值，barrierAction 在所有线程都到达屏障的时候会执行一次。
     */
//    public CyclicBarrier(int parties, Runnable barrierAction) {
//        if (parties <= 0) throw new IllegalArgumentException();
//        this.parties = parties;
//        this.count = parties;
//        this.barrierCommand = barrierAction;
//    }
//
//    public CyclicBarrier(int parties) {
//        this(parties, null);
//    }
    public  static void main(String[] args){
        final int totalThread = 10;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(totalThread);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i = 0; i < totalThread; i++){
            executorService.execute(() -> {
                System.out.print("before");
                try{
                    cyclicBarrier.await(); // cyclicBarrier的count减一个，调用它的线程挂起
                }catch (InterruptedException | BrokenBarrierException e){
                    e.printStackTrace();
                }
                System.out.print("after...");
            });
        }
        executorService.shutdown();
    }

}
