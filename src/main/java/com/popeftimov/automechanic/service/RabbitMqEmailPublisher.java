package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMqEmailPublisher implements EmailPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;

    @Value("${rabbitmq.routingKey.email.name}")
    private String emailRoutingKey;

    public void publishEmailRequest(EmailRequest emailRequest) {
        rabbitTemplate.convertAndSend(emailExchange, emailRoutingKey, emailRequest);
    }
}
