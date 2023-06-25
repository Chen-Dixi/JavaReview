package dixi.bupt.reactive.core;

import java.util.List;

import reactor.core.publisher.Mono;

/**
 * @author chendixi
 * Created on 2023-01-06
 */
public class DefaultChain implements Chain {

    private int index;
    List<Filter> filters;

    public DefaultChain(List<Filter> filters) {
        this.index = 0;
        this.filters = filters;
    }

    public DefaultChain(List<Filter> filters, int index) {
        this.index = index;
        this.filters = filters;
    }

    public Mono<Void> filter(Exchange exchange) {
        return Mono.defer(() -> {
            if (index < filters.size()) {
                Filter filter = filters.get(index);
                index++;
                return filter.filter(exchange, this);
            } else {
                return Mono.empty();
            }
        });
    }
}
