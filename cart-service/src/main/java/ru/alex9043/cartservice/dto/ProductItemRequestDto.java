package ru.alex9043.cartservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductItemRequestDto {
    private UUID productId;
}
