package ru.alex9043.gatewayservice.filter;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.alex9043.commondto.TokenRequestDto;
import ru.alex9043.commondto.ValidationResponseDTO;
import ru.alex9043.gatewayservice.config.RabbitMQConfig;
import ru.alex9043.gatewayservice.exception.JwtValidationException;

import java.util.List;

@Component
public class CustomAuthFilter extends AbstractGatewayFilterFactory<CustomAuthFilter.Config> {
    private final RabbitTemplate rabbitTemplate;

    public CustomAuthFilter(RabbitTemplate rabbitTemplate) {
        super(Config.class);
        this.rabbitTemplate = rabbitTemplate;
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
            Boolean isValid = false;

            ValidationResponseDTO response;

            try {
                response = (ValidationResponseDTO)
                        rabbitTemplate.convertSendAndReceive(
                                RabbitMQConfig.AUTH_EXCHANGE_NAME,
                                RabbitMQConfig.AUTH_ROUTING_KEY_VALIDATE,
                                new TokenRequestDto(token)
                        );
            } catch (Exception e) {
                throw new JwtValidationException("Error occurred while validating token", HttpStatus.INTERNAL_SERVER_ERROR);
            }


            if (response != null && response.isValid()) {
                return chain.filter(exchange);
            } else {
                throw new JwtValidationException(response != null ? response.getErrorMessage() : "Unknown error", HttpStatus.FORBIDDEN);
            }
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
