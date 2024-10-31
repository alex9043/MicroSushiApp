package ru.alex9043.accountservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Account Management", description = "Управление учетными записями пользователей")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Получить информацию о текущем пользователе",
            description = "Возвращает информацию о текущем пользователе на основе переданного токена.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно получена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountsResponseDto.AccountResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Отсутствует авторизация или недействительный токен", content = @Content)
    })
    @GetMapping("/self")
    public AccountsResponseDto.AccountResponseDto getAccount(
            @Parameter(description = "JWT токен пользователя", required = true)
            @RequestHeader("Authorization") String token) {
        log.info("Received request for account details with token.");
        return accountService.getAccount(token);
    }

    @Operation(summary = "Получить информацию об учетной записи по ID",
            description = "Позволяет администратору получить информацию об учетной записи по ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация об учетной записи успешно получена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountsResponseDto.AccountResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Учетная запись не найдена", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("{accountId}")
    public AccountsResponseDto.AccountResponseDto getAccountForAdmin(
            @Parameter(description = "UUID учетной записи", required = true)
            @PathVariable("accountId") UUID id) {
        log.info("Received admin request for account with ID: {}", id);
        return accountService.getAccountForAdmin(id);
    }

    @Operation(summary = "Получить список всех учетных записей",
            description = "Позволяет администратору получить список всех учетных записей.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список учетных записей успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public AccountsResponseDto getAccounts() {
        log.info("Received request to get all accounts.");
        return accountService.getAccounts();
    }

    @Operation(summary = "Создать новую учетную запись",
            description = "Позволяет администратору создать новую учетную запись.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Учетная запись успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "400", description = "Некорректные данные учетной записи", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountsResponseDto createAccount(
            @Parameter(description = "Данные новой учетной записи", required = true)
            @Valid @RequestBody AccountRequestDto accountRequestDto) {
        log.info("Received request to create a new account.");
        return accountService.createAccount(accountRequestDto);
    }

    @Operation(summary = "Обновить учетную запись",
            description = "Позволяет администратору обновить существующую учетную запись по ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Учетная запись успешно обновлена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Учетная запись не найдена", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{accountId}")
    public AccountsResponseDto updateAccount(
            @Parameter(description = "UUID учетной записи", required = true)
            @PathVariable("accountId") UUID id,
            @Parameter(description = "Обновленные данные учетной записи", required = true)
            @Valid @RequestBody AccountRequestDto accountRequestDto) {
        log.info("Received request to update account with id - {}", id);
        return accountService.updateAccount(id, accountRequestDto);
    }

    @Operation(summary = "Удалить учетную запись",
            description = "Позволяет администратору удалить учетную запись по ID.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Учетная запись успешно удалена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountsResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
            @ApiResponse(responseCode = "404", description = "Учетная запись не найдена", content = @Content)
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("{accountId}")
    public AccountsResponseDto deleteAccount(
            @Parameter(description = "UUID учетной записи", required = true)
            @PathVariable("accountId") UUID id) {
        log.info("Received request to delete account with id - {}", id);
        return accountService.deleteAccount(id);
    }

    @Operation(summary = "Регистрация нового пользователя",
            description = "Регистрирует нового пользователя и возвращает токены для доступа.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokensResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные регистрации", content = @Content)
    })
    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokensResponseDTO register(
            @Parameter(description = "Данные для регистрации", required = true)
            @Valid @RequestBody RegisterRequestDto registerRequestDTO) {
        log.info("Received request to register");
        return accountService.register(registerRequestDTO);
    }

    @Operation(summary = "Вход пользователя",
            description = "Авторизует пользователя и возвращает токены для доступа.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно авторизован",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokensResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные", content = @Content)
    })
    @PostMapping("login")
    public TokensResponseDTO login(
            @Parameter(description = "Данные для входа", required = true)
            @Valid @RequestBody LoginRequestDto registerRequestDTO) {
        log.info("Received request to login");
        return accountService.login(registerRequestDTO);
    }

    @Operation(summary = "Обновление токена",
            description = "Обновляет токен доступа на основе действующего refresh-токена.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен успешно обновлен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokensResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Недействительный refresh-токен", content = @Content)
    })
    @PostMapping("refresh-token")
    public TokensResponseDTO refreshToken(
            @Parameter(description = "Refresh-токен для обновления", required = true)
            @RequestBody RefreshTokenDto refreshTokenDto) {
        log.info("Received request to refresh token");
        return accountService.refreshToken(refreshTokenDto);
    }
}