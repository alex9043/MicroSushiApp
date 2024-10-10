package ru.alex9043.authservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserRequestDTO {
    private String username;
    private List<String> roles;
}
