package ru.alex9043.accountservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.alex9043.accountservice.dto.AccountResponseDto;
import ru.alex9043.accountservice.dto.LoginRequestDto;
import ru.alex9043.accountservice.dto.RefreshTokenDto;
import ru.alex9043.accountservice.dto.RegisterRequestDto;
import ru.alex9043.accountservice.service.AccountService;
import ru.alex9043.commondto.TokensResponseDTO;

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
    public TokensResponseDTO register(@Valid @RequestBody RegisterRequestDto registerRequestDTO) {
        return accountService.register(registerRequestDTO);
    }

    @PostMapping("login")
    public TokensResponseDTO login(@Valid @RequestBody LoginRequestDto registerRequestDTO) {
        return accountService.login(registerRequestDTO);
    }

    @PostMapping("refresh-token")
    public TokensResponseDTO refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        return accountService.refreshToken(refreshTokenDto);
    }
}
