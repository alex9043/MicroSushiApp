package ru.alex9043.cartservice.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorsResponse {
    private List<FieldError> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String message;
        private LocalDateTime timestamp;
        private HttpStatusCode statusCode;
    }
}
