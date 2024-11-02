package ru.alex9043.addressservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DistrictRequestDto {
    @NotBlank(message = "District is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;
}
