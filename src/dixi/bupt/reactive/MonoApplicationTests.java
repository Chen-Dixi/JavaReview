package dixi.bupt.reactive;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.google.common.util.concurrent.Uninterruptibles;

import dixi.bupt.reactive.core.DefaultChain;
import dixi.bupt.reactive.core.DefaultFilter;
import dixi.bupt.reactive.core.Exchange;
import dixi.bupt.reactive.core.Filter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

/**
 * @author chendixi
 * Created on 2023-01-04
 */

public class MonoApplicationTests {
    private static String PRE_FILTER_FORMAT = "%s pre filter";
    private static String POST_FILTER_FORMAT = "%s post filter";

    @Test
    public void monoToFluxTest() {
        System.out.println("Start on: " + Thread.currentThread().getName());
        Mono<String> mono = Mono.defer(() -> {
            System.out.println("Defer on: " + Thread.currentThread().getName());
            return Mono.just("asdasdas");
        }).doOnNext(str -> {
            System.out.println("doOnNext on: " + Thread.currentThread().getName());
        });
        mono.subscribe();
    }

    // onErrorResume, flatMap, timeout 放在一起测试
    @Test
    public void baseStreamTest() {

        Mono<Void> voidMono = Mono.defer(() -> {
                    System.out.println("start");
                    return Mono.just("3");
                })
                .flatMap(str -> {
                    System.out.println("receive str " + str);
                    if (str == "3") {
                        return voidMono();
                    }
                    return Mono.just("4");
                })
                .flatMap(number -> {
                    System.out.println("receive number " + number);
                    return Mono.empty();
                });

        voidMono.subscribe();
    }

