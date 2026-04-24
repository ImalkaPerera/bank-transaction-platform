package com.bank.transaction_service.exception;

import com.bank.logging.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class TransactionNotFoundException extends BusinessException {
    public TransactionNotFoundException(String id) {
        super("Resource not found", "Transaction not found with id: " + id, HttpStatus.NOT_FOUND, "MEDIUM");
    }
}
