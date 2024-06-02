package dixi.bupt.reactive;

import org.junit.Test;

import reactor.core.publisher.Mono;

/**
 * Created on 2024-05-31
 */
public class MonoThreadingAndSchedulerTest {


    @Test
    public void threadingTest() throws InterruptedException {
        final Mono<String> mono = Mono.just("hello ");

        Thread t = new Thread(() -> mono
                .map(msg -> msg + "thread ")
                .subscribe(v ->
                        System.out.println(v + Thread.currentThread().getName())
                )
        );
        t.start();
        t.join();
    }

}
