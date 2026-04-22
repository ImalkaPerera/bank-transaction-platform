











// Handles exceptions thrown in your REST controllers and logs them.
package com.bank.logging.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles RuntimeExceptions and returns a 404 error response
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
        RuntimeException ex) {

    log.error("RuntimeException: {}", ex.getMessage());

    Map<String, Object> error = new HashMap<>();
    error.put("timestamp", LocalDateTime.now().toString());
    error.put("status",    404);
    error.put("error",     "Not Found");
    error.put("message",   ex.getMessage());

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(error);
    }

    // Handles all other exceptions and returns a 500 error response
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
        Exception ex) {

    log.error("Unexpected exception: {}", ex.getMessage());

    Map<String, Object> error = new HashMap<>();
    error.put("timestamp", LocalDateTime.now().toString());
    error.put("status",    500);
    error.put("error",     "Internal Server Error");
    error.put("message",   "An unexpected error occurred");

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(error);
    }
}