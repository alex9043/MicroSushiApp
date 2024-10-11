package ru.alex9043.gatewayservice.dto;

public class TokenRequestDto {
    private String token;

    public TokenRequestDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
