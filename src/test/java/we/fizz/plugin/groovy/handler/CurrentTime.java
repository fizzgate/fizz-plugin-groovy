package we.fizz.plugin.groovy.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import we.fizz.plugin.groovy.IHandler;
import we.util.WebUtils;

public class CurrentTime implements IHandler {
    private static final String RES = "{\"code\":0,\"msg\":null,\"result\":%s}";

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Void> execute(ServerWebExchange exchange) {
        return WebUtils.responseJson(exchange, HttpStatus.OK,
                null, String.format(RES, System.currentTimeMillis()));
    }
}
