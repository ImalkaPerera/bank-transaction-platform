package com.bank.transaction_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String id;
    private String fromAccountId;
    private String toAccountId;
    private Double amount;
    private TransactionType type;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;
    private String message;
}