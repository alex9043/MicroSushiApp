package ru.alex9043.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.alex9043.productservice.dto.IngredientRequestDto;
import ru.alex9043.productservice.dto.IngredientsResponseDto;
import ru.alex9043.productservice.model.Ingredient;
import ru.alex9043.productservice.repository.IngredientRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final ModelMapper modelMapper;

    public Ingredient findById(UUID id) {
        log.debug("Finding ingredient with ID: {}", id);
        return ingredientRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Ingredient with id " + id + " not found")
        );
    }

    public IngredientsResponseDto getIngredients() {
        log.info("Fetching all ingredients.");
        Set<IngredientsResponseDto.IngredientResponseDto> ingredients = ingredientRepository.findAll().stream()
                .map(i -> modelMapper.map(i, IngredientsResponseDto.IngredientResponseDto.class))
                .collect(Collectors.toSet());
        return new IngredientsResponseDto(ingredients);
    }

    public IngredientsResponseDto.IngredientResponseDto getIngredient(UUID id) {
        log.info("Fetching ingredient details for ID: {}", id);
        Ingredient ingredient = findById(id);
        return modelMapper.map(ingredient, IngredientsResponseDto.IngredientResponseDto.class);
    }

    public IngredientsResponseDto createIngredient(IngredientRequestDto ingredientRequestDTO) {
        log.info("Creating ingredient with name: {}", ingredientRequestDTO.getName());
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientRequestDTO.getName());
        ingredientRepository.save(ingredient);
        log.info("Ingredient created successfully with name: {}", ingredient.getName());
        return getIngredients();
    }

    public IngredientsResponseDto updateIngredient(UUID id, IngredientRequestDto ingredientRequestDTO) {
        log.info("Updating ingredient with ID: {}", id);
        Ingredient ingredient = findById(id);
        ingredient.setName(ingredientRequestDTO.getName());
        ingredientRepository.save(ingredient);
        log.info("Ingredient updated successfully with ID: {}", id);
        return getIngredients();
    }

    public IngredientsResponseDto deleteIngredient(UUID id) {
        log.info("Deleting ingredient with ID: {}", id);
        ingredientRepository.deleteById(id);
        log.info("Ingredient deleted successfully with ID: {}", id);
        return getIngredients();
    }
}