package com.bank.transaction_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private String id;
    private String fromAccountId;
    private String toAccountId;
    private Double amount;
    private TransactionType type;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;

    public static Transaction create(
            String fromAccountId,
            String toAccountId,
            Double amount,
            TransactionType type,
            String description) {

        return Transaction.builder()
                .id(UUID.randomUUID().toString())
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .type(type)
                .status(TransactionStatus.PENDING)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();
    }
}