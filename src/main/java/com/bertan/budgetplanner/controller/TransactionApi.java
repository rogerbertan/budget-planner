package com.bertan.budgetplanner.controller;

import com.bertan.budgetplanner.dto.CreateTransactionRequest;
import com.bertan.budgetplanner.dto.ErrorResponse;
import com.bertan.budgetplanner.dto.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "Transações",
        description = "Endpoints para gerenciamento de transações financeiras"
)
public interface TransactionApi {

    @Operation(
            summary = "Listar transações",
            description = "Retorna as transações de forma paginada",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Transações retornadas com sucesso",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = TransactionResponse.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token de autenticação ausente ou inválido"
                    )
            }
    )
    ResponseEntity<Page<TransactionResponse>> getAllTransactions(Pageable pageable);

    @Operation(
            summary = "Buscar transação por id",
            description = "Retorna os dados de uma transação específica",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Transação retornada com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = TransactionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Transação não encontrada",
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
    ResponseEntity<TransactionResponse> getTransactionById(Long id);

    @Operation(
            summary = "Criar transação",
            description = "Cria uma nova transação financeira",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Transação criada com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = TransactionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados da transação inválidos",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Categoria informada não encontrada",
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
    ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da transação",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateTransactionRequest.class),
                            examples = @ExampleObject(
                                    name = "Transação válida",
                                    value = """
                                            {
                                                "type": "EXPENSE",
                                                "amount": 150.50,
                                                "description": "Supermercado",
                                                "categoryId": 1,
                                                "transactionDate": "2026-07-02"
                                            }
                                            """
                            )
                    )
            )
            CreateTransactionRequest requestDTO);

    @Operation(
            summary = "Atualizar transação",
            description = "Atualiza uma transação existente",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Transação atualizada com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = TransactionResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados da transação inválidos",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Transação ou categoria informada não encontrada",
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
    ResponseEntity<TransactionResponse> updateTransaction(
            Long id,
            @RequestBody
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da transação",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateTransactionRequest.class),
                            examples = @ExampleObject(
                                    name = "Transação válida",
                                    value = """
                                            {
                                                "type": "EXPENSE",
                                                "amount": 150.50,
                                                "description": "Supermercado",
                                                "categoryId": 1,
                                                "transactionDate": "2026-07-02"
                                            }
                                            """
                            )
                    )
            )
            CreateTransactionRequest requestDTO);

    @Operation(
            summary = "Remover transação",
            description = "Remove uma transação existente",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Transação removida com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Transação não encontrada",
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
    ResponseEntity<Void> deleteTransaction(Long id);
}
