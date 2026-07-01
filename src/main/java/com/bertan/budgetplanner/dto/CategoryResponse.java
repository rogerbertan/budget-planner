package com.bertan.budgetplanner.dto;

import com.bertan.budgetplanner.domain.category.Type;

public record CategoryResponse(
    Long id,
    String name,
    Type type
) {
}
