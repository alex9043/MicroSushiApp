package ru.alex9043.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alex9043.authservice.util.JwtUtils;
import ru.alex9043.commondto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtUtils jwtUtils;

    public TokensResponseDTO generateTokens(UserRequestDTO userRequestDTO) {
        log.info("Generating tokens for user: {}", userRequestDTO.getUsername());
        return new TokensResponseDTO(
                jwtUtils.generateAccessToken(userRequestDTO.getId(), userRequestDTO.getUsername(), userRequestDTO.getRoles()),
                jwtUtils.generateRefreshToken());
    }

    public ValidationResponseDTO validateToken(TokenRequestDto token) {
        log.info("Validating token: {}", token.getToken());
        return jwtUtils.validateToken(token.getToken());
    }

    public SubjectResponseDto getSubject(TokenRequestDto token) {
        log.info("Extracting subject from token: {}", token.getToken());
        return jwtUtils.getSubject(token);
    }
}
