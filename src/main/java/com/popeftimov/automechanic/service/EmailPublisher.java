package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
public class EmailPublisher {

    private final RabbitMqPublisher rabbitMqPublisher;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;

    @Value("${rabbitmq.routingKey.email.name}")
    private String emailRoutingKey;

    public void publishEmailRequest(EmailRequest emailRequest) {
        rabbitMqPublisher.publishMessage(emailExchange, emailRoutingKey, emailRequest);
    }
}
