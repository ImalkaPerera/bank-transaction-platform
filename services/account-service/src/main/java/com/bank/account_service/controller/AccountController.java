package com.bank.account_service.controller;


import com.bank.account_service.model.AccountResponse;
import com.bank.account_service.model.CreateAccountRequest;
import com.bank.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestBody CreateAccountRequest request) {
        log.info("POST /accounts - creating account for: {}", request.getOwnerName());
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String id) {
        log.info("GET /accounts/{} - fetching account", id);
        AccountResponse response = accountService.getAccount(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable String id) {
        log.info("GET /accounts/{}/balance - checking balance", id);
        Double balance = accountService.getBalance(id);

        Map<String, Object> response = new HashMap<>();
        response.put("accountId", id);
        response.put("balance", balance);
        response.put("message", "Balance fetched successfully");

        return ResponseEntity.ok(response);
    }
}