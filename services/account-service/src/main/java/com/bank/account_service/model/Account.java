package com.bank.account_service.model;

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
public class Account {

    private String id;
    private String ownerName;
    private String email;
    private Double balance;
    private LocalDateTime createdAt;

    // Auto generate ID and timestamp
    public static Account create(String ownerName, String email, Double initialBalance) {
        return Account.builder()
                .id(UUID.randomUUID().toString())
                .ownerName(ownerName)
                .email(email)
                .balance(initialBalance)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
