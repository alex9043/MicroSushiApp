package ru.alex9043.gatewayservice.dto.error;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorResponseDto {
    private String message;
    private LocalDateTime timestamp;
    private int statusCode;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(String message, HttpStatus status) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.statusCode = status.value();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
