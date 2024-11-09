package ru.alex9043.cartservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.alex9043.cartservice.config.RabbitMQConfig;
import ru.alex9043.commondto.SubjectResponseDto;
import ru.alex9043.commondto.TokenRequestDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitService {

    private final RabbitTemplate rabbitTemplate;

    public SubjectResponseDto getSubject(TokenRequestDto tokenRequestDto) {
        try {
            log.info("Requesting subject");
            log.debug("Requesting subject for token: {}", tokenRequestDto.getToken());
            SubjectResponseDto subject = (SubjectResponseDto)
                    rabbitTemplate.convertSendAndReceive(
                            RabbitMQConfig.AUTH_EXCHANGE_NAME,
                            RabbitMQConfig.AUTH_ROUTING_KEY_SUBJECT,
                            tokenRequestDto
                    );
            if (subject == null) {
                throw new IllegalStateException("Auth service is unavailable.");
            }
            log.debug("Subject received successfully for token: {}", tokenRequestDto.getToken());
            return subject;
        } catch (AmqpException e) {
            throw new IllegalStateException("Service temporarily unavailable, please try again later.");
        }
    }
}
