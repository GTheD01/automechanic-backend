package com.popeftimov.automechanic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMqPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMqPublisher rabbitMqPublisher;

    @Test
    void testPublishMessage() {
        // Arrange
        String exchange = "test.exchange";
        String routingKey = "test.routingKey";
        Object message = "test message";

        // Act
        rabbitMqPublisher.publishMessage(exchange, routingKey, message);

        // Assert
        verify(rabbitTemplate).convertAndSend(exchange, routingKey, message);
    }
}
