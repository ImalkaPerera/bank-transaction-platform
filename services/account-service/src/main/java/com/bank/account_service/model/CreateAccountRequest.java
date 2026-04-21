package com.bank.account_service.model;

import lombok.Data;

@Data
public class CreateAccountRequest {
    private String ownerName;
    private String email;
    private Double initialBalance;
}
