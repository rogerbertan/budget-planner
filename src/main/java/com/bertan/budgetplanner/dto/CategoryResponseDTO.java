package com.bertan.budgetplanner.dto;

import com.bertan.budgetplanner.domain.Type;

public record CategoryResponseDTO(
    Long id,
    String name,
    Type type
) {
}
