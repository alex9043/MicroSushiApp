package ru.alex9043.accountservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRequestDto {
    private String token;
}
