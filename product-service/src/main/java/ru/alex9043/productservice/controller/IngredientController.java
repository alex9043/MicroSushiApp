package ru.alex9043.productservice.controller;

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
public class IngredientController {
    private final IngredientService ingredientService;

    @GetMapping
    public IngredientsResponseDto getIngredients() {
        log.info("Received request to get all ingredients.");
        return ingredientService.getIngredients();
    }

    @GetMapping("{ingredientId}")
    public IngredientsResponseDto.IngredientResponseDto getIngredient(@PathVariable("ingredientId") UUID ingredientId) {
        log.info("Received request for ingredient with ID: {}", ingredientId);
        return ingredientService.getIngredient(ingredientId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public IngredientsResponseDto createIngredient(@Valid @RequestBody IngredientRequestDto ingredientRequestDTO) {
        log.info("Received request to create a new ingredient with name: {}", ingredientRequestDTO.getName());
        return ingredientService.createIngredient(ingredientRequestDTO);
    }

    @PutMapping("{ingredientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public IngredientsResponseDto updateIngredient(@PathVariable("ingredientId") UUID ingredientId,
                                                   @Valid @RequestBody IngredientRequestDto ingredientRequestDTO) {
        log.info("Received request to update ingredient with ID: {}", ingredientId);
        return ingredientService.updateIngredient(ingredientId, ingredientRequestDTO);
    }

    @DeleteMapping("{ingredientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public IngredientsResponseDto deleteIngredient(@PathVariable("ingredientId") UUID ingredientId) {
        log.info("Received request to delete ingredient with ID: {}", ingredientId);
        return ingredientService.deleteIngredient(ingredientId);
    }
}