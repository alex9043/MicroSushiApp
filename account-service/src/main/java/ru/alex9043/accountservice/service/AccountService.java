package ru.alex9043.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.alex9043.accountservice.config.RabbitMQConfig;
import ru.alex9043.accountservice.dto.*;
import ru.alex9043.accountservice.model.Account;
import ru.alex9043.accountservice.model.Role;
import ru.alex9043.accountservice.repository.AccountRepository;
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
    private final RestTemplate restTemplate;
    @Value("${RABBITMQ_ROUTING_KEY}")
    private String ROUTING_KEY;
    private final PasswordEncoder passwordEncoder;

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Service", "true");
        return headers;
    }

    private TokensResponseDTO getTokens(Account account) {

        TokensResponseDTO tokens = (TokensResponseDTO)
                rabbitTemplate.convertSendAndReceive(
                        RabbitMQConfig.EXCHANGE_NAME,
                        ROUTING_KEY,
                        new UserRequestDTO(account.getPhone(), account.getRoles().stream().map(
                                Enum::toString
                        ).collect(Collectors.toSet()))
                );
        assert tokens != null;

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
        HttpHeaders headers = createHeaders();
        HttpEntity<TokenRequestDto> request = new HttpEntity<>(
                TokenRequestDto.builder()
                        .token(token.substring(7))
                        .build(),
                headers
        );

        ResponseEntity<SubjectResponseDto> response = restTemplate.exchange(
                "http://auth-service/api/v1/auth/subject",
                HttpMethod.POST,
                request,
                SubjectResponseDto.class
        );

        SubjectResponseDto subject = response.getBody();
        assert subject != null;
        return subject.getSubject();
    }

    public TokensResponseDTO refreshToken(RefreshTokenDto refreshTokenDto) {
        Account account = accountRepository.findByRefreshTokenContains(refreshTokenDto.getRefreshToken()).orElseThrow(
                () -> new IllegalArgumentException("Invalid refresh token")
        );

        return getTokens(account);
    }
}
