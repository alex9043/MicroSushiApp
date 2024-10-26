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
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class JwtUtils {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Date ACCESS_EXPIRATION_TIME = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
    private static final Date REFRESH_EXPIRATION_TIME = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);

    private static Claims extractClaims(TokenRequestDto token) {
        return Jwts.
                parserBuilder()
                .deserializeJsonWith(new JacksonDeserializer<>())
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token.getToken())
                .getBody();
    }

    public String generateAccessToken(String username, Set<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(ACCESS_EXPIRATION_TIME)
                .signWith(SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken() {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(REFRESH_EXPIRATION_TIME)
                .signWith(SECRET_KEY)
                .compact();
    }

    public ValidationResponseDTO validateToken(String authToken) {
        String validationMessage = null;
        log.info("token - {}", authToken);
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(authToken);
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
