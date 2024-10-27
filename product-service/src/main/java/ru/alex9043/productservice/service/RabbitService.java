package ru.alex9043.productservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.alex9043.commondto.SubjectResponseDto;
import ru.alex9043.commondto.TokenRequestDto;
import ru.alex9043.productservice.config.RabbitMQConfig;

@Service
@RequiredArgsConstructor
public class RabbitService {
    private final RabbitTemplate rabbitTemplate;

    public SubjectResponseDto getSubject(TokenRequestDto tokenRequestDto) {
        try {
            SubjectResponseDto subject = (SubjectResponseDto)
                    rabbitTemplate.convertSendAndReceive(
                            RabbitMQConfig.AUTH_EXCHANGE_NAME,
                            RabbitMQConfig.AUTH_ROUTING_KEY_SUBJECT,
                            tokenRequestDto
                    );
            if (subject == null) {
                throw new IllegalStateException("Auth service is unavailable.");
            }
            return subject;
        } catch (AmqpException e) {
            throw new IllegalStateException("Service temporarily unavailable, please try again later.");
        }
    }
}
