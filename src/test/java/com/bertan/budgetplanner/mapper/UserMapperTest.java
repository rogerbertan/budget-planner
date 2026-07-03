package com.bertan.budgetplanner.mapper;

import com.bertan.budgetplanner.domain.user.User;
import com.bertan.budgetplanner.dto.RegisterUserRequest;
import com.bertan.budgetplanner.dto.RegisterUserResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldMapRegisterUserRequestToUser() {
        RegisterUserRequest request = new RegisterUserRequest("Roger Bertan", "roger@example.com", "encoded-password");

        User user = userMapper.toUser(request);

        assertThat(user.getName()).isEqualTo("Roger Bertan");
        assertThat(user.getUsername()).isEqualTo("roger@example.com");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(user.getId()).isNull();
    }

    @Test
    void shouldMapUserToRegisterUserResponse() {
        User user = new User("Roger Bertan", "roger@example.com", "encoded-password");

        RegisterUserResponse response = userMapper.toRegisterUserResponse(user);

        assertThat(response.name()).isEqualTo("Roger Bertan");
        assertThat(response.email()).isEqualTo("roger@example.com");
    }
}