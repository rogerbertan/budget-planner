package com.bertan.budgetplanner.config;

import com.bertan.budgetplanner.domain.user.Role;

public record JWTUserData(
        Long userId,
        String email,
        Role role
) {
}