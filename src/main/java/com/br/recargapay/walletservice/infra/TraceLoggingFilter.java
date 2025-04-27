package com.br.recargapay.walletservice.infra;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceLoggingFilter implements WebFilter {

    private static final String TRACE_ID = "traceId";

    @Value("${logging.trace.log-headers:false}")
    private boolean logHeaders;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst(TRACE_ID);

        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        String finalTraceId = traceId;

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(TRACE_ID, finalTraceId))
                .doFirst(() -> {
                    MDC.put(TRACE_ID, finalTraceId);
                    if (logHeaders) {
                        request.getHeaders().forEach((key, value) ->
                                System.out.println("Header: " + key + " = " + value)
                        );
                    }
                })
                .doFinally(signal -> MDC.remove(TRACE_ID));
    }
}