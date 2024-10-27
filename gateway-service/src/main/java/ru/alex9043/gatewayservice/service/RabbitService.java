package ru.alex9043.gatewayservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.alex9043.commondto.TokenRequestDto;
import ru.alex9043.commondto.ValidationResponseDTO;
import ru.alex9043.gatewayservice.config.RabbitMQConfig;
import ru.alex9043.gatewayservice.exception.JwtValidationException;

@Service
public class RabbitService {
    private final RabbitTemplate rabbitTemplate;

    public RabbitService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
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
                throw new JwtValidationException("Error occurred while validating token", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return validationResponseDTO;
        } catch (Exception e) {
            throw new JwtValidationException("Error occurred while validating token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
