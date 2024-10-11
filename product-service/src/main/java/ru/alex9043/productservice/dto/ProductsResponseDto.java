package ru.alex9043.productservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class ProductsResponseDto {
    private Set<ProductResponseDto> products;
}
