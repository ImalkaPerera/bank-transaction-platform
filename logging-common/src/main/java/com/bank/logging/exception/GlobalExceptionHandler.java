package com.bank.logging.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        // Classify the error
        String type = ex.getClass().getSimpleName();
        String category = classifyCategory(ex);
        String severity = classifySeverity(ex);

        // Put metadata in MDC for the ELK pipeline
        MDC.put("error.type", type);
        MDC.put("error.category", category);
        MDC.put("error.severity", severity);
        
        log.error("Request failed - [{}]: {}", type, ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred") // Don't leak internals to client
                .type(type)
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String classifyCategory(Exception ex) {
        String name = ex.getClass().getName();
        if (name.contains("IllegalArgument") || name.contains("Constraint") || name.contains("Validation")) {
            return "BUSINESS";
        }
        return "SYSTEM";
    }

    private String classifySeverity(Exception ex) {
        if (classifyCategory(ex).equals("BUSINESS")) {
            return "LOW";
        }
        return "HIGH";
    }

    @Getter
    @Builder
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private String type;
    }
}