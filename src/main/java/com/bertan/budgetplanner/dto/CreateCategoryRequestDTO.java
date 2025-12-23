package com.bertan.budgetplanner.dto;

import com.bertan.budgetplanner.domain.Type;

public record CreateCategoryRequestDTO(
    String name,
    Type type
) {
}
