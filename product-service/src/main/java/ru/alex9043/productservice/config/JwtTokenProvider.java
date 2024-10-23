package ru.alex9043.productservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import ru.alex9043.commondto.SubjectResponseDto;
import ru.alex9043.productservice.dto.TokenRequestDto;

@Configuration
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final RabbitTemplate rabbitTemplate;

    public SubjectResponseDto getSubjectAndRoles(String token) {

        return (SubjectResponseDto)
                rabbitTemplate.convertSendAndReceive(
                        RabbitMQConfig.AUTH_EXCHANGE_NAME,
                        RabbitMQConfig.AUTH_ROUTING_KEY_SUBJECT,
                        new TokenRequestDto(token)
                );
    }
}
