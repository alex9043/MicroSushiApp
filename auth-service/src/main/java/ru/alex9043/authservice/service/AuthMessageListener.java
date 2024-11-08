package ru.alex9043.authservice.service;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.alex9043.authservice.config.RabbitMQConfig;
import ru.alex9043.commondto.*;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthMessageListener {

    public final AuthService authService;

    @RabbitListener(queues = RabbitMQConfig.AUTH_QUEUE_TOKENS, containerFactory = "rabbitListenerContainerFactory")
    public TokensResponseDTO generateTokens(UserRequestDTO userRequest, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("Received UserRequestDTO from RabbitMQ: {}", userRequest);
            channel.basicAck(tag, false);
            return authService.generateTokens(userRequest);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            channel.basicNack(tag, false, false);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.AUTH_QUEUE_SUBJECT, containerFactory = "rabbitListenerContainerFactory")
    public SubjectResponseDto getSubject(TokenRequestDto token, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("Received TokenRequestDto from RabbitMQ: {}", token.getToken());
            channel.basicAck(tag, false);
            return authService.getSubject(token);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            channel.basicNack(tag, false, false);
            throw e;
        }

    }

    @RabbitListener(queues = RabbitMQConfig.AUTH_QUEUE_VALIDATE, containerFactory = "rabbitListenerContainerFactory")
    public ValidationResponseDTO validateToken(TokenRequestDto token, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("Received TokenRequestDto from RabbitMQ: {}", token.getToken());
            channel.basicAck(tag, false);
            return authService.validateToken(token);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            channel.basicNack(tag, false, false);
            throw e;
        }
    }
}
