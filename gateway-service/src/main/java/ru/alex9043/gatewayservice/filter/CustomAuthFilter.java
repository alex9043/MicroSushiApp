package ru.alex9043.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.alex9043.commondto.TokenRequestDto;
import ru.alex9043.commondto.ValidationResponseDTO;
import ru.alex9043.gatewayservice.exception.JwtValidationException;
import ru.alex9043.gatewayservice.service.RabbitService;

import java.util.List;

@Component
@Slf4j
public class CustomAuthFilter extends AbstractGatewayFilterFactory<CustomAuthFilter.Config> {
    private final RabbitService rabbitService;


    public CustomAuthFilter(RabbitService rabbitService) {
        super(Config.class);
        this.rabbitService = rabbitService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            HttpMethod method = exchange.getRequest().getMethod();

            log.info("Processing request for path: {} with method: {}", path, method);

            boolean isExcluded = config.getExcludedPaths().stream()
                    .anyMatch(excluded -> path.startsWith(excluded.getPath()) && method == excluded.getMethod());

            if (isExcluded) {
                log.info("Request is excluded from authentication filter. Proceeding without authentication.");
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("Unauthorized request. Missing or invalid authorization header.");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            log.debug("Token received in request: {}", token);
            log.info("Validating token with RabbitMQ...");
            ValidationResponseDTO response = rabbitService.validate(new TokenRequestDto(token));

            if (response.isValid()) {
                log.info("Token is valid. Proceeding with the request.");
                return chain.filter(exchange);
            } else {
                log.error("Invalid token: {}", response.getErrorMessage());
                throw new JwtValidationException(response.getErrorMessage(), HttpStatus.FORBIDDEN);
            }
        });
    }

    public static class Config {
        private List<ExcludedPath> excludedPaths;

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
