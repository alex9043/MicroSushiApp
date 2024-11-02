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
public class DistrictsResponseDto {
    private Set<DistrictResponseDto> districts = new HashSet<>();


    @Data
    public static class DistrictResponseDto {
        private UUID id;
        private String name;
    }
}
