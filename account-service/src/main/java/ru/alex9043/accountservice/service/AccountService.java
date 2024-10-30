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
        TokensResponseDTO tokens = rabbitService.getTokens(new UserRequestDTO(account.getPhone(), account.getRoles().stream().map(
                Enum::toString
        ).collect(Collectors.toSet())));
        account.getRefreshToken().add(tokens.getRefreshToken());
        accountRepository.save(account);

        return tokens;
    }

    public TokensResponseDTO register(RegisterRequestDto registerRequestDTO) {
        if (!Objects.equals(registerRequestDTO.getPassword(), registerRequestDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords don't match");
        }
        if (accountRepository.findByPhone(registerRequestDTO.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Phone already exist");
        }
        if (accountRepository.findByEmail(registerRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exist");
        }
        if (accountRepository.findByName(registerRequestDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Name already exist");
        }
        Account account = modelMapper.map(registerRequestDTO, Account.class);
        account.getRoles().add(Role.ROLE_USER);
        account.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        accountRepository.save(account);
        return getTokens(account);
    }

    public TokensResponseDTO login(LoginRequestDto registerRequestDTO) {
        Account account = accountRepository.findByPhone(registerRequestDTO.getPhone()).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        if (!passwordEncoder.matches(registerRequestDTO.getPassword(), account.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        return getTokens(account);
    }

    public AccountsResponseDto.AccountResponseDto getAccount(String token) {
        String phone = getSubject(token);
        log.info("phone - {}", phone);
        Account account = accountRepository.findByPhone(phone).orElseThrow(
                () -> new IllegalArgumentException("User not found"));
        return modelMapper.map(account, AccountsResponseDto.AccountResponseDto.class);
    }

    private String getSubject(String token) {
        SubjectResponseDto subject = rabbitService.getSubject(new TokenRequestDto(token.substring(7)));
        return subject.getSubject();
    }

    public TokensResponseDTO refreshToken(RefreshTokenDto refreshTokenDto) {
        ValidationResponseDTO response = rabbitService.validate(new TokenRequestDto(refreshTokenDto.getRefreshToken()));
        if (!response.isValid()) {
            throw new JwtValidationException(response.getErrorMessage(), HttpStatus.FORBIDDEN);
        }
        Account account = accountRepository.findByRefreshTokenContains(refreshTokenDto.getRefreshToken()).orElseThrow(
                () -> new IllegalArgumentException("Invalid refresh token")
        );

        return getTokens(account);
    }

    public AccountsResponseDto.AccountResponseDto getAccountForAdmin(UUID id) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found"));
        return modelMapper.map(account, AccountsResponseDto.AccountResponseDto.class);
    }

    public AccountsResponseDto getAccounts() {
        Set<AccountsResponseDto.AccountResponseDto> accounts = accountRepository.findAll().stream().map(
                account -> modelMapper.map(account, AccountsResponseDto.AccountResponseDto.class)
        ).collect(Collectors.toSet());
        return new AccountsResponseDto(accounts);
    }

    public AccountsResponseDto createAccount(AccountRequestDto accountRequestDto) {
        return setAccountData(new Account(), accountRequestDto);
    }

    public AccountsResponseDto updateAccount(UUID id, AccountRequestDto accountRequestDto) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        return setAccountData(account, accountRequestDto);
    }

    private AccountsResponseDto setAccountData(Account account, AccountRequestDto accountRequestDto) {
        account.setPhone(accountRequestDto.getPhone());
        account.setName(accountRequestDto.getName());
        account.setEmail(accountRequestDto.getEmail());
        account.setDateOfBirth(accountRequestDto.getDateOfBirth());
        account.setPassword(passwordEncoder.encode(accountRequestDto.getPassword()));
        account.setRoles(accountRequestDto.getRoles().stream().peek(
                role -> {
                    if (!EnumSet.allOf(Role.class).contains(role)) {
                        throw new IllegalArgumentException("Invalid role");
                    }
                }).collect(Collectors.toSet()));
        accountRepository.save(account);

        return getAccounts();
    }

    public AccountsResponseDto deleteAccount(UUID id) {
        accountRepository.deleteById(id);

        return getAccounts();
    }
}
