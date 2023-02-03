package dixi.bupt.reactive.core;

import reactor.core.publisher.Mono;

/**
 * @author chendixi
 * Created on 2023-02-03
 */
public interface Filter {
    Mono<Void> filter(Exchange exchange, DefaultChain chain);
}
