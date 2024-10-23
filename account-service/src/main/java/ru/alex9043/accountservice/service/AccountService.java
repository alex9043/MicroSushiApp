package ru.alex9043.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.alex9043.accountservice.config.RabbitMQConfig;
import ru.alex9043.accountservice.dto.AccountResponseDto;
import ru.alex9043.accountservice.dto.LoginRequestDTO;
import ru.alex9043.accountservice.dto.RefreshTokenDto;
import ru.alex9043.accountservice.dto.RegisterRequestDTO;
import ru.alex9043.accountservice.model.Account;
import ru.alex9043.accountservice.model.Role;
import ru.alex9043.accountservice.repository.AccountRepository;
import ru.alex9043.commondto.SubjectResponseDto;
import ru.alex9043.commondto.TokenRequestDto;
import ru.alex9043.commondto.TokensResponseDTO;
import ru.alex9043.commondto.UserRequestDTO;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final RabbitTemplate rabbitTemplate;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    private TokensResponseDTO getTokens(Account account) {
        TokensResponseDTO tokens = (TokensResponseDTO)
                rabbitTemplate.convertSendAndReceive(
                        RabbitMQConfig.AUTH_EXCHANGE_NAME,
                        RabbitMQConfig.AUTH_ROUTING_KEY_TOKENS,
                        new UserRequestDTO(account.getPhone(), account.getRoles().stream().map(
                                Enum::toString
                        ).collect(Collectors.toSet()))
                );

        account.setRefreshToken(new LinkedHashSet<>());

        account.getRefreshToken().add(tokens.getRefreshToken());
        accountRepository.save(account);

        return tokens;
    }

    public TokensResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        Account account = modelMapper.map(registerRequestDTO, Account.class);
        account.getRoles().add(Role.ROLE_USER);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
        return getTokens(account);
    }

    public TokensResponseDTO login(LoginRequestDTO registerRequestDTO) {
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
        SubjectResponseDto response = (SubjectResponseDto)
                rabbitTemplate.convertSendAndReceive(
                        RabbitMQConfig.AUTH_EXCHANGE_NAME,
                        RabbitMQConfig.AUTH_ROUTING_KEY_SUBJECT,
                        new TokenRequestDto(token.substring(7))
                );

        assert response != null;
        System.out.println("Response - " + response.getSubject());
        return response.getSubject();
    }

    public TokensResponseDTO refreshToken(RefreshTokenDto refreshTokenDto) {
        Account account = accountRepository.findByRefreshTokenContains(refreshTokenDto.getRefreshToken()).orElseThrow(
                () -> new IllegalArgumentException("Invalid refresh token")
        );

        return getTokens(account);
    }
}
