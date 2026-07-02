package com.bertan.budgetplanner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Saúde",
        description = "Endpoint de verificação de status da aplicação"
)
public interface HealthApi {

    @Operation(
            summary = "Verificar status",
            description = "Verifica se a aplicação está em execução",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Aplicação em execução",
                            content = @Content(
                                    schema = @Schema(implementation = String.class, example = "OK")
                            )
                    )
            }
    )
    ResponseEntity<String> health();
}
