package com.popeftimov.automechanic.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.email.name}")
    private String emailQueue;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;

    @Value("${rabbitmq.routingKey.email.name}")
    private String emailRoutingKey;

    @Value("${rabbitmq.queue.email.dlx-name}")
    private String emailDLXQueue;

    @Value("${rabbitmq.exchange.email.dlx-name}")
    private String emailDLXExchange;

    @Value("${rabbitmq.routingKey.email.dlx-name}")
    private String emailDLXRoutingKey;

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueue)
                .deadLetterExchange(emailDLXExchange)
                .deadLetterRoutingKey(emailDLXRoutingKey)
                .build();
    }

    @Bean
    public Queue emailDLXQueue() {
        return QueueBuilder.durable(emailDLXQueue)
                .ttl(300000)
                .deadLetterExchange(emailExchange)
                .deadLetterRoutingKey(emailRoutingKey)
                .build();
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(emailExchange);
    }

    @Bean
    public DirectExchange emailDLXExchange() {
        return new DirectExchange(emailDLXExchange);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(emailExchange())
                .with(emailRoutingKey);
    }

    @Bean
    public Binding emailDLXBinding() {
        return BindingBuilder.bind(emailDLXQueue())
                .to(emailDLXExchange())
                .with(emailDLXRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