    @Test
    public void onErrorResumeTest() {
        Mono<String> mono1 = Mono.defer(() -> Mono.just("asdasdas"));
        Mono<String> mono2 = Mono.defer(() -> Mono.just("1"));

        Mono<Long> longMono = mono1.flatMap(s -> {
            return Mono.just(Long.valueOf(s));
        }).onErrorResume(throwable -> {
            System.out.println("on error resume1");
            return Mono.error(throwable);
        }).flatMap(longValue -> {
            System.out.println("long value" + longValue);
            return Mono.just(longValue);
        }).onErrorResume(throwable -> {
            System.out.println("on error resume2");
            return Mono.error(throwable);
        });

        longMono.subscribe(new Subscriber<Long>() {
            @Override
            public void onSubscribe(Subscription subscription) {
//                        subscription.cancel();
                System.out.println("Subscriber.onSubscribe");
            }

            @Override
            public void onNext(Long v) {
                System.out.println("Subscriber.onNext"); // 没有打印出来
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Subscriber.onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });

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

        List<Filter> filters = new ArrayList<>();
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
                return Mono.defer(() ->
                                Mono.fromCallable(() -> {
                                    System.out.println("First filter: " + Thread.currentThread().getName());
                                    Uninterruptibles.sleepUninterruptibly(300, TimeUnit.MILLISECONDS);
                                    return true;
                                })
                        ).timeout(Duration.ofMillis(500), Mono.defer(() -> Mono.error(new Exception("validate passport token timeout"))))
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

        List<Filter> filters = new ArrayList<>();
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
        Mono<String> deferAction = Mono.defer(this::getDeferSample);
        Mono<String> callableAction = Mono.fromCallable(() -> getCallableSample());

        System.out.println("Intermediate");

        StepVerifier.create(deferAction)
                .expectNext("test")
                .verifyComplete();

        StepVerifier.create(callableAction)
                .expectNext("test")
                .verifyComplete();
    }

    private Mono<String> getDeferSample() {
        System.out.println("call getDeferSample");
        return Mono.just("test");
    }

    private String getCallableSample() {
        System.out.println("call getCallableSample");
        return "test";
    }

    /**
     * <a href="https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#transform-java.util.function.Function-">transform-API文档</a>
     */
    @Test
    public void monoTransformTest() {
        Mono<Integer> monoInteger = Mono.just(4);

        Function<Mono<Integer>, Mono<Integer>> applySchedulers = mono -> mono.publishOn(Schedulers.newParallel("Scheduler PB"))
                .subscribeOn(Schedulers.newParallel("Scheduler SB"));

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
     * <p/>
     * First Test ...<br/>
     * Mono one ...<br/>
     * Mono one on error<br/>
     * Downstream on Error error<br/>
     * Second Test ...<br/>
     * Mono one ...<br/>
     * Then: Mono two ...<br/>
     * Then: Mono two on error<br/>
     * Downstream on error<br/>
     */
    @Test
    public void monoThenErrorTest() {
        System.out.println("First Test ...");
        Mono<Long> errorSignalThen = Mono.just(1L)
                .map(l -> {
                    System.out.println("Mono one ...");
                    return mapLongWithError(l);
                })
                .doOnError(e -> {
                    System.out.println("Mono one on error");
                })
                .then(Mono.just(1L)
                        .map(l -> {
                            System.out.println("Then: Mono two ...");
                            return mapLongWithError(l);
                        })
                        .doOnError(e -> {
                            System.out.println("Then: Mono two on error");
                        })
                ).doOnError(e -> {
                    System.out.println("Downstream on error");
                });
        errorSignalThen.subscribe();

        System.out.println("Second Test ...");

        Mono<Long> errorSignalThen2 = Mono.just(1L)
                .map(l -> {
                    System.out.println("Mono one ...");
                    return mapLongOk(l);
                })
                .doOnError(e -> {
                    System.out.println("Mono one on error");
                })
                .then(Mono.just(1L)
                        .map(l -> {
                            System.out.println("Then: Mono two ...");
                            return mapLongWithError(l);
                        })
                        .doOnError(e -> {
                            System.out.println("Then: Mono two on error");
                        })
                ).doOnError(e -> {
                    System.out.println("Downstream on Error error");
                });

        errorSignalThen2.subscribe(new Subscriber<Long>() {
            @Override
            public void onSubscribe(Subscription subscription) {
//                        subscription.cancel();
                System.out.println("Subscriber.onSubscribe");
            }

            @Override
            public void onNext(Long v) {
                System.out.println("Subscriber.onNext"); // 没有打印出来
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Subscriber.onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
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

        List<Filter> filters = new ArrayList<>();
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

        List<Filter> filters = new ArrayList<>();
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

        List<Filter> filters = new ArrayList<>();
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
                System.out.println("one");
                return Mono.fromSupplier(() -> {
                    System.out.println("Filter one from supplier");
                    return 3;
                }).flatMap(n -> chain.filter(exchange).doOnSuccess((t) -> {
                    System.out.println("Filter one doOnSuccess");
                }).doOnCancel(() -> {
                    System.out.println("Filter one doOnCancel");
                }));
            }
        };


        DefaultFilter filterTwo = new DefaultFilter() {
            @Override
            public Mono<Void> filter(Exchange exchange, DefaultChain chain) {
                System.out.println("two");
                return Mono.fromSupplier(() -> {
                    System.out.println("Filter two from supplier");
                    return 3;
                }).flatMap(n -> chain.filter(exchange).doOnSuccess((t) -> {
                    System.out.println("Filter two doOnSuccess");
                }));
            }
        };

        List<Filter> filters = new ArrayList<>();
        filters.add(filterOne);
        filters.add(filterTwo);

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange)
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
//                        subscription.cancel();
                        System.out.println("Subscriber.onSubscribe");
                    }

                    @Override
                    public void onNext(Void v) {
                        System.out.println("Subscriber.onNext"); // 没有打印出来
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
    }

    @Test
    public void doOnCancelAndErrorTest() {
        Filter filterOne = (exchange, chain) -> {
            return Mono.defer(() -> {
                        System.out.println("one");
                        return Mono.empty();
                    }).doOnCancel(() -> System.out.println("filter one onCancel pre"))
                    .doOnError((e) -> System.out.println("filter one onError pre"))
                    .then(chain.filter(exchange))
                    .doOnError((e) -> System.out.println("filter one onError post"))
                    .doOnCancel(() -> System.out.println("filter one onCancel post"));
        };


        Filter filterTwo = (exchange, chain) -> {
            return Mono.defer(() -> {
                mapLongWithError(1L);
                return chain.filter(exchange).doOnSuccess((t) -> {
                    System.out.println("Filter two cancel");
                });
            });
        };


        List<Filter> filters = new ArrayList<>();
        filters.add(filterOne);
        filters.add(filterTwo);

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange).subscribe();
    }

    @Test
    public void monoRunnableTest() {
        List<Filter> filters = new ArrayList<>();

        filters.add(createFilter("one"));
        filters.add(createFilter("two"));
        filters.add(createFilter("three"));

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange).subscribe();
    }

