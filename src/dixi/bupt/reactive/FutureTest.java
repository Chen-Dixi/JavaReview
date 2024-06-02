package dixi.bupt.reactive;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

/**
 * Created on 2022-08-11
 */
public class FutureTest {
    @Test
    public void backpressureExample() {
        String varInMainThread = "asd";
        CompletableFuture<Void> f = CompletableFuture.runAsync(() -> {
            System.out.println("run async");
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("main thread");
        System.out.println(Thread.currentThread().getName());
        f.whenComplete((v, e) -> {
            System.out.println("run async complete " + varInMainThread);
            System.out.println(Thread.currentThread().getName());
        });

        System.out.println("main thread 2");
        System.out.println(Thread.currentThread().getName());

        try {
            f.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
