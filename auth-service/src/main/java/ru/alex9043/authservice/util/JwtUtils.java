package ru.alex9043.authservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.alex9043.commondto.SubjectResponseDto;
import ru.alex9043.commondto.TokenRequestDto;
import ru.alex9043.commondto.ValidationResponseDTO;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
@Slf4j
public class JwtUtils {

    private static final SecretKey PRIVATE_KEY;

    static {
        try {
            String keyContent = Files.readString(Path.of("/ssh-keys/secret.key")).trim();
            PRIVATE_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(keyContent));
        } catch (Exception e) {
            log.error("Failed to load the private key", e);
            throw new RuntimeException("Не удалось загрузить ключ", e);
        }
    }


    private static Claims extractClaims(TokenRequestDto token) {
        return Jwts.
                parserBuilder()
                .deserializeJsonWith(new JacksonDeserializer<>())
                .setSigningKey(PRIVATE_KEY)
                .build()
                .parseClaimsJws(token.getToken())
                .getBody();
    }

    public String generateAccessToken(UUID id, String username, Set<String> roles) {
        log.info("Generating access token for user: {}", username);
        log.info("Generating access token");
        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(PRIVATE_KEY)
                .compact();
    }

    public String generateRefreshToken() {
        log.info("Generating refresh token");
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(PRIVATE_KEY)
                .compact();
    }

    public ValidationResponseDTO validateToken(String authToken) {
        String validationMessage = null;
        try {
            Jwts.parserBuilder().setSigningKey(PRIVATE_KEY).build().parseClaimsJws(authToken);
            log.debug("Token is valid");
            return new ValidationResponseDTO(true, validationMessage);
        } catch (ExpiredJwtException e) {
            validationMessage = "Expired_JWT_TOKEN";
            log.error(validationMessage, e);
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            validationMessage = "INVALID_JWT_TOKEN";
            log.error(validationMessage, e);
        } catch (IllegalArgumentException e) {
            validationMessage = "Token validation error " + e.getMessage();
            log.error(validationMessage, e);
        } catch (Exception e) {
            validationMessage = "Token error " + e.getMessage();
            log.error(validationMessage, e);
        }
        return new ValidationResponseDTO(false, validationMessage);
    }

    public SubjectResponseDto getSubject(TokenRequestDto token) {
        Claims claims = extractClaims(token);
        log.debug("Extracted claims from token: {}", claims);
        return new SubjectResponseDto(UUID.fromString(claims.get("id", String.class)), claims.getSubject(), claims.get("roles", List.class));
    }
}
