package com.bank.account_service.exception;

import com.bank.logging.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String id) {
        super("Resource not found", "Account not found with id: " + id, HttpStatus.NOT_FOUND, "MEDIUM");
    }
}
