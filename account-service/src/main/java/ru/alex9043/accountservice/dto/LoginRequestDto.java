package ru.alex9043.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {
    @Pattern(message = "Invalid phone number format",
            regexp = "^([+]?\\d{1,2}[-\\s]?|)\\d{3}[-\\s]?\\d{3}[-\\s]?\\d{4}$")
    @NotBlank(message = "Phone number is required")
    private String phone;
    @Size(min = 8, max = 255, message = "Name must be between 8 and 255 characters")
    @NotBlank(message = "Password is required")
    private String password;
}