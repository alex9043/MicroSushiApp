package ru.alex9043.accountservice.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String phone;
    private String password;
}
