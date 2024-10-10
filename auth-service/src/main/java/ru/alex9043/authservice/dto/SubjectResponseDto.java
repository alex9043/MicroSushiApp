package ru.alex9043.authservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubjectResponseDto {
    private String subject;
    private List<String> roles;
}
