package com.bank.logging.exception;

import org.springframework.http.HttpStatus;

public class SystemException extends BaseException {
    public SystemException(String message, Throwable cause) {
        super("System failure occurred", message, "SYSTEM", "HIGH", HttpStatus.INTERNAL_SERVER_ERROR);
        if (cause != null) {
            initCause(cause);
        }
    }
}
