package ru.alex9043.addressservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class AddressRequestDto {
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;
    @NotBlank(message = "Street is required")
    @Size(min = 2, max = 255, message = "Street must be between 2 and 255 characters")
    private String street;
    @NotNull(message = "House number is required")
    @Positive(message = "House number must be greater than 0")
    private Integer houseNumber;
    @Positive(message = "Building number must be greater than 0")
    private Integer building;
    @Positive(message = "Entrance number must be greater than 0")
    private Integer entrance;
    @Positive(message = "Floor number must be greater than 0")
    private Integer floor;
    @NotNull(message = "Apartment number is required")
    @Positive(message = "Apartment number must be greater than 0")
    private Integer apartmentNumber;
    @NotNull(message = "Account ID is required")
    private UUID accountId;
    @NotNull(message = "District information is required")
    private UUID districtId;
}
