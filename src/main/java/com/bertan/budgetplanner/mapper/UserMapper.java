package com.bertan.budgetplanner.mapper;

import com.bertan.budgetplanner.domain.user.User;
import com.bertan.budgetplanner.dto.RegisterUserRequest;
import com.bertan.budgetplanner.dto.RegisterUserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(RegisterUserRequest request) {
        return new User(
                request.name(),
                request.email(),
                request.password()
        );
    }

    public RegisterUserResponse toRegisterUserResponse(User user) {
        return new RegisterUserResponse(
                user.getName(),
                user.getUsername()
        );
    }
}
