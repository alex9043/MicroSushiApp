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
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class JwtUtils {

    private static final SecretKey PRIVATE_KEY;

    static {
        try {
            String keyContent = Files.readString(Path.of("/ssh-keys/secret.key")).trim();
            PRIVATE_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(keyContent));
        } catch (Exception e) {
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

    public String generateAccessToken(String username, Set<String> roles) {
        log.info("Generating access token");
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(PRIVATE_KEY)
                .compact();
    }

    public String generateRefreshToken() {
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
            return new ValidationResponseDTO(true, validationMessage);
        } catch (ExpiredJwtException e) {
            validationMessage = "Expired_JWT_TOKEN";
            log.info(validationMessage);
            log.info(e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            validationMessage = "INVALID_JWT_TOKEN";
            log.info(validationMessage);
            log.info(e.getMessage());
        } catch (IllegalArgumentException e) {
            validationMessage = "Token validation error " + e.getMessage();
            log.info(validationMessage);
            log.info(e.getMessage());
        } catch (Exception e) {
            validationMessage = "Token error " + e.getMessage();
            log.info(validationMessage);
            log.info(e.getMessage());
        }
        return new ValidationResponseDTO(false, validationMessage);
    }

    public SubjectResponseDto getSubject(TokenRequestDto token) {
        Claims claims = extractClaims(token);

        return new SubjectResponseDto(claims.getSubject(), claims.get("roles", List.class));
    }
}
