package ru.alex9043.gatewayservice.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.alex9043.gatewayservice.dto.TokenRequestDto;

import java.util.List;

@Component
public class CustomAuthFilter extends AbstractGatewayFilterFactory<CustomAuthFilter.Config> {
    private final WebClient.Builder webClientBuilder;

    public CustomAuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            HttpMethod method = exchange.getRequest().getMethod();

            System.out.println("Filter is started");

            boolean isExcluded = config.getExcludedPaths().stream()
                    .anyMatch(excluded -> path.startsWith(excluded.getPath()) && method == excluded.getMethod());

            if (isExcluded) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            System.out.println("Token in request - " + token);
            System.out.println("Uri - " + config.getValidatePath());

            return webClientBuilder.build()
                    .post()
                    .uri(config.getValidatePath())
                    .bodyValue(new TokenRequestDto(token))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(isValid -> {
                        if (Boolean.TRUE.equals(isValid)) {
                            return chain.filter(exchange);
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            return exchange.getResponse().setComplete();
                        }
                    });
        });
    }

    public static class Config {
        private String validatePath;
        private List<ExcludedPath> excludedPaths;

        public String getValidatePath() {
            return validatePath;
        }

        public void setValidatePath(String validatePath) {
            this.validatePath = validatePath;
        }

        public List<ExcludedPath> getExcludedPaths() {
            return excludedPaths;
        }

        public void setExcludedPaths(List<ExcludedPath> excludedPaths) {
            this.excludedPaths = excludedPaths;
        }

        public static class ExcludedPath {
            private HttpMethod method;
            private String path;

            public HttpMethod getMethod() {
                return method;
            }

            public void setMethod(HttpMethod method) {
                this.method = method;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }
        }
    }
}
