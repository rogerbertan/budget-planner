package com.bertan.budgetplanner.service;

import com.bertan.budgetplanner.config.TokenConfig;
import com.bertan.budgetplanner.domain.user.User;
import com.bertan.budgetplanner.dto.LoginRequest;
import com.bertan.budgetplanner.dto.LoginResponse;
import com.bertan.budgetplanner.dto.RegisterUserRequest;
import com.bertan.budgetplanner.dto.RegisterUserResponse;
import com.bertan.budgetplanner.mapper.UserMapper;
import com.bertan.budgetplanner.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenConfig tokenConfig;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserWithEncodedPassword() {
        RegisterUserRequest request = new RegisterUserRequest("Roger Bertan", "roger@example.com", "raw-password");
        RegisterUserRequest encodedRequest = new RegisterUserRequest("Roger Bertan", "roger@example.com", "encoded-password");
        User entity = new User("Roger Bertan", "roger@example.com", "encoded-password");
        User saved = new User("Roger Bertan", "roger@example.com", "encoded-password");
        RegisterUserResponse responseDTO = new RegisterUserResponse("Roger Bertan", "roger@example.com");

        when(passwordEncoder.encode("raw-password")).thenReturn("encoded-password");
        when(userMapper.toUser(encodedRequest)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toRegisterUserResponse(saved)).thenReturn(responseDTO);

        RegisterUserResponse result = authService.registerUser(request);

        assertThat(result).isEqualTo(responseDTO);
        verify(userRepository).save(entity);
    }

    @Test
    void shouldLoginAndReturnTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("roger@example.com", "raw-password");
        User principal = new User("Roger Bertan", "roger@example.com", "encoded-password");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenConfig.generateToken(principal)).thenReturn("jwt-token");

        LoginResponse result = authService.login(request);

        assertThat(result.token()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(eq(new UsernamePasswordAuthenticationToken("roger@example.com", "raw-password")));
    }

    @Test
    void shouldThrowExceptionWhenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest("roger@example.com", "wrong-password");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}