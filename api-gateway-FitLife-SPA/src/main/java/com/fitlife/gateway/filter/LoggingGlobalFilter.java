package com.fitlife.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtro global aplicado a TODAS las rutas del Gateway.
 * Deja trazabilidad de qué ruta entró y hacia qué microservicio fue enrutada,
 * y mide el tiempo total de la petición (útil durante la defensa técnica
 * para explicar el flujo de comunicación distribuida).
 */
@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long inicio = System.currentTimeMillis();
        String metodo = exchange.getRequest().getMethod() != null ? exchange.getRequest().getMethod().name() : "?";
        String ruta = exchange.getRequest().getPath().value();

        log.info(">> Petición entrante: {} {}", metodo, ruta);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duracionMs = System.currentTimeMillis() - inicio;
            int status = exchange.getResponse().getStatusCode() != null
                    ? exchange.getResponse().getStatusCode().value()
                    : 0;
            log.info("<< Respuesta: {} {} -> status={} ({} ms)", metodo, ruta, status, duracionMs);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
