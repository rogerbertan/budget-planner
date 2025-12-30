package com.bertan.budgetplanner.dto;

import java.math.BigDecimal;

public record CategoriesSummaryResponseDTO(
    String category,
    BigDecimal totalIncome,
    BigDecimal totalExpense
) {
}
