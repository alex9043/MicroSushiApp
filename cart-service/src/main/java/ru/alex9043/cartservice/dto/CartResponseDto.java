package ru.alex9043.cartservice.dto;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CartResponseDto {
    private UUID id;
    private UUID accountId;
    private Set<CartElementResponseDto> cartElements;

    @Data
    public static class CartElementResponseDto {
        private UUID id;
        private UUID productId;
        private Integer quantity;
    }
}
