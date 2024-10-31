package ru.alex9043.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class IngredientsResponseDto {
    private Set<IngredientResponseDto> ingredients;

    @Data
    public static class IngredientResponseDto {
        private UUID id;
        private String name;
    }
}
