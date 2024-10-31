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
        log.info("Received request for account details with token.");
        return accountService.getAccount(token);
    }

    @GetMapping("{accountId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AccountsResponseDto.AccountResponseDto getAccountForAdmin(@PathVariable("accountId") UUID id) {
        log.info("Received admin request for account with ID: {}", id);
        return accountService.getAccountForAdmin(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AccountsResponseDto getAccounts() {
        log.info("Received request to get all accounts.");
        return accountService.getAccounts();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountsResponseDto createAccount(@Valid @RequestBody AccountRequestDto accountRequestDto) {
        log.info("Received request to create a new account.");
        return accountService.createAccount(accountRequestDto);
    }

    @PutMapping("{accountId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AccountsResponseDto updateAccount(@PathVariable("accountId") UUID id, @Valid @RequestBody AccountRequestDto accountRequestDto) {
        log.info("Received request to update account with id - {}", id);
        return accountService.updateAccount(id, accountRequestDto);
    }

    @DeleteMapping("{accountId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AccountsResponseDto deleteAccount(@PathVariable("accountId") UUID id) {
        log.info("Received request to delete account with id - {}", id);
        return accountService.deleteAccount(id);
    }

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokensResponseDTO register(@Valid @RequestBody RegisterRequestDto registerRequestDTO) {
        log.info("Received request to register");
        return accountService.register(registerRequestDTO);
    }

    @PostMapping("login")
    public TokensResponseDTO login(@Valid @RequestBody LoginRequestDto registerRequestDTO) {
        log.info("Received request to login");
        return accountService.login(registerRequestDTO);
    }

    @PostMapping("refresh-token")
    public TokensResponseDTO refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        log.info("Received request to refresh token");
        return accountService.refreshToken(refreshTokenDto);
    }
}
