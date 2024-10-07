package ru.alex9043.accountservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountRequestDto {
    private String name;
    private String phone;
    private String email;
    private LocalDate dateOfBirth;
    private String password;
}
