package ru.alex9043.addressservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressesResponseDto {
    private Set<AddressResponseDto> addresses = new HashSet<>();

    @Data
    public static class AddressResponseDto {
        private UUID id;
        private String name;
        private String street;
        private Integer houseNumber;
        private Integer building;
        private Integer entrance;
        private String floor;
        private String apartmentNumber;
        private UUID accountId;
        private DistrictsResponseDto.DistrictResponseDto district;
    }
}
