package ru.alex9043.accountservice.config;

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
    public static final String AUTH_QUEUE_TOKENS = "auth.queue.tokens";
    public static final String AUTH_QUEUE_SUBJECT = "auth.queue.subject";
    public static final String AUTH_ROUTING_KEY_TOKENS = "auth.routingKey.tokens";
    public static final String AUTH_ROUTING_KEY_SUBJECT = "auth.routingKey.subject";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(AUTH_EXCHANGE_NAME);
    }

    @Bean
    public Queue tokensQueue() {
        return new Queue(AUTH_QUEUE_TOKENS, true);
    }

    @Bean
    public Queue subjectQueue() {
        return new Queue(AUTH_QUEUE_SUBJECT, true);
    }

    @Bean
    public Binding tokensBinding(Queue tokensQueue, DirectExchange exchange) {
        return BindingBuilder.bind(tokensQueue).to(exchange).with(AUTH_ROUTING_KEY_TOKENS);
    }

    @Bean
    public Binding subjectBinding(Queue subjectQueue, DirectExchange exchange) {
        return BindingBuilder.bind(subjectQueue).to(exchange).with(AUTH_ROUTING_KEY_SUBJECT);
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
