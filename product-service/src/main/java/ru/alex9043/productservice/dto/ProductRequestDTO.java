package ru.alex9043.productservice.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class ProductRequestDTO {
    private String name;
    private BigDecimal price;
    private String base64Image;
}
