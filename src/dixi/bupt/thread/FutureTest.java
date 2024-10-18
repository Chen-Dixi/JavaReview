package dixi.bupt.thread;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.junit.Test;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;


/**
 * Created on 2023-12-20
 */
public class FutureTest {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        CompletableFuture future = null;
        ExecutorService service;
    }

    @Test
    public void test() {
        System.out.println("Hello World!");
    }

    @Test
    public void listenableFutureAndMonoTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ListenableFuture<String> listenableFuture = createListenableFuture(latch);
        Mono<String> mono = Mono.create(sink -> {
            System.out.println("Mono create, current thread: " + Thread.currentThread().getName());
            listenableFuture.addListener(() -> {
                if (listenableFuture.isDone()) {
                    try {
                        sink.success(listenableFuture.get());
                    } catch (Exception e) {
                        sink.error(e);
                    }
                }
            }, Runnable::run);
        });

        mono.publishOn(Schedulers.newParallel("Scheduler PB"))
                .doOnNext(t -> {
                    System.out.println("Do on next, current thread:" + Thread.currentThread().getName());
                })
                .subscribeOn(Schedulers.newParallel("Scheduler SB"))
                .subscribe(
                result -> System.out.println("Success: " + result),
                error -> System.out.println("Error: " + error.getMessage())
        );
        latch.await(); // 这将阻塞主线程直到计数器归零
    }

    private ListenableFuture<String> createListenableFuture(CountDownLatch latch) {
        SettableFuture<String> future = SettableFuture.create();
        new Thread(() -> {
            try {
                // 假设这里有一个耗时的操作
                Thread.sleep(1000);
                future.set("Hello, World!"); // 设置结果
            } catch (InterruptedException e) {
                future.setException(e); // 设置异常
            } finally {
                latch.countDown(); // 异步操作完成后，释放计数器
            }
        }).start();
        return future;
    }

    /**
     * 掌握CompletableFuture 和 ListenableFuture 各自的差异和场景
     */
    @Test
    public void completableFutureTest() throws InterruptedException {

        // 创建一个 CompletableFuture 实例
        CompletableFuture<String> future = new CompletableFuture<>();
        // 启动一个线程在后台模拟某个异步操作
        new Thread(() -> {
            try {
                // 模拟一些耗时操作，比如网络请求或计算
                Thread.sleep(2000);
                // 手动完成 CompletableFuture
                future.complete("Hello from the future!");
            } catch (InterruptedException e) {
                future.completeExceptionally(e);
            }
        }).start();
        // 主线程等待 CompletableFuture 的结果
        System.out.println("Waiting for the future to complete...");
        // 获取并打印结果
        try {
            String result = future.get();  // 这将阻塞，直到 future 被 complete
            System.out.println("Result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Main thread finished.");
    }
}