    private Filter createFilter(String name) {
        return (exchange, chain) -> {
            System.out.println(String.format(PRE_FILTER_FORMAT, name));
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                System.out.println(String.format(POST_FILTER_FORMAT, name));
            }));
        };
    }

    /**
     * 如果doOnNext里面有Mono, Mono里的内容是不会执行的
     */
    @Test
    public void monoDoOnNextTest() {
        List<Filter> filters = new ArrayList<>();
        filters.add((exchange, chain) -> {
            return Mono.just(1)
                    .doOnNext(integer -> handle(integer))
                    .flatMap(integer -> chain.filter(exchange));
        });

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange).subscribe();

    }

    private void handle(Integer integer) {
        System.out.println("call handle " + integer);
        getMonoVoid();
    }

    /**
     * 当上游步骤返回了Mono<Void>的时候，下游至少需要一个对象结果的operator就不会执行
     * 可以看这篇文档：<a href="https://projectreactor.io/docs/core/release/reference/#faq.monoThen">My Mono zipWith or zipWhen is never called</a>
     */
    @Test
    public void emptySourceTest() {
        List<Filter> filters = new ArrayList<>();
        boolean testVoidBehavior = true; // 尝试ture 和 false，分别是什么运行结果
        filters.add((exchange, chain) -> {
            return Mono.defer(() -> {
                        if (testVoidBehavior) {
                            return getMonoVoid();
                        }
                        return Mono.just(true);
                    })
                    .flatMap(result -> {
                        // 当上面返回返回 Void 的时候，flatMap不会执行。
                        System.out.println("filter1 flatMap");
                        return chain.filter(exchange);
                    });
        });
        filters.add((exchange, chain) -> {
            System.out.println("filter2 start");
            return chain.filter(exchange);
        });

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange).subscribe();
    }

    @Test
    public void emptySourceWithSwitchIfEmpty() {
        List<Filter> filters = new ArrayList<>();
        filters.add((exchange, chain) -> {
            return Mono.defer(() -> {
                        return getMonoVoid();
                    })
                    .switchIfEmpty(
                            Mono.defer(() -> {
                                return chain.filter(exchange);
                            })
                    );
        });

        filters.add((exchange, chain) -> {
            System.out.println("filter2 start");
            return chain.filter(exchange);
        });
        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange).subscribe();
    }

    @Test
    public void zipMonoTest() {
        List<Filter> filters = new ArrayList<>();
        filters.add((exchange, chain) -> {
            // zip
            Mono<Boolean> checkPermission1 = Mono.fromSupplier(() -> true);
            Mono<Boolean> checkPermission2 = Mono.fromSupplier(() -> false);
            Mono<Boolean> zipMono = Mono.zip(checkPermission1, checkPermission2, (v1, v2) -> {
                System.out.println("result 2: " + v1);
                System.out.println("result 2: " + v2);
                return v1 || v2;
            });


            return zipMono.flatMap(result -> {
                System.out.println("final result: " + result);
                return chain.filter(exchange);
            });
        });

        DefaultChain chain = new DefaultChain(filters);
        Exchange exchange = new Exchange();
        chain.filter(exchange).subscribe();
    }

    @Test
    public void monoSinkTest() {
        Mono<String> mono = Mono.create(sink -> {
            simulateAsyncOperation(() -> {
                sink.success("Hello, World!");
            }, () -> {
                sink.error(new RuntimeException("Async operation failed"));
            });
        });

        mono.subscribe(
                result -> System.out.println("Success: " + result),
                error -> System.out.println("Error: " + error.getMessage())
        );
    }

    private static void simulateAsyncOperation(Runnable successAction, Runnable failureAction) {
        try {
            Thread.sleep(1000);
            successAction.run();
        } catch (InterruptedException e) {
            failureAction.run();
        }
    }

    private Mono<Void> getMonoVoid() {
        System.out.println("call getMonoVoid");
        return Mono.fromRunnable(() -> {
            System.out.println("call getMonoVoid: void Mono");
        });
    }

    private Mono<Void> voidMono() {
        return Mono.empty();
    }
}
