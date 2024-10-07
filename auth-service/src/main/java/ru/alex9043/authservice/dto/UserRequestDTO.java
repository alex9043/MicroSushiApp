package ru.alex9043.authservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserRequestDTO {
    private String username;
    private List<String> roles;
}
