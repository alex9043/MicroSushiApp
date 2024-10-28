package ru.alex9043.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountsResponseDto {
    private Set<AccountResponseDto> accounts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountResponseDto {
        private UUID id;
        private String name;
        private String phone;
        private String email;
        private LocalDate dateOfBirth;
    }
}
