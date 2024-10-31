package ru.alex9043.productservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.alex9043.productservice.dto.IngredientRequestDto;
import ru.alex9043.productservice.dto.IngredientsResponseDto;
import ru.alex9043.productservice.service.IngredientService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/ingredients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ingredient Management", description = "Управление ингредиентами")
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(summary = "Получить список всех ингредиентов",
            description = "Возвращает список всех ингредиентов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список ингредиентов успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredientsResponseDto.class)))
    })
    @GetMapping
    public IngredientsResponseDto getIngredients() {
        log.info("Received request to get all ingredients.");
        return ingredientService.getIngredients();
    }

    @Operation(summary = "Получить ингредиент по ID",
            description = "Возвращает данные об ингредиенте по указанному ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные об ингредиенте успешно получены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredientsResponseDto.IngredientResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ингредиент не найден", content = @Content)
    })
    @GetMapping("{ingredientId}")
    public IngredientsResponseDto.IngredientResponseDto getIngredient(
            @Parameter(description = "UUID ингредиента", required = true)
            @PathVariable("ingredientId") UUID ingredientId) {
        log.info("Received request for ingredient with ID: {}", ingredientId);
        return ingredientService.getIngredient(ingredientId);
    }

    @Operation(summary = "Создать новый ингредиент",
            description = "Позволяет администратору создать новый ингредиент.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ингредиент успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredientsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные данные ингредиента", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public IngredientsResponseDto createIngredient(
            @Parameter(description = "Данные нового ингредиента", required = true)
            @Valid @RequestBody IngredientRequestDto ingredientRequestDTO) {
        log.info("Received request to create a new ingredient with name: {}", ingredientRequestDTO.getName());
        return ingredientService.createIngredient(ingredientRequestDTO);
    }

    @Operation(summary = "Обновить ингредиент по ID",
            description = "Позволяет администратору обновить данные об ингредиенте по указанному ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ингредиент успешно обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredientsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ингредиент не найден", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{ingredientId}")
    public IngredientsResponseDto updateIngredient(
            @Parameter(description = "UUID ингредиента", required = true)
            @PathVariable("ingredientId") UUID ingredientId,
            @Parameter(description = "Обновленные данные ингредиента", required = true)
            @Valid @RequestBody IngredientRequestDto ingredientRequestDTO) {
        log.info("Received request to update ingredient with ID: {}", ingredientId);
        return ingredientService.updateIngredient(ingredientId, ingredientRequestDTO);
    }

    @Operation(summary = "Удалить ингредиент по ID",
            description = "Позволяет администратору удалить ингредиент по указанному ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ингредиент успешно удален",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = IngredientsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ингредиент не найден", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{ingredientId}")
    public IngredientsResponseDto deleteIngredient(
            @Parameter(description = "UUID ингредиента", required = true)
            @PathVariable("ingredientId") UUID ingredientId) {
        log.info("Received request to delete ingredient with ID: {}", ingredientId);
        return ingredientService.deleteIngredient(ingredientId);
    }
}