package com.bertan.budgetplanner.dto;

import com.bertan.budgetplanner.domain.category.Type;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
        @NotNull(message = "Type is required") Type type,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Category id is required") Long categoryId,
        @NotNull(message = "Transaction date is required") LocalDate transactionDate
) {
}
