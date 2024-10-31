package ru.alex9043.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.alex9043.accountservice.config.RabbitMQConfig;
import ru.alex9043.commondto.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitService {

    private final RabbitTemplate rabbitTemplate;

    public TokensResponseDTO getTokens(UserRequestDTO userRequestDTO) {
        try {
            log.info("Requesting tokens for user: {}", userRequestDTO.getUsername());
            TokensResponseDTO tokens = (TokensResponseDTO)
                    rabbitTemplate.convertSendAndReceive(
                            RabbitMQConfig.AUTH_EXCHANGE_NAME,
                            RabbitMQConfig.AUTH_ROUTING_KEY_TOKENS,
                            userRequestDTO
                    );
            if (tokens == null) {
                log.error("Failed to get tokens from Auth service");
                throw new IllegalStateException("Auth service is unavailable.");
            }
            log.debug("Tokens received successfully for user: {}", userRequestDTO.getUsername());
            return tokens;
        } catch (AmqpException e) {
            log.error("Failed to get tokens from Auth service: {}", e.getMessage());
            throw new IllegalStateException("Service temporarily unavailable, please try again later.");
        }
    }

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

    public ValidationResponseDTO validate(TokenRequestDto tokenRequestDto) {
        try {
            ValidationResponseDTO validationResponseDTO = (ValidationResponseDTO)
                    rabbitTemplate.convertSendAndReceive(
                            RabbitMQConfig.AUTH_EXCHANGE_NAME,
                            RabbitMQConfig.AUTH_ROUTING_KEY_VALIDATE,
                            tokenRequestDto
                    );
            if (validationResponseDTO == null) {
                log.error("Failed to validate from Auth service");
                throw new IllegalStateException("Auth service is unavailable.");
            }
            log.debug("Validation result received successfully for token: {}", tokenRequestDto.getToken());
            return validationResponseDTO;
        } catch (Exception e) {
            log.error("Failed to validate from Auth service");
            throw new IllegalStateException("Service temporarily unavailable, please try again later.");
        }
    }
}
