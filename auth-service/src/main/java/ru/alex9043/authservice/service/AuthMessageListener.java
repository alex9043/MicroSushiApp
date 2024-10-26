package ru.alex9043.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.alex9043.authservice.config.RabbitMQConfig;
import ru.alex9043.commondto.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthMessageListener {

    public final AuthService authService;

    @RabbitListener(queues = RabbitMQConfig.AUTH_QUEUE_TOKENS, containerFactory = "rabbitListenerContainerFactory")
    public TokensResponseDTO generateTokens(UserRequestDTO userRequest) {
        log.info("Received UserRequestDTO from RabbitMQ: {}", userRequest);
        return authService.generateTokens(userRequest);
    }

    @RabbitListener(queues = RabbitMQConfig.AUTH_QUEUE_SUBJECT, containerFactory = "rabbitListenerContainerFactory")
    public SubjectResponseDto getSubject(TokenRequestDto token) {
        log.info("Received TokenRequestDto from RabbitMQ: {}", token.getToken());
        return authService.getSubject(token);
    }

    @RabbitListener(queues = RabbitMQConfig.AUTH_QUEUE_VALIDATE, containerFactory = "rabbitListenerContainerFactory")
    public ValidationResponseDTO validateToken(TokenRequestDto token) {
        return authService.validateToken(token);
    }
}
