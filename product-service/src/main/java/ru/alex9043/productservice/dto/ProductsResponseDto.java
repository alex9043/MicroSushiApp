package ru.alex9043.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductsResponseDto {
    private Set<ProductResponseDto> products;

    @Data
    public static class ProductResponseDto {
        private UUID id;
        private String name;
        private BigDecimal price;
        private byte[] image;
        private Set<CategoriesResponseDto.CategoryResponseDto> categories;
        private Set<IngredientsResponseDto.IngredientResponseDto> ingredients;
    }

}
