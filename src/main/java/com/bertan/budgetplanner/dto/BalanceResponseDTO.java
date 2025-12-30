package com.bertan.budgetplanner.dto;

import java.math.BigDecimal;

public record BalanceResponseDTO(
        BigDecimal balance
) {
}
