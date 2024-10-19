package ru.alex9043.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.alex9043.authservice.config.RabbitMQConfig;
import ru.alex9043.commondto.TokensResponseDTO;
import ru.alex9043.commondto.UserRequestDTO;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthMessageListener {

    public final AuthService authService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public TokensResponseDTO receiveMessage(UserRequestDTO userRequest) {
        log.info("Received UserRequestDTO from RabbitMQ: {}", userRequest);
        return authService.generateTokens(userRequest);
    }
}
