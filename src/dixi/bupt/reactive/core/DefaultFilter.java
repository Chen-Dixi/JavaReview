package dixi.bupt.reactive.core;

import reactor.core.publisher.Mono;

/**
 * @author chendixi
 * Created on 2023-01-06
 */
public abstract class DefaultFilter implements Filter {
    public abstract Mono<Void> filter(Exchange exchange, DefaultChain chain);
}
