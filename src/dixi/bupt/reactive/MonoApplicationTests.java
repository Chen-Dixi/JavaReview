package dixi.bupt.reactive;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

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

    /**
     * <a href="https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#flatMap-java.util.function.Function-">flatMap-官方API文档</a>
     */
    @Test
    public void flatMapTest() {
        Mono<String> mono = Mono.fromSupplier(() -> Mono.just(3))
                .flatMap(number -> {
                   return Mono.just("three");
                });

        StepVerifier.create(mono)
                .expectNext("three")
                .verifyComplete();
    }

    /**
     * 输出
     * Intermediate
     * call getDeferSample
     */
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

    /**
     * <a href="https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#transform-java.util.function.Function-">transform-API文档</a>
     */
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

    /**
     * In other words ignore element from this Mono and transform its completion signal into the emission and completion signal of a provided Mono<V>. <br/>
     * Error signal is replayed in the resulting Mono<V>.
     */
    @Test
    public void monoThenTest() {
        Mono<Object> first = Mono.just(1L)
                .flatMap(integer -> {
                    System.out.println("First work ...");
                    Uninterruptibles.sleepUninterruptibly(integer, TimeUnit.SECONDS);
                    return Mono.empty();
                }).then(Mono.fromSupplier(() -> {
                    System.out.println("Second work ...");
                    return null;
                }));
        first.subscribe();
    }

    /**
     * In other words ignore element from this Mono and transform its completion signal into the emission and completion signal of a provided Mono<V>. <br/>
     * Error signal is replayed in the resulting Mono<V>.
     */
    @Test
    public void monoThenErrorTest() {
        Mono<Long> errorSignalThen = Mono.just(1L)
                .map(l -> {
                    System.out.println("First work ...");
                    return mapLongOk(l);
                })
                .doOnError(e -> {
                    System.out.println("First work error");
                })
                .then(Mono.just(1L)
                        .map(l -> {
                            System.out.println("Second work ...");
                            return mapLongWithError(l);
                        })
                        .doOnError(e -> {
                            System.out.println("Second work error");
                        })
                );

        errorSignalThen.subscribe();
    }

    private long mapLongWithError(long l) {
        throw new RuntimeException();
    }

    private long mapLongOk(long l) {
        return 2;
    }

    /**
     * The Consumer is executed first, then the onError signal is propagated downstream.<br/>
     * 输出：<br/>
     * one<br/>
     * Filter one flatMap<br/>
     * two<br/>
     * Filter two flatMap<br/>
     * three<br/>
     * Filter three pre onError<br/>
     * Filter two post onError<br/>
     * Filter one post onError<br/>
     */
    @Test
    public void doOnErrorOrderTest() {
        DefaultFilter filterOne = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                return Mono.fromSupplier(() -> {
                    System.out.println("one");
                    return 1L;
                }).map((i) -> mapLongOk(i))
                .doOnError(e -> {
                    System.out.println("Filter one pre onError");
                })
                .flatMap((i) -> {
                    System.out.println("Filter one flatMap");
                    return chain.filter(exchange).doOnError(e -> {
                        System.out.println("Filter one post onError");
                    });
                });
            }
        };


        DefaultFilter filterTwo = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                return Mono.fromSupplier(() -> {
                    System.out.println("two");
                    return 1L;
                }).map((i) -> mapLongOk(i))
                .doOnError(e -> {
                    System.out.println("Filter two pre onError");
                })
                .flatMap((i) -> {
                    System.out.println("Filter two flatMap");
                    return chain.filter(exchange).doOnError(e -> {
                        System.out.println("Filter two post onError");
                    });
                });
            }
        };

        DefaultFilter filterThree = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                return Mono.fromSupplier(() -> {
                            System.out.println("three");
                            return 1L;
                        }).map((i) -> mapLongWithError(i))
                        .doOnError(e -> {
                            System.out.println("Filter three pre onError");
                        }).flatMap((i) -> {
                            System.out.println("Filter three flatMap");
                            return chain.filter(exchange).doOnError(e -> {
                                System.out.println("Filter three post onError");
                            });
                        });
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
    }

    /**
     * 输出：
     * one<br/>
     * two<br/>
     * Filter one doFinally<br/>
     * Filter two doFinally<br/>
     * 仔细看Filter在Chain当中的成链方式，文档里写"the signal is propagated downstream before the callback is executed means that several doFinally in a row will be executed in reverse order."<br/>
     * 这里Filter one 的doFinally先执行，是因为chain.filter(exchange).doFinally的写法，"Filter one doFinally"实际上是在Mono链的最下游。
     */
    @Test
    public void doFinallyOrderTest() {
        DefaultFilter filterOne = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                System.out.println("one");
                return chain.filter(exchange).doFinally((s) -> {
                    System.out.println("Filter one doFinally");
                });
            }
        };


        DefaultFilter filterTwo = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                System.out.println("two");
                return chain.filter(exchange).doFinally((t) -> {
                    System.out.println("Filter two doFinally");
                });
            }
        };

        List<DefaultFilter> filters = new ArrayList<>();
        filters.add(filterOne);
        filters.add(filterTwo);

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange)
                .subscribe();
    }

    /**
     * 输出：
     * one
     * two
     * Filter two success
     * Filter one success
     * doOnSuccess实际上在 is executed right before onNext is propagated downstream
     * 仔细看Filter在Chain当中的成链方式<br/>
     * 这里Filter one 的doOnSuccess后执行，是因为chain.filter(exchange).doOnSuccess对的写法，"Filter one success"实际上是在Mono链的最下游。"Filter one success"在这最后执行。
     */
    @Test
    public void doOnSuccessOrderTest() {

        DefaultFilter filterOne = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                System.out.println("one");
                return chain.filter(exchange).doOnSuccess((v) -> {
                    System.out.println("Filter one success");
                });
            }
        };


        DefaultFilter filterTwo = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                System.out.println("two");
                return chain.filter(exchange).doOnSuccess((t) -> {
                    System.out.println("Filter two success");
                });
            }
        };

        List<DefaultFilter> filters = new ArrayList<>();
        filters.add(filterOne);
        filters.add(filterTwo);

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange)
                .subscribe();
    }

    /**
     * one<br/>
     * two<br/>
     * Filter one cancel<br/>
     * Filter two cancel<br/>
     * The handler is executed first, then the cancel signal is propagated upstream to the source.<br/>
     * cancel信号从最后往上游传播，先执行 Mono链底下的，再执行上游的，所以这里是"Filter one cancel"先执行
     */
    @Test
    public void doOnCancelOrderTest() {
        DefaultFilter filterOne = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                return Mono.defer(() -> {
                    System.out.println("one");
                    return chain.filter(exchange).doOnCancel(() -> {
                        System.out.println("Filter one cancel");
                    });
                });
            }
        };


        DefaultFilter filterTwo = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                return Mono.defer(() -> {
                    System.out.println("two");
                    return chain.filter(exchange).doOnSuccess((t) -> {
                        System.out.println("Filter two cancel");
                    });
                });
            }
        };

        List<DefaultFilter> filters = new ArrayList<>();
        filters.add(filterOne);
        filters.add(filterTwo);

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange)
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.cancel();
                    }

                    @Override
                    public void onNext(Void v) {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
