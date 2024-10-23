package ru.alex9043.commondto;

public class TokenRequestDto {
    private String token;

    public TokenRequestDto() {
    }

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
