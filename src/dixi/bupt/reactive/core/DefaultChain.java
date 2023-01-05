package dixi.bupt.reactive.core;

import java.util.List;

import reactor.core.publisher.Mono;

/**
 * @author chendixi
 * Created on 2023-01-06
 */
public class DefaultChain {

    private int index;
    List<DefaultFilter> filters;

    public DefaultChain(List<DefaultFilter> filters) {
        this.index = 0;
        this.filters = filters;
    }

    public DefaultChain(List<DefaultFilter> filters, int index) {
        this.index = index;
        this.filters = filters;
    }

    public Mono<Void> filter(Exchange exchange) {
        return Mono.defer(() -> {
            if (index < filters.size()) {
                DefaultFilter filter = filters.get(index);
                DefaultChain chain = new DefaultChain(filters, index + 1);
                return filter.filter(exchange, chain);
            } else {
                return Mono.empty();
            }
        });
    }
}
