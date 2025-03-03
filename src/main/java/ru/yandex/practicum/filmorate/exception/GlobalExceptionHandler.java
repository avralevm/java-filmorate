package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, Object> fieldErrors  = new HashMap<>();
        fieldErrors.put("timestamp", LocalDateTime.now());
        fieldErrors.put("status", HttpStatus.BAD_REQUEST.value());
        fieldErrors.put("error", "Validation Error");

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
            log.error("[ERROR] Validation error: {}",  error.getDefaultMessage());
        });
        return fieldErrors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConditionsNotMetException.class)
    public Map<String, Object> handleConditionsNotMetException(ConditionsNotMetException ex) {
        Map<String, Object> fieldErrors  = new HashMap<>();
        fieldErrors.put("timestamp", LocalDateTime.now());
        fieldErrors.put("status", HttpStatus.BAD_REQUEST.value());
        fieldErrors.put("error", "Conditions Not Met");
        fieldErrors.put("message", ex.getMessage());

        log.error("[ERROR] Conditions not met: {}", ex.getMessage());
        return fieldErrors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, Object> handleNotFoundException(NotFoundException ex) {
        Map<String, Object> fieldErrors = new HashMap<>();
        fieldErrors.put("timestamp", LocalDateTime.now());
        fieldErrors.put("status", HttpStatus.NOT_FOUND.value());
        fieldErrors.put("error", "Resource Not Found");
        fieldErrors.put("message", ex.getMessage());

        log.error("[ERROR] Resource not found: {}", ex.getMessage());
        return fieldErrors;
    }
}