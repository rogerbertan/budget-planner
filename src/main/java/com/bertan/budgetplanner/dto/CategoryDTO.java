package com.bertan.budgetplanner.dto;

import com.bertan.budgetplanner.domain.Type;

public record CategoryDTO(
    Long id,
    String name,
    Type type
) {
}
