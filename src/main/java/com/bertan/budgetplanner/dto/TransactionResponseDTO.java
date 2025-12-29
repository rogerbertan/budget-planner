package com.bertan.budgetplanner.dto;

import com.bertan.budgetplanner.domain.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        Type type,
        BigDecimal amount,
        String description,
        Long category,
        LocalDate transactionDate,
        LocalDateTime createdAt
) {
}
