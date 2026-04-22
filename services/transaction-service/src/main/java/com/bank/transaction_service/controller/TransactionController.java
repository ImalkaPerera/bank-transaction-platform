package com.bank.transaction_service.controller;

import com.bank.transaction_service.model.TransactionRequest;
import com.bank.transaction_service.model.TransactionResponse;
import com.bank.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody TransactionRequest request) {
        log.info("POST /transactions - from: {} to: {} amount: {}",
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount());
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String id) {
        log.info("GET /transactions/{}", id);
        TransactionResponse response = transactionService.getTransaction(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionResponse>> getHistory() {
        log.info("GET /transactions/history");
        List<TransactionResponse> response = transactionService.getHistory();
        return ResponseEntity.ok(response);
    }
}
