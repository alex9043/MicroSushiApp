package ru.alex9043.productservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubjectResponseDto {
    private String subject;
    private List<String> roles;
}
