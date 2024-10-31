package ru.alex9043.productservice.controller;

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
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public CategoriesResponseDto getCategories() {
        log.info("Received request to get all categories.");
        return categoryService.getCategories();
    }

    @GetMapping("{categoryId}")
    public CategoriesResponseDto.CategoryResponseDto getCategory(@PathVariable("categoryId") UUID categoryId) {
        log.info("Received request for category with ID: {}", categoryId);
        return categoryService.getCategory(categoryId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CategoriesResponseDto createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDTO) {
        log.info("Received request to create a new category with name: {}", categoryRequestDTO.getName());
        return categoryService.createCategory(categoryRequestDTO);
    }

    @PutMapping("{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CategoriesResponseDto updateCategory(@PathVariable("categoryId") UUID categoryId,
                                                @Valid @RequestBody CategoryRequestDto categoryRequestDTO) {
        log.info("Received request to update category with ID: {}", categoryId);
        return categoryService.updateCategory(categoryId, categoryRequestDTO);
    }

    @DeleteMapping("{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CategoriesResponseDto deleteCategory(@PathVariable("categoryId") UUID categoryId) {
        log.info("Received request to delete category with ID: {}", categoryId);
        return categoryService.deleteCategory(categoryId);
    }
}