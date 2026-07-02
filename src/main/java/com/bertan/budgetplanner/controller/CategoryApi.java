package com.bertan.budgetplanner.controller;

import com.bertan.budgetplanner.dto.CategoryResponse;
import com.bertan.budgetplanner.dto.CreateCategoryRequest;
import com.bertan.budgetplanner.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
        name = "Categorias",
        description = "Endpoints para gerenciamento de categorias"
)
public interface CategoryApi {

    @Operation(
            summary = "Listar categorias",
            description = "Retorna todas as categorias cadastradas",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categorias retornadas com sucesso",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token de autenticação ausente ou inválido"
                    )
            }
    )
    ResponseEntity<List<CategoryResponse>> getAllCategories();

    @Operation(
            summary = "Criar categoria",
            description = "Cria uma nova categoria",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Categoria criada com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = CategoryResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados da categoria inválidos",
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
    ResponseEntity<CategoryResponse> createCategory(
            @RequestBody
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da categoria",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateCategoryRequest.class),
                            examples = @ExampleObject(
                                    name = "Categoria válida",
                                    value = """
                                            {
                                                "name": "Alimentação",
                                                "type": "EXPENSE"
                                            }
                                            """
                            )
                    )
            )
            CreateCategoryRequest requestDTO);

    @Operation(
            summary = "Atualizar categoria",
            description = "Atualiza uma categoria existente",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categoria atualizada com sucesso",
                            content = @Content(
                                    schema = @Schema(implementation = CategoryResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados da categoria inválidos",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Categoria não encontrada",
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
    ResponseEntity<CategoryResponse> updateCategory(
            Long id,
            @RequestBody
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da categoria",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateCategoryRequest.class),
                            examples = @ExampleObject(
                                    name = "Categoria válida",
                                    value = """
                                            {
                                                "name": "Alimentação",
                                                "type": "EXPENSE"
                                            }
                                            """
                            )
                    )
            )
            CreateCategoryRequest requestDTO);

    @Operation(
            summary = "Remover categoria",
            description = "Remove uma categoria existente",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Categoria removida com sucesso"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Categoria não encontrada",
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
    ResponseEntity<Void> deleteCategory(Long id);
}
