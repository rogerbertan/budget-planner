package com.bertan.budgetplanner.dto;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {
}
