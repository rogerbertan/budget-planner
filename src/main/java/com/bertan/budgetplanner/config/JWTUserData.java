package com.bertan.budgetplanner.config;

public record JWTUserData(
        Long userId,
        String email
) {
}