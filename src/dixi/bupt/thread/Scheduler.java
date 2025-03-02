package dixi.bupt.thread;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Test;

import com.github.phantomthief.concurrent.MoreFutures;

/**
 * 多线程调度器
 * Created on 2025-02-16
 */
public class Scheduler {

    private final ScheduledExecutorService singleScheduler = Executors.newSingleThreadScheduledExecutor();

    @Test
    public void testSingleSchedule() throws Exception {
        CountDownLatch latch = new CountDownLatch(3);

        MoreFutures.scheduleWithDynamicDelay(singleScheduler, () -> Duration.ofSeconds(3),
                () -> {
                    System.out.println("Thread Name:" + Thread.currentThread().getName());
                    latch.countDown();
                });

        latch.await();
        singleScheduler.shutdown();
    }
}
