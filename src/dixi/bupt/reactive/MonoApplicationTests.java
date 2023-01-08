package dixi.bupt.reactive;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.google.common.util.concurrent.Uninterruptibles;

import dixi.bupt.reactive.core.DefaultChain;
import dixi.bupt.reactive.core.DefaultFilter;
import dixi.bupt.reactive.core.Exchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

/**
 * @author chendixi
 * Created on 2023-01-04
 */

public class MonoApplicationTests {
    /*


     */
    @Test
    public void onErrorResumeTest() {
        Mono<String> mono1 = Mono.defer(() -> Mono.just("asdasdas"));
        Mono<String> mono2 = Mono.defer(() -> Mono.just("1"));

        Mono<Long> longMono = mono1.flatMap(s -> {
            return Mono.just(Long.valueOf(s));
        }).onErrorResume(throwable -> {
            System.out.println("on error resume");
            return Mono.just(0L);
        });

        StepVerifier.create(longMono)
                .expectNext(0L)
                .verifyComplete();

        longMono = mono2.flatMap(s -> {
            return Mono.just(Long.valueOf(s));
        }).onErrorResume(throwable -> {
            System.out.println("on error resume");
            return Mono.just(0L);
        });

        StepVerifier.create(longMono)
                .expectNext(1L)
                .verifyComplete();

    }

    /**
     * 模仿SCG的GatewayFilter
     */
    @Test
    public void filterChainTest() {
        DefaultFilter filterOne = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                System.out.println("one");
                return chain.filter(exchange);
            }
        };

        DefaultFilter filterTwo = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                System.out.println("two");
                return chain.filter(exchange);
            }
        };

        DefaultFilter filterThree = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                System.out.println("three");
                exchange.setName("three is here");
                return chain.filter(exchange);
            }
        };

        List<DefaultFilter> filters = new ArrayList<>();
        filters.add(filterOne);
        filters.add(filterTwo);
        filters.add(filterThree);

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange)
                .subscribe();

        System.out.println(exchange.getName());
    }

    @Test
    public void monoTimeoutTest() {
        DefaultFilter authCheck = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                return Mono.defer(()->
                    Mono.fromCallable(() -> {
                        System.out.println("First filter: " + Thread.currentThread().getName());
                        Uninterruptibles.sleepUninterruptibly(300, TimeUnit.MILLISECONDS);
                        return true;
                    })
                ).timeout(Duration.ofMillis(500), Mono.defer(() ->  Mono.error(new Exception("validate passport token timeout"))))
                        .onErrorResume(e -> {
                            System.out.println(e.getMessage());
                            System.out.println("stop at filter authCheck, ");
                            return Mono.empty();
                        })
                        .flatMap(valid -> {
                            if (valid) {
                                return chain.filter(exchange);
                            }
                            return Mono.empty();
                        });
            }
        };

        DefaultFilter filterTwo = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                System.out.println("comes to filter two");
                return chain.filter(exchange);
            }
        };

        List<DefaultFilter> filters = new ArrayList<>();
        filters.add(authCheck);
        filters.add(filterTwo);

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange)
                .subscribe();
    }

    @Test
    public void monoDeferTest() {
        Mono<String> deferAction =  Mono.defer(this::getDeferSample);

        System.out.println("Intermediate");

        StepVerifier.create(deferAction)
                .expectNext("test")
                .verifyComplete();
    }

    private Mono<String> getDeferSample() {
        System.out.println("call getDeferSample");
        return Mono.just("test");
    }

    @Test
    public void monoTransformTest() {
        Mono<Integer> monoInteger = Mono.just(4);

        Function<Mono<Integer>, Mono<Integer>> applySchedulers = mono -> mono.subscribeOn(Schedulers.newParallel("Scheduler SB"))
                .publishOn(Schedulers.newParallel("Scheduler PB"));
        monoInteger = monoInteger.map(i -> {
                    System.out.println("First map: " + Thread.currentThread().getName());
                    return i * i;
        }).transform(applySchedulers)
                .map(i -> {
                    System.out.println("Second map: " + Thread.currentThread().getName());
                    return i / 2;
                });

        StepVerifier.create(monoInteger)
                .expectNext(8)
                .verifyComplete();
    }
}
