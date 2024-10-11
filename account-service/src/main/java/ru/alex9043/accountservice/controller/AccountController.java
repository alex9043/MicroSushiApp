package ru.alex9043.accountservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.alex9043.accountservice.dto.*;
import ru.alex9043.accountservice.service.AccountService;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public AccountResponseDto getAccount(@RequestHeader("Authorization") String token) {
        log.info("Get account for token: {}", token);
        return accountService.getAccount(token);
    }

    @PostMapping("register")
    public TokensResponseDTO register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return accountService.register(registerRequestDTO);
    }

    @PostMapping("login")
    public TokensResponseDTO login(@RequestBody LoginRequestDTO registerRequestDTO) {
        return accountService.login(registerRequestDTO);
    }

    @PostMapping("refresh-token")
    public TokensResponseDTO refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        return accountService.refreshToken(refreshTokenDto);
    }
}
