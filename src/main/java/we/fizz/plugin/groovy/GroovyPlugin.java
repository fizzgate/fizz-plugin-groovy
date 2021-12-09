package we.fizz.plugin.groovy;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import we.plugin.FizzPluginFilterChain;
import we.plugin.core.filter.AbstractFizzPlugin;
import we.plugin.core.filter.config.FizzConfig;
import we.util.JacksonUtils;
import we.util.WebUtils;

import static we.fizz.plugin.groovy.GroovyPlugin.PluginConfig;
import static we.fizz.plugin.groovy.GroovyPlugin.RouterConfig;

/**
 * groovy脚本插件
 *
 * @author huanghua
 */
@Slf4j
@Component
public class GroovyPlugin extends AbstractFizzPlugin<RouterConfig, PluginConfig> {

    @Override
    public String pluginName() {
        return "groovyPlugin";
    }

    @Override
    public Mono<Void> doFilter(ServerWebExchange exchange) {
        if (log.isTraceEnabled()) {
            log.trace("config:{}", JacksonUtils.writeValueAsString(this.originRouterCfg(exchange)));
        }
        RouterConfig routerConfig = routerConfig(exchange);
        if (routerConfig == null) {
            return FizzPluginFilterChain.next(exchange);
        }
        try {
            IHandler handler = GroovyFactory.getInstance().loadNewInstance(routerConfig.getCodeSource());
            log.trace("groovy handler execute. handler:{}", handler);
            return handler.execute(exchange);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return WebUtils.buildDirectResponse(exchange, HttpStatus.BAD_GATEWAY, null, "");
    }

    @Data
    @FizzConfig
    public static class RouterConfig {
        private String codeSource;
    }

    @Data
    @FizzConfig
    public static class PluginConfig {
    }

}
