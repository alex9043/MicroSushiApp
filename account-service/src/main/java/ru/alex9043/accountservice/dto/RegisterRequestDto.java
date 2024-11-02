package ru.alex9043.accountservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequestDto {
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
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
    @Size(min = 8, message = "Password must be longer than 7 characters")
    @NotBlank(message = "Password is required")
    private String password;
    @Size(min = 8, message = "ConfirmPassword must be longer than 7 characters")
    @NotBlank(message = "ConfirmPassword is required")
    private String confirmPassword;
}