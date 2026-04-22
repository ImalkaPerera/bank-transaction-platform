package com.bank.transaction_service.model;

import lombok.Data;

@Data
public class TransactionRequest {
    private String fromAccountId;
    private String toAccountId;
    private Double amount;
    private TransactionType type;
    private String description;
}