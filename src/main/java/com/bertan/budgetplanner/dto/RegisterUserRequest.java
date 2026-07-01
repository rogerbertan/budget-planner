package com.bertan.budgetplanner.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Password is required") String password
) {
}
