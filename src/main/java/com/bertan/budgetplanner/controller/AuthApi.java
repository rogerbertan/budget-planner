package com.bertan.budgetplanner.controller;

import com.bertan.budgetplanner.dto.ErrorResponse;
import com.bertan.budgetplanner.dto.LoginRequest;
import com.bertan.budgetplanner.dto.LoginResponse;
import com.bertan.budgetplanner.dto.RegisterUserRequest;
import com.bertan.budgetplanner.dto.RegisterUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "Autenticação",
        description = "Endpoints para autenticação e registro de usuários"
)
public interface AuthApi {

    @Operation(
            summary = "Login",
            description = "Autentica um usuário e retorna um token JWT",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login bem-sucedido",
                            content = @Content(
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados de login inválidos",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciais inválidas",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<LoginResponse> login(
            @RequestBody
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados de login do usuário",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Login válido",
                                    value = """
                                            {
                                                "email": "joao@gmail.com",
                                                "password": "joao123"
                                            }
                                            """
                            )
                    )
            )
            LoginRequest request);

    @Operation(
            summary = "Registro",
            description = "Registra um novo usuário",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuário registrado com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = RegisterUserResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados do usuário inválidos",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<RegisterUserResponse> register(
            @RequestBody
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados de registro do usuário",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterUserRequest.class),
                            examples = @ExampleObject(
                                    name = "Registro válido",
                                    value = """
                                            {
                                                "name": "João Silva",
                                                "email": "joao@gmail.com",
                                                "password": "joao123"
                                            }
                                            """
                            )
                    )
            )
            RegisterUserRequest request);
}
