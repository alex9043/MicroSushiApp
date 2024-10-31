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
import ru.alex9043.productservice.dto.CategoriesResponseDto;
import ru.alex9043.productservice.dto.CategoryRequestDto;
import ru.alex9043.productservice.service.CategoryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category Management", description = "Управление категориями продуктов")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Получить список всех категорий",
            description = "Возвращает список всех категорий.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список категорий успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriesResponseDto.class)))
    })
    @GetMapping
    public CategoriesResponseDto getCategories() {
        log.info("Received request to get all categories.");
        return categoryService.getCategories();
    }

    @Operation(summary = "Получить категорию по ID",
            description = "Возвращает данные о категории по указанному ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные о категории успешно получены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriesResponseDto.CategoryResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content)
    })
    @GetMapping("{categoryId}")
    public CategoriesResponseDto.CategoryResponseDto getCategory(
            @Parameter(description = "UUID категории", required = true)
            @PathVariable("categoryId") UUID categoryId) {
        log.info("Received request for category with ID: {}", categoryId);
        return categoryService.getCategory(categoryId);
    }

    @Operation(summary = "Создать новую категорию",
            description = "Позволяет администратору создать новую категорию.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Категория успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriesResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные данные категории", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public CategoriesResponseDto createCategory(
            @Parameter(description = "Данные новой категории", required = true)
            @Valid @RequestBody CategoryRequestDto categoryRequestDTO) {
        log.info("Received request to create a new category with name: {}", categoryRequestDTO.getName());
        return categoryService.createCategory(categoryRequestDTO);
    }

    @Operation(summary = "Обновить категорию по ID", description = "Позволяет администратору обновить данные о категории по указанному ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория успешно обновлена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriesResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{categoryId}")
    public CategoriesResponseDto updateCategory(
            @Parameter(description = "UUID категории", required = true)
            @PathVariable("categoryId") UUID categoryId,
            @Parameter(description = "Обновленные данные категории", required = true)
            @Valid @RequestBody CategoryRequestDto categoryRequestDTO) {
        log.info("Received request to update category with ID: {}", categoryId);
        return categoryService.updateCategory(categoryId, categoryRequestDTO);
    }

    @Operation(summary = "Удалить категорию по ID",
            description = "Позволяет администраттору удалить категорию по указанному ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория успешно удалена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriesResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{categoryId}")
    public CategoriesResponseDto deleteCategory(
            @Parameter(description = "UUID категории", required = true)
            @PathVariable("categoryId") UUID categoryId) {
        log.info("Received request to delete category with ID: {}", categoryId);
        return categoryService.deleteCategory(categoryId);
    }
}