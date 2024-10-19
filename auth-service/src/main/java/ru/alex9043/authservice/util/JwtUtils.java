package ru.alex9043.authservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.alex9043.authservice.dto.SubjectResponseDto;
import ru.alex9043.authservice.dto.TokenRequestDto;

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

    public boolean validateToken(String authToken) {
        log.info("token - {}", authToken);
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired_JWT_TOKEN");
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            log.info("INVALID_JWT_TOKEN");
        } catch (IllegalArgumentException e) {
            log.info("Token validation error {}", e.getMessage());
        } catch (Exception e) {
            log.info("Token error {}", e.getMessage());
        }
        return false;
    }

    public SubjectResponseDto getSubject(TokenRequestDto token) {
        Claims claims = extractClaims(token);

        return SubjectResponseDto.builder()
                .subject(claims.getSubject())
                .roles(claims.get("roles", List.class))
                .build();
    }
}
