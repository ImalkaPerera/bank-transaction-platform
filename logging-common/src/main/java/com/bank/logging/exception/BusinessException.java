package com.bank.logging.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends BaseException {
    public BusinessException(String logMessage, String detailMessage, HttpStatus status) {
        super(logMessage, detailMessage, "BUSINESS", "LOW", status);
    }
    
    public BusinessException(String logMessage, String detailMessage, HttpStatus status, String severity) {
        super(logMessage, detailMessage, "BUSINESS", severity, status);
    }
}
