package ru.alex9043.gatewayservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String AUTH_EXCHANGE_NAME = "auth.exchange";
    public static final String AUTH_QUEUE_VALIDATE = "auth.queue.validate";
    public static final String AUTH_ROUTING_KEY_VALIDATE = "auth.routingKey.validate";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(AUTH_EXCHANGE_NAME);
    }

    @Bean
    public Queue validateQueue() {
        return new Queue(AUTH_QUEUE_VALIDATE, true);
    }

    @Bean
    public Binding validateBinding(Queue validateQueue, DirectExchange exchange) {
        return BindingBuilder.bind(validateQueue).to(exchange).with(AUTH_ROUTING_KEY_VALIDATE);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(AUTH_EXCHANGE_NAME);

        template.setMessageConverter(jackson2JsonMessageConverter());

        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
