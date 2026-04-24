package com.bank.account_service.service;

import com.bank.account_service.exception.AccountNotFoundException;
import com.bank.account_service.model.Account;
import com.bank.account_service.model.AccountResponse;
import com.bank.account_service.model.CreateAccountRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger("AccountService");

    // In-memory storage (no database yet)
    private final Map<String, Account> accounts = new HashMap<>();

    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for owner: {}", request.getOwnerName());

        Account account = Account.create(
                request.getOwnerName(),
                request.getEmail(),
                request.getInitialBalance()
        );

        accounts.put(account.getId(), account);

        log.info("Account created successfully with id: {}", account.getId());

        return AccountResponse.builder()
                .id(account.getId())
                .ownerName(account.getOwnerName())
                .email(account.getEmail())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .message("Account created successfully")
                .build();
    }

    public AccountResponse getAccount(String id) {
        log.info("Fetching account with id: {}", id);

        Account account = accounts.get(id);

        if (account == null) {
            throw new AccountNotFoundException(id);
        }

        log.info("Account found for owner: {}", account.getOwnerName());

        return AccountResponse.builder()
                .id(account.getId())
                .ownerName(account.getOwnerName())
                .email(account.getEmail())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .message("Account fetched successfully")
                .build();
    }

    public Double getBalance(String id) {
        log.info("Checking balance for account id: {}", id);

        Account account = accounts.get(id);

        if (account == null) {
            throw new AccountNotFoundException(id);
        }

        log.info("Balance for account {}: {}", id, account.getBalance());

        return account.getBalance();
    }
}