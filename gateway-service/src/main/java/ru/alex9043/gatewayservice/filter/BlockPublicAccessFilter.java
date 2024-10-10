package ru.alex9043.gatewayservice.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class BlockPublicAccessFilter extends AbstractGatewayFilterFactory<Object> {
    @Override
    public GatewayFilter apply(Object config) {
        return ((exchange, chain) -> {
            String internalServiceHeader = exchange.getRequest().getHeaders().getFirst("X-Internal-Service");
            if (internalServiceHeader == null || !internalServiceHeader.equals("true")) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        });
    }
}
