package ru.alex9043.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.alex9043.accountservice.dto.*;
import ru.alex9043.accountservice.dto.error.JwtValidationException;
import ru.alex9043.accountservice.model.Account;
import ru.alex9043.accountservice.model.Role;
import ru.alex9043.accountservice.repository.AccountRepository;
import ru.alex9043.commondto.*;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RabbitService rabbitService;

    private TokensResponseDTO getTokens(Account account) {
        log.debug("Generating tokens for account with phone: {}", account.getPhone());
        TokensResponseDTO tokens = rabbitService.getTokens(new UserRequestDTO(account.getId(), account.getPhone(), account.getRoles().stream().map(
                Enum::toString
        ).collect(Collectors.toSet())));
        account.getRefreshToken().add(tokens.getRefreshToken());
        accountRepository.save(account);
        log.info("Tokens generated and saved for account with phone: {}", account.getPhone());
        return tokens;
    }

    public TokensResponseDTO register(RegisterRequestDto registerRequestDTO) {
        log.info("Registering new user with phone: {}", registerRequestDTO.getPhone());
        if (!Objects.equals(registerRequestDTO.getPassword(), registerRequestDTO.getConfirmPassword())) {
            log.error("Password mismatch for phone: {}", registerRequestDTO.getPhone());
            throw new IllegalArgumentException("Passwords don't match");
        }
        if (accountRepository.findByPhone(registerRequestDTO.getPhone()).isPresent()) {
            log.error("Phone already exists: {}", registerRequestDTO.getPhone());
            throw new IllegalArgumentException("Phone already exist");
        }
        if (accountRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            log.error("Email already exists: {}", registerRequestDTO.getEmail());
            throw new IllegalArgumentException("Email already exist");
        }
        if (accountRepository.findByName(registerRequestDTO.getName()).isPresent()) {
            log.error("Name already exists: {}", registerRequestDTO.getName());
            throw new IllegalArgumentException("Name already exist");
        }
        Account account = modelMapper.map(registerRequestDTO, Account.class);
        account.getRoles().add(Role.ROLE_USER);
        account.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        accountRepository.save(account);
        log.info("Account registered successfully with phone: {}", account.getPhone());
        return getTokens(account);
    }

    public TokensResponseDTO login(LoginRequestDto loginRequestDTO) {
        log.info("Attempting login for phone: {}", loginRequestDTO.getPhone());
        Account account = accountRepository.findByPhone(loginRequestDTO.getPhone()).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), account.getPassword())) {
            log.error("Invalid password for phone: {}", loginRequestDTO.getPhone());
            throw new IllegalArgumentException("Invalid password");
        }
        log.info("Login successful for phone: {}", account.getPhone());
        return getTokens(account);
    }

    public AccountsResponseDto.AccountResponseDto getAccount(String token) {
        String phone = getSubject(token);
        log.info("Fetching account details for phone: {}", phone);
        Account account = accountRepository.findByPhone(phone).orElseThrow(
                () -> new IllegalArgumentException("User not found"));
        log.debug("Account details: {}", account);
        return modelMapper.map(account, AccountsResponseDto.AccountResponseDto.class);
    }

    private String getSubject(String token) {
        log.debug("Extracting subject from token");
        SubjectResponseDto subject = rabbitService.getSubject(new TokenRequestDto(token.substring(7)));
        log.debug("Subject extracted: {}", subject.getSubject());
        return subject.getSubject();
    }

    public TokensResponseDTO refreshToken(RefreshTokenDto refreshTokenDto) {
        log.info("Refreshing token for refreshToken: {}", refreshTokenDto.getRefreshToken());
        ValidationResponseDTO response = rabbitService.validate(new TokenRequestDto(refreshTokenDto.getRefreshToken()));
        if (!response.isValid()) {
            log.error("Refresh token is invalid: {}", refreshTokenDto.getRefreshToken());
            throw new JwtValidationException(response.getErrorMessage(), HttpStatus.FORBIDDEN);
        }
        Account account = accountRepository.findByRefreshTokenContains(refreshTokenDto.getRefreshToken()).orElseThrow(
                () -> new IllegalArgumentException("Invalid refresh token")
        );

        log.info("Token refreshed for account with phone: {}", account.getPhone());
        return getTokens(account);
    }

    public AccountsResponseDto.AccountResponseDto getAccountForAdmin(UUID id) {
        log.info("Fetching account details for admin with account ID: {}", id);
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found"));
        log.debug("Account details for admin: {}", account);
        return modelMapper.map(account, AccountsResponseDto.AccountResponseDto.class);
    }

    public AccountsResponseDto getAccounts() {
        log.info("Fetching all accounts for admin");
        Set<AccountsResponseDto.AccountResponseDto> accounts = accountRepository.findAll().stream().map(
                account -> modelMapper.map(account, AccountsResponseDto.AccountResponseDto.class)
        ).collect(Collectors.toSet());
        log.debug("Fetched accounts: {}", accounts);
        return new AccountsResponseDto(accounts);
    }

    public AccountsResponseDto createAccount(AccountRequestDto accountRequestDto) {
        log.info("Creating new account with phone: {}", accountRequestDto.getPhone());
        return setAccountData(new Account(), accountRequestDto);
    }

    public AccountsResponseDto updateAccount(UUID id, AccountRequestDto accountRequestDto) {
        log.info("Updating account with ID: {}", id);
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        return setAccountData(account, accountRequestDto);
    }

    private AccountsResponseDto setAccountData(Account account, AccountRequestDto accountRequestDto) {
        log.debug("Setting account data for phone: {}", accountRequestDto.getPhone());
        account.setPhone(accountRequestDto.getPhone());
        account.setName(accountRequestDto.getName());
        account.setEmail(accountRequestDto.getEmail());
        account.setDateOfBirth(accountRequestDto.getDateOfBirth());
        account.setPassword(passwordEncoder.encode(accountRequestDto.getPassword()));
        account.setRoles(accountRequestDto.getRoles().stream().peek(
                role -> {
                    if (!EnumSet.allOf(Role.class).contains(role)) {
                        log.error("Invalid role: {}", role);
                        throw new IllegalArgumentException("Invalid role");
                    }
                }).collect(Collectors.toSet()));
        accountRepository.save(account);

        log.info("Account data set and saved for phone: {}", account.getPhone());
        return getAccounts();
    }

    public AccountsResponseDto deleteAccount(UUID id) {
        log.info("Deleting account with ID: {}", id);
        accountRepository.deleteById(id);

        log.info("Account deleted with ID: {}", id);
        return getAccounts();
    }
}
