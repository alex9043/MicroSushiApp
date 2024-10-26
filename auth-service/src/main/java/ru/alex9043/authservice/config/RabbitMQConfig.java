package ru.alex9043.authservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitMQConfig {

    public static final String AUTH_EXCHANGE_NAME = "auth.exchange";
    public static final String AUTH_QUEUE_TOKENS = "auth.queue.tokens";
    public static final String AUTH_QUEUE_SUBJECT = "auth.queue.subject";
    public static final String AUTH_QUEUE_VALIDATE = "auth.queue.validate";
    public static final String AUTH_ROUTING_KEY_TOKENS = "auth.routingKey.tokens";
    public static final String AUTH_ROUTING_KEY_SUBJECT = "auth.routingKey.subject";
    public static final String AUTH_ROUTING_KEY_VALIDATE = "auth.routingKey.validate";


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
    public Queue validateQueue() {
        return new Queue(AUTH_QUEUE_VALIDATE, true);
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
    public Binding validateBinding(Queue validateQueue, DirectExchange exchange) {
        return BindingBuilder.bind(validateQueue).to(exchange).with(AUTH_ROUTING_KEY_VALIDATE);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(AUTH_EXCHANGE_NAME);

        template.setMessageConverter(jackson2JsonMessageConverter());

        template.setReplyTimeout(10000);

        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());

        RetryOperationsInterceptor retryOperationsInterceptor = RetryInterceptorBuilder
                .stateless()
                .maxAttempts(3)
                .backOffOptions(1000, 2.0, 10000)
                .build();
        factory.setAdviceChain(retryOperationsInterceptor);

        return factory;
    }
}