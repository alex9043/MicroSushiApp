package ru.alex9043.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.alex9043.accountservice.dto.AccountResponseDto;
import ru.alex9043.accountservice.dto.LoginRequestDto;
import ru.alex9043.accountservice.dto.RefreshTokenDto;
import ru.alex9043.accountservice.dto.RegisterRequestDto;
import ru.alex9043.accountservice.model.Account;
import ru.alex9043.accountservice.model.Role;
import ru.alex9043.accountservice.repository.AccountRepository;
import ru.alex9043.commondto.SubjectResponseDto;
import ru.alex9043.commondto.TokenRequestDto;
import ru.alex9043.commondto.TokensResponseDTO;
import ru.alex9043.commondto.UserRequestDTO;

import java.util.Objects;
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

    public AccountResponseDto getAccount(String token) {
        String phone = getSubject(token);
        log.info("phone - {}", phone);
        Account account = accountRepository.findByPhone(phone).orElseThrow(
                () -> new IllegalArgumentException("User not found"));
        return modelMapper.map(account, AccountResponseDto.class);
    }

    private String getSubject(String token) {
        SubjectResponseDto subject = rabbitService.getSubject(new TokenRequestDto(token.substring(7)));
        return subject.getSubject();
    }

    public TokensResponseDTO refreshToken(RefreshTokenDto refreshTokenDto) {
        // TODO validate refresh tokens and delete expired
        Account account = accountRepository.findByRefreshTokenContains(refreshTokenDto.getRefreshToken()).orElseThrow(
                () -> new IllegalArgumentException("Invalid refresh token")
        );

        return getTokens(account);
    }
}
