package ru.alex9043.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CategoriesResponseDto {
    private Set<CategoryResponseDto> categories;

    @Data
    public static class CategoryResponseDto {
        private UUID id;
        private String name;
    }
}
