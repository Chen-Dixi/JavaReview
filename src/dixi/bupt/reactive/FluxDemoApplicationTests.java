package dixi.bupt.reactive;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple3;

class FluxDemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mapExample() {
        Flux<String> fluxColors = Flux.just("red", "green", "blue");
        fluxColors.map(color -> color.charAt(0)).subscribe(System.out::println);
    }

    @Test
    void logExample() {
        Flux<String> fluxColors = Flux.just("red", "green", "blue");
        fluxColors.log().subscribe(System.out::println);
    }

    @Test
    void simpleFluxExample() {
        Flux<String> fluxFruits = Flux.just("apple", "pear", "plum");
        Flux<String> fluxColors = Flux.just("red", "green", "blue");
        Flux<Integer> fluxAmounts = Flux.just(10, 20, 30);
        Flux<Tuple3<String, String, Integer>> fluxZip = Flux.zip(fluxFruits, fluxColors, fluxAmounts);
        System.out.println("return!");
        fluxZip.subscribe(System.out::println);

        return;
    }

    @Test
    public void onErrorReturnExample() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                .map(i -> "10 / " + i + " = " + (10 / i))
                .onErrorReturn(ArithmeticException.class, "Division by 0 not allowed");

        fluxCalc.subscribe(value -> System.out.println("Next: " + value),
                error -> System.err.println("Error: " + error));

        Flux<String> fluxCalc2 = Flux.just(-1, 0, 1)
                .map(i -> "10 / " + i + " = " + (10 / i));

        fluxCalc2.subscribe(value -> System.out.println("Next: " + value),
                error -> System.err.println("Error: " + error));
    }

    @Test
    public void stepVerifierTest() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                .map(i -> "10 / " + i + " = " + (10 / i));

        StepVerifier.create(fluxCalc)
                .expectNextCount(1)
                .expectError(ArithmeticException.class)
                .verify();
    }

    @Test
    public void publishSubscribeExample() {
        Scheduler schedulerA = Schedulers.newParallel("Scheduler A");
        Scheduler schedulerB = Schedulers.newParallel("Scheduler B");
        Scheduler schedulerC = Schedulers.newParallel("Scheduler C");

        Flux.just(1)
                .map(i -> {
                    System.out.println("First map: " + Thread.currentThread().getName());
                    return i;
                })
                .publishOn(schedulerA)
                .map(i -> {
                    System.out.println("Second map: " + Thread.currentThread().getName());
                    return i;
                })
                .publishOn(schedulerB)
                .map(i -> {
                    System.out.println("Third map: " + Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(schedulerC)
                .map(i -> {
                    System.out.println("Fourth map: " + Thread.currentThread().getName());
                    return i;
                })
                .publishOn(schedulerA)
                .map(i -> {
                    System.out.println("Fifth map: " + Thread.currentThread().getName());
                    return i;
                })
                .blockLast();
    }

    @Test
    public void backpressureExample() {
        /*
        onSubscribe
        Requesting 2 emissions
        doOnNext: 1
        onNext 1
        doOnNext: 2
        onNext 2
        Requesting 2 emissions
        */
        Flux.range(1,5)
                .doOnNext(integer -> {
                    System.out.println("doOnNext: " + integer);
                })
                .subscribe(new Subscriber<Integer>() {
                    private Subscription s;
                    int counter;

                    @Override
                    public void onSubscribe(Subscription s) {
                        System.out.println("onSubscribe");
                        this.s = s;
                        System.out.println("Requesting 2 emissions");
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer i) {
                        System.out.println("onNext " + i);
                        counter++;
                        if (counter % 2 == 0) {
                            System.out.println("Requesting 2 emissions");
//                            s.request(2);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
    }

    @Test
    public void contextTest() {
        String key = "key";
        Mono<String> mono = Mono.just("anything")
                .doOnNext(System.out::println)
                .flatMap(s -> Mono.subscriberContext()
                        .map(ctx -> s + "Value stored in context: " + ctx.get(key)))
                .subscriberContext(ctx -> ctx.put(key, "myValue"));

        StepVerifier.create(mono)
                .expectNext("anythingValue stored in context: myValue")
                .verifyComplete();
    }

    @Test
    public void contextFluxTest() {
        String key = "key";
        
    }
}
