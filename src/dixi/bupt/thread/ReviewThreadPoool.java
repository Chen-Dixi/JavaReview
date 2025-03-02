package dixi.bupt.thread;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ReviewThreadPoool {
    private static final int DEFAULT_IO_WORKER_COUNT = Math.max(Runtime.getRuntime().availableProcessors(), 8);

    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        for (int i = 0; i < 5; i++) {
//            executorService.execute(new MyRunnable());
//        }
//        executorService.shutdown();
//        System.out.println("Main Method!");
        System.out.println(DEFAULT_IO_WORKER_COUNT);
        String cpuNumStr = "4.0";
        int cpuCores = 0;
        try {
            cpuCores = Integer.parseInt(cpuNumStr);
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException");
        }
        System.out.println(cpuCores);
    }

    @Test
    public void reviewThreadPoolParameters() {
        // 线程池参数
        ExecutorService executorService = new ThreadPoolExecutor(
                5, // corePoolSize
                10, // maximumPoolSize
                60, // keepAliveTime
                TimeUnit.SECONDS, // unit
                new LinkedBlockingQueue<>(100), // workQueue
                Executors.defaultThreadFactory(), // threadFactory
                new ThreadPoolExecutor.AbortPolicy() // handler
        );
    }
}
