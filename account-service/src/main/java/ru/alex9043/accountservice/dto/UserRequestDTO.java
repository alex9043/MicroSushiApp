package ru.alex9043.accountservice.dto;

import lombok.Builder;
import lombok.Data;
import ru.alex9043.accountservice.model.Role;

import java.util.Set;

@Data
@Builder
public class UserRequestDTO {
    private String username;
    private Set<Role> roles;
}
