package ru.alex9043.authservice.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alex9043.authservice.service.AuthService;
import ru.alex9043.commondto.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("subject")
    public SubjectResponseDto getSubject(@RequestBody TokenRequestDto token) {
        return authService.getSubject(token);
    }

    @PostMapping("generate-tokens")
    public TokensResponseDTO generateTokens(@RequestBody UserRequestDTO userRequestDTO) {
        return authService.generateTokens(userRequestDTO);
    }

    @PostMapping("validate-token")
    public ValidationResponseDTO validateToken(@RequestBody TokenRequestDto token) {
        return authService.validateToken(token);
    }
}
