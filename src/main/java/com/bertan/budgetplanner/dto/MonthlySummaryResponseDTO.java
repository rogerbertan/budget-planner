package com.bertan.budgetplanner.dto;

import java.math.BigDecimal;

public record MonthlySummaryResponseDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {
}
