package com.bank.account_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private String id;
    private String ownerName;
    private String email;
    private Double balance;
    private LocalDateTime createdAt;
    private String message;
}