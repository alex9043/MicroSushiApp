package ru.alex9043.productservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class ProductRequestDto {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    @NotNull(message = "Price is required")
    @Min(message = "Price must be more than 0", value = 1)
    private BigDecimal price;
    private String base64Image;
    private Set<UUID> ingredients = new LinkedHashSet<>();
    private Set<UUID> categories = new LinkedHashSet<>();
}
