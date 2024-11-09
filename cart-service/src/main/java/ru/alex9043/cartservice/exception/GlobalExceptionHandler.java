package ru.alex9043.cartservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.alex9043.cartservice.dto.error.ErrorsResponse;
import ru.alex9043.cartservice.dto.error.JwtValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        List<ErrorsResponse.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorsResponse.FieldError(error.getDefaultMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST))
                .collect(Collectors.toList());

        ErrorsResponse response = new ErrorsResponse(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorsResponse.FieldError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Message not readable error: {}", ex.getMessage());
        ErrorsResponse.FieldError response = new ErrorsResponse.FieldError(ex.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorsResponse.FieldError> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument error: {}", ex.getMessage());
        ErrorsResponse.FieldError response = new ErrorsResponse.FieldError(ex.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorsResponse.FieldError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation error: {}", ex.getMessage());
        ErrorsResponse.FieldError response = new ErrorsResponse.FieldError(ex.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorsResponse.FieldError> handleIllegalStateException(IllegalStateException ex) {
        log.error("Illegal state error: {}", ex.getMessage());
        ErrorsResponse.FieldError response = new ErrorsResponse.FieldError(ex.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<ErrorsResponse.FieldError> handleJwtValidationException(JwtValidationException ex) {
        log.error("JWT validation error: {}", ex.getMessage());
        ErrorsResponse.FieldError errorResponse = new ErrorsResponse.FieldError(ex.getMessage(), LocalDateTime.now(), ex.getStatus());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }
}
