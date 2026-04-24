package com.bank.logging.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final String logMessage;
    private final String category;
    private final String severity;
    private final HttpStatus status;

    protected BaseException(String logMessage, String detailMessage, String category, String severity, HttpStatus status) {
        super(detailMessage);
        this.logMessage = logMessage;
        this.category = category;
        this.severity = severity;
        this.status = status;
    }
}
