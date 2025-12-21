package com.bertan.budgetplanner.dto;

import com.bertan.budgetplanner.domain.Type;

public record CreateCategoryDTO(
    String name,
    Type type
) {
}
