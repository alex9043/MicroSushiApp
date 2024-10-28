package ru.alex9043.accountservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.alex9043.accountservice.model.Role;

import java.time.LocalDate;
import java.util.Set;

@Data
public class AccountRequestDto {
    @Size(message = "Name must be between 2 and 50 characters", min = 2, max = 50)
    @NotBlank(message = "Name is required")
    private String name;
    @Pattern(message = "Invalid phone number format",
            regexp = "^([+]?\\d{1,2}[-\\s]?|)\\d{3}[-\\s]?\\d{3}[-\\s]?\\d{4}$")
    @NotBlank(message = "Phone number is required")
    private String phone;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    @Size(message = "Password must be longer than 7 characters", min = 8)
    @NotBlank(message = "Password is required")
    private String password;
    private Set<Role> roles;
}