package ru.alex9043.gatewayservice.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private String message;
    private LocalDateTime timestamp;
    private int statusCode;

}
