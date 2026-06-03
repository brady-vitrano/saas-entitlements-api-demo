package com.example.spring.config;

import com.example.spring.error.ConflictException;
import com.example.spring.error.NotFoundException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> conflict(ConflictException error) {
        return error(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(NotFoundException error) {
        return error(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException error) {
        String message = error.getMessage();
        if (messageContains(message, "unique") || messageContains(message, "duplicate") || messageContains(message, "already exists")) {
            return error(HttpStatus.CONFLICT, message);
        }
        if (messageContains(message, "not found")) {
            return error(HttpStatus.NOT_FOUND, message);
        }
        return error(HttpStatus.BAD_REQUEST, message);
    }

    private static boolean messageContains(String message, String needle) {
        return message != null && message.toLowerCase().contains(needle);
    }

    private static ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message == null ? status.getReasonPhrase() : message));
    }
}
