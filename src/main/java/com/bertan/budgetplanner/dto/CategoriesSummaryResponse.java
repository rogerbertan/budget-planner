package com.bertan.budgetplanner.dto;

import java.math.BigDecimal;

public record CategoriesSummaryResponse(
    String category,
    BigDecimal totalIncome,
    BigDecimal totalExpense
) {
}
