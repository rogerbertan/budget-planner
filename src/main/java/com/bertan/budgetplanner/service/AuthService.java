package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.domain.user.User;
import com.bertan.budgetplanner.dto.RegisterUserRequest;
import com.bertan.budgetplanner.dto.RegisterUserResponse;
import com.bertan.budgetplanner.mapper.UserMapper;
import com.bertan.budgetplanner.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public AuthService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public RegisterUserResponse registerUser(RegisterUserRequest request) {
        User user = userMapper.toUser(request);
        User saved = userRepository.save(user);
        return userMapper.toRegisterUserResponse(saved);
    }
}
