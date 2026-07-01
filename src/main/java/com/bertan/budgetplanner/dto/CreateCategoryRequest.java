package com.bertan.budgetplanner.dto;

import com.bertan.budgetplanner.domain.category.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCategoryRequest(
    @NotBlank(message = "Name is required") String name,
    @NotNull(message = "Type is required") Type type
) {
}
