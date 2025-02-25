package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;

    @Value("${rabbitmq.routingKey.email.name}")
    private String emailRoutingKey;

    public void sendEmailRequest(String to, String subject, String templateName, Map<String, Object> contextValues) {
        EmailRequest emailRequest = new EmailRequest(to, subject, templateName, contextValues);

        rabbitTemplate.convertAndSend(emailExchange, emailRoutingKey, emailRequest);
    }
}
