package ru.alex9043.productservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.alex9043.productservice.dto.ErrorsResponse;

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
}
