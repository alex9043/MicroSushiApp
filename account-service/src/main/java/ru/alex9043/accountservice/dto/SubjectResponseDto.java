package ru.alex9043.accountservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class SubjectResponseDto {
    private String subject;
    private Set<String> roles;
}