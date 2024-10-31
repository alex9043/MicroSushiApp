package ru.alex9043.gatewayservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.alex9043.gatewayservice.dto.error.ErrorResponseDto;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleJwtValidationException(JwtValidationException ex) {
        log.error("Invalid JWT token: {}", ex.getMessage());
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        log.error("An error occurred: {}", ex.getMessage());
        ErrorResponseDto errorResponse = new ErrorResponseDto("An unexpected error occurred", LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
