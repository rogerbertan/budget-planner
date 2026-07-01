package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.config.TokenConfig;
import com.bertan.budgetplanner.domain.user.User;
import com.bertan.budgetplanner.dto.LoginRequest;
import com.bertan.budgetplanner.dto.LoginResponse;
import com.bertan.budgetplanner.dto.RegisterUserRequest;
import com.bertan.budgetplanner.dto.RegisterUserResponse;
import com.bertan.budgetplanner.mapper.UserMapper;
import com.bertan.budgetplanner.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthService(UserMapper userMapper,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        TokenConfig tokenConfig) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    public RegisterUserResponse registerUser(RegisterUserRequest request) {
        String encodedPassword = passwordEncoder.encode(request.password());
        RegisterUserRequest encodedRequest = new RegisterUserRequest(request.name(), request.email(), encodedPassword);

        User user = userMapper.toUser(encodedRequest);
        User saved = userRepository.save(user);
        return userMapper.toRegisterUserResponse(saved);
    }

    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);

        return new LoginResponse(token);
    }
}
