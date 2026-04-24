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
        // Default classification
        String type = ex.getClass().getSimpleName();
        String category = "SYSTEM";
        String severity = "HIGH";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String clientMessage = "An unexpected error occurred";
        String logMessage = "Request failed";

        // Extract metadata from semantic exceptions
        if (ex instanceof BaseException baseEx) {
            category = baseEx.getCategory();
            severity = baseEx.getSeverity();
            status = baseEx.getStatus();
            clientMessage = baseEx.getMessage();
            logMessage = baseEx.getLogMessage();
        }

        // Put metadata in MDC for the ELK pipeline
        MDC.put("error.type", type);
        MDC.put("error.category", category);
        MDC.put("error.severity", severity);
        MDC.put("error.message", ex.getMessage()); // Technical detail
        MDC.put("http.status", String.valueOf(status.value()));
        
        // Semantic, clean log message (using the high-level summary)
        log.error("{}: {}", logMessage, ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(clientMessage)
                .type(type)
                .build();

        return new ResponseEntity<>(error, status);
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