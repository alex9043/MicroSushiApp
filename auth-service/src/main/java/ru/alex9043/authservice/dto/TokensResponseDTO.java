package ru.alex9043.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokensResponseDTO {
    private String accessToken;
    private String refreshToken;
}
