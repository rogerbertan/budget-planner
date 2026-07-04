package com.bertan.budgetplanner.controller;

import com.bertan.budgetplanner.config.JWTUserData;
import com.bertan.budgetplanner.dto.BalanceResponse;
import com.bertan.budgetplanner.dto.CategoriesSummaryResponse;
import com.bertan.budgetplanner.dto.ErrorResponse;
import com.bertan.budgetplanner.dto.MonthlySummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(
        name = "Resumo",
        description = "Endpoints para resumos financeiros"
)
public interface SummaryApi {

    @Operation(
            summary = "Saldo geral",
            description = "Retorna o saldo geral (receitas menos despesas)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Saldo retornado com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = BalanceResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token de autenticação ausente ou inválido"
                    )
            }
    )
    ResponseEntity<BalanceResponse> getBalanceSummary(@AuthenticationPrincipal JWTUserData principal);

    @Operation(
            summary = "Resumo mensal",
            description = "Retorna o resumo de receitas, despesas e saldo de um mês específico",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Resumo mensal retornado com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = MonthlySummaryResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parâmetros 'month'/'year' ausentes ou inválidos",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token de autenticação ausente ou inválido"
                    )
            }
    )
    ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @Parameter(description = "Mês de referência (1-12)", example = "7") int month,
            @Parameter(description = "Ano de referência", example = "2026") int year,
            @AuthenticationPrincipal JWTUserData principal);

    @Operation(
            summary = "Resumo por categoria",
            description = "Retorna o resumo de receitas e despesas agrupado por categoria em um mês específico",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Resumo por categoria retornado com sucesso",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = CategoriesSummaryResponse.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parâmetros 'month'/'year' ausentes ou inválidos",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token de autenticação ausente ou inválido"
                    )
            }
    )
    ResponseEntity<List<CategoriesSummaryResponse>> getCategoriesSummary(
            @Parameter(description = "Mês de referência (1-12)", example = "7") int month,
            @Parameter(description = "Ano de referência", example = "2026") int year,
            @AuthenticationPrincipal JWTUserData principal);
}
