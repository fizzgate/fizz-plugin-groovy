package we.fizz.plugin.groovy;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import we.plugin.FizzPluginFilterChain;

/**
 * @author huanghua
 */
public interface IHandler {

    default Mono<Void> execute(ServerWebExchange exchange) {
        return FizzPluginFilterChain.next(exchange);
    }

}
