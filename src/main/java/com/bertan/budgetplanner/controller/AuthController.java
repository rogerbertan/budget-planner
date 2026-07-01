package com.bertan.budgetplanner.controller;

import com.bertan.budgetplanner.dto.LoginRequest;
import com.bertan.budgetplanner.dto.LoginResponse;
import com.bertan.budgetplanner.dto.RegisterUserRequest;
import com.bertan.budgetplanner.dto.RegisterUserResponse;
import com.bertan.budgetplanner.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        String encodedPassword = passwordEncoder.encode(request.password());
        RegisterUserRequest encodedRequest = new RegisterUserRequest(request.name(), request.email(), encodedPassword);

        RegisterUserResponse response = authService.registerUser(encodedRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
