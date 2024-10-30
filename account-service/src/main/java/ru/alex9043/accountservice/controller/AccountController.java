package ru.alex9043.accountservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.alex9043.accountservice.dto.*;
import ru.alex9043.accountservice.service.AccountService;
import ru.alex9043.commondto.TokensResponseDTO;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/self")
    public AccountsResponseDto.AccountResponseDto getAccount(@RequestHeader("Authorization") String token) {
        return accountService.getAccount(token);
    }

    @GetMapping("{accountId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AccountsResponseDto.AccountResponseDto getAccountForAdmin(@PathVariable("accountId") UUID id) {
        return accountService.getAccountForAdmin(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AccountsResponseDto getAccounts() {
        return accountService.getAccounts();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountsResponseDto createAccount(@Valid @RequestBody AccountRequestDto accountRequestDto) {
        return accountService.createAccount(accountRequestDto);
    }

    @PutMapping("{accountId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AccountsResponseDto updateAccount(@PathVariable("accountId") UUID id, @Valid @RequestBody AccountRequestDto accountRequestDto) {
        return accountService.updateAccount(id, accountRequestDto);
    }

    @DeleteMapping("{accountId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AccountsResponseDto deleteAccount(@PathVariable("accountId") UUID id) {
        return accountService.deleteAccount(id);
    }

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
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
