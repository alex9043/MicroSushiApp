package ru.alex9043.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alex9043.authservice.dto.SubjectResponseDto;
import ru.alex9043.authservice.dto.TokenRequestDto;
import ru.alex9043.authservice.dto.TokensResponseDTO;
import ru.alex9043.authservice.dto.UserRequestDTO;
import ru.alex9043.authservice.util.JwtUtils;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;

    public TokensResponseDTO generateTokens(UserRequestDTO userRequestDTO) {
        return TokensResponseDTO.builder()
                .accessToken(jwtUtils.generateAccessToken(userRequestDTO.getUsername(), userRequestDTO.getRoles()))
                .refreshToken(jwtUtils.generateRefreshToken())
                .build();
    }

    public boolean validateToken(TokenRequestDto token) {
        return jwtUtils.validateToken(token.getToken());
    }

    public SubjectResponseDto getSubject(TokenRequestDto token) {
        return jwtUtils.getSubject(token);
    }
}
