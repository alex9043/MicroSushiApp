package ru.alex9043.accountservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.alex9043.accountservice.config.RabbitMQConfig;
import ru.alex9043.commondto.*;

@Service
@RequiredArgsConstructor
public class RabbitService {

    private final RabbitTemplate rabbitTemplate;

    public TokensResponseDTO getTokens(UserRequestDTO userRequestDTO) {
        try {
            TokensResponseDTO tokens = (TokensResponseDTO)
                    rabbitTemplate.convertSendAndReceive(
                            RabbitMQConfig.AUTH_EXCHANGE_NAME,
                            RabbitMQConfig.AUTH_ROUTING_KEY_TOKENS,
                            userRequestDTO
                    );
            if (tokens == null) {
                throw new IllegalStateException("Auth service is unavailable.");
            }
            return tokens;
        } catch (AmqpException e) {
            throw new IllegalStateException("Service temporarily unavailable, please try again later.");
        }
    }

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

    public ValidationResponseDTO validate(TokenRequestDto tokenRequestDto) {
        try {
            ValidationResponseDTO validationResponseDTO = (ValidationResponseDTO)
                    rabbitTemplate.convertSendAndReceive(
                            RabbitMQConfig.AUTH_EXCHANGE_NAME,
                            RabbitMQConfig.AUTH_ROUTING_KEY_VALIDATE,
                            tokenRequestDto
                    );
            if (validationResponseDTO == null) {
                throw new IllegalStateException("Auth service is unavailable.");
            }

            return validationResponseDTO;
        } catch (Exception e) {
            throw new IllegalStateException("Service temporarily unavailable, please try again later.");
        }
    }
}
