package dixi.bupt.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class MyRunnable implements Runnable {
    private AtomicInteger cnt  = new AtomicInteger();
    @Override
    public void run() {
        // ...

        try {
            Thread.sleep(2000);
            System.out.println("My Runnable");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}