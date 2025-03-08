package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMqEmailListener {

    private final EmailService emailService;

    @Value("${rabbitmq.exchange.email.dlx-name}")
    private String emailDLXExchange;

    @Value("${rabbitmq.routingKey.email.dlx-name}")
    private String emailDLXRoutingKey;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;

    @Value("${rabbitmq.routingKey.email.name}")
    private String emailRoutingKey;

    @RabbitListener(queues = "${rabbitmq.queue.email.name}")
    public void processEmailMessage(EmailRequest emailRequest,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                    Channel channel,
                                    Message message) throws IOException {

        try {
            emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(),
                    emailRequest.getTemplateName(), emailRequest.getContextValues());
        } catch (Exception e) {
            log.error("Error processing email for {}", emailRequest.getTo());
            long retryCount = message.getMessageProperties().getRetryCount();

            if (retryCount >= 2) {
                log.error("Max retry attempt reached for email: {}", emailRequest.getTo());
                return;
            }

            channel.basicReject(deliveryTag, false);
        }
    }
}
