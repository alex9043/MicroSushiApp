package ru.alex9043.gatewayservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.alex9043.commondto.TokenRequestDto;
import ru.alex9043.commondto.ValidationResponseDTO;
import ru.alex9043.gatewayservice.config.RabbitMQConfig;
import ru.alex9043.gatewayservice.exception.JwtValidationException;

@Service
@Slf4j
public class RabbitService {
    private final RabbitTemplate rabbitTemplate;

    public RabbitService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public ValidationResponseDTO validate(TokenRequestDto tokenRequestDto) {
        try {
            log.info("Try received validation request in RabbitMQ");
            ValidationResponseDTO validationResponseDTO = (ValidationResponseDTO)
                    rabbitTemplate.convertSendAndReceive(
                            RabbitMQConfig.AUTH_EXCHANGE_NAME,
                            RabbitMQConfig.AUTH_ROUTING_KEY_VALIDATE,
                            tokenRequestDto
                    );
            if (validationResponseDTO == null) {
                log.error("Error occurred while validating token");
                throw new JwtValidationException("Error occurred while validating token", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("Success");
            return validationResponseDTO;
        } catch (Exception e) {
            log.error("Error occurred while validating token: {}", e.getMessage());
            throw new JwtValidationException("Error occurred while validating token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
