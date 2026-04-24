package com.bank.transaction_service.exception;

import com.bank.logging.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidTransactionAmountException extends BusinessException {
    public InvalidTransactionAmountException(Double amount) {
        super("Transaction validation failed", "Amount must be greater than zero. Received: " + amount, HttpStatus.BAD_REQUEST);
    }
}
