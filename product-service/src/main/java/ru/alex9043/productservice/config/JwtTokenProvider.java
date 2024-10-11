package ru.alex9043.productservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.alex9043.productservice.dto.SubjectResponseDto;
import ru.alex9043.productservice.dto.TokenRequestDto;

@Configuration
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final RestTemplate restTemplate;

    public SubjectResponseDto getSubjectAndRoles(String token) {
        TokenRequestDto request = new TokenRequestDto(token);
        ResponseEntity<SubjectResponseDto> response = restTemplate.postForEntity(
                "http://auth-service/api/v1/auth/subject", request, SubjectResponseDto.class
        );
        return response.getBody();
    }
}
