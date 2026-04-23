package com.bank.transaction_service.service;

import com.bank.transaction_service.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger("TransactionService");

    // In-memory storage
    private final Map<String, Transaction> transactions = new HashMap<>();

    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Creating transaction from account: {} to account: {} amount: {}",
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount());

        // Validate amount
        if (request.getAmount() <= 0) {
            log.error("Invalid amount: {}", request.getAmount());
            throw new RuntimeException("Amount must be greater than zero");
        }

        // Create transaction
        Transaction transaction = Transaction.create(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                request.getType(),
                request.getDescription()
        );

        // Mark as success
        transaction.setStatus(TransactionStatus.SUCCESS);

        // Save
        transactions.put(transaction.getId(), transaction);

        log.info("Transaction created successfully with id: {} status: {}",
                transaction.getId(),
                transaction.getStatus());

        return mapToResponse(transaction, "Transaction created successfully");
    }

    public TransactionResponse getTransaction(String id) {
        log.info("Fetching transaction with id: {}", id);

        Transaction transaction = transactions.get(id);

        if (transaction == null) {
            log.error("Transaction not found with id: {}", id);
            throw new RuntimeException("Transaction not found with id: " + id);
        }

        log.info("Transaction found: {} status: {}",
                transaction.getId(),
                transaction.getStatus());

        return mapToResponse(transaction, "Transaction fetched successfully");
    }

    public List<TransactionResponse> getHistory() {
        log.info("Fetching all transactions, total: {}", transactions.size());

        return transactions.values()
                .stream()
                .map(t -> mapToResponse(t, "OK"))
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction t, String message) {
        return TransactionResponse.builder()
                .id(t.getId())
                .fromAccountId(t.getFromAccountId())
                .toAccountId(t.getToAccountId())
                .amount(t.getAmount())
                .type(t.getType())
                .status(t.getStatus())
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .message(message)
                .build();
    }
}