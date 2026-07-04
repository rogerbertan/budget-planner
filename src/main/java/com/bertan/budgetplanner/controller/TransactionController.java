package com.bertan.budgetplanner.controller;

import com.bertan.budgetplanner.config.JWTUserData;
import com.bertan.budgetplanner.dto.CreateTransactionRequest;
import com.bertan.budgetplanner.dto.TransactionResponse;
import com.bertan.budgetplanner.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController implements TransactionApi {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal JWTUserData principal) {

        return ResponseEntity.ok(transactionService.findAll(pageable, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id,
            @AuthenticationPrincipal JWTUserData principal) {

        return ResponseEntity.ok(transactionService.getTransactionById(id, principal));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest requestDTO,
            @AuthenticationPrincipal JWTUserData principal) {

        TransactionResponse created = transactionService.createTransaction(requestDTO, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody CreateTransactionRequest requestDTO,
            @AuthenticationPrincipal JWTUserData principal) {

        TransactionResponse updated = transactionService.updateTransaction(id, requestDTO, principal);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal JWTUserData principal) {

        transactionService.deleteTransaction(id, principal);
        return ResponseEntity.noContent().build();
    }
}
