package ru.alex9043.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.alex9043.authservice.util.JwtUtils;
import ru.alex9043.commondto.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;

    public TokensResponseDTO generateTokens(UserRequestDTO userRequestDTO) {
        return new TokensResponseDTO(
                jwtUtils.generateAccessToken(userRequestDTO.getUsername(), userRequestDTO.getRoles()),
                jwtUtils.generateRefreshToken());
    }

    public ValidationResponseDTO validateToken(TokenRequestDto token) {
        return jwtUtils.validateToken(token.getToken());
    }

    public SubjectResponseDto getSubject(TokenRequestDto token) {
        return jwtUtils.getSubject(token);
    }
}
