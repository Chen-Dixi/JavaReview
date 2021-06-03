package dixi.bupt.thread;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewThreadPoool {
    //线程池

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            executorService.execute(new MyRunnable());
        }
        executorService.shutdown();
        System.out.println("Main Method!");
    }
}
