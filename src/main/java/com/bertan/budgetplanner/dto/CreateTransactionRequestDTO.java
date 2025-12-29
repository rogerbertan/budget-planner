package com.bertan.budgetplanner.dto;

import com.bertan.budgetplanner.domain.Type;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequestDTO(
        Type type,
        BigDecimal amount,
        String description,
        Long categoryId,
        LocalDate transactionDate
) {
}
