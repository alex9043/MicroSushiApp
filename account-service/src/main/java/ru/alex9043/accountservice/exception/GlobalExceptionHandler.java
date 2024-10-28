package ru.alex9043.accountservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.alex9043.accountservice.dto.ErrorsResponse;
import ru.alex9043.accountservice.dto.error.JwtValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
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
        ErrorsResponse.FieldError response = new ErrorsResponse.FieldError(ex.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorsResponse.FieldError> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorsResponse.FieldError response = new ErrorsResponse.FieldError(ex.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorsResponse.FieldError> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ErrorsResponse.FieldError response = new ErrorsResponse.FieldError(ex.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorsResponse.FieldError> handleIllegalStateException(IllegalStateException ex) {
        ErrorsResponse.FieldError response = new ErrorsResponse.FieldError(ex.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(JwtValidationException.class)
    public ResponseEntity<ErrorsResponse.FieldError> handleJwtValidationException(JwtValidationException ex) {
        ErrorsResponse.FieldError errorResponse = new ErrorsResponse.FieldError(ex.getMessage(), LocalDateTime.now(), ex.getStatus());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }
}
