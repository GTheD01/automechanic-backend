package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailPublisherTest {

    @Mock
    private RabbitMqPublisher rabbitMqPublisher;

    private EmailPublisher emailPublisher;

    @BeforeEach
    void setUp() {
        emailPublisher = new EmailPublisher(rabbitMqPublisher);
        emailPublisher.setEmailExchange("test-exchange");
        emailPublisher.setEmailRoutingKey("test-routingKey");
    }

    @Test
    void shouldPublishEmailRequest() {
        // Arrange
        EmailRequest request = new EmailRequest(
                "user@example.com",
                "Subject",
                "TemplateName",
                Map.of("key", "value")
        );

        // Act
        emailPublisher.publishEmailRequest(request);

        // Assert
        verify(rabbitMqPublisher).publishMessage(
                eq("test-exchange"),
                eq("test-routingKey"),
                eq(request)
        );
    }
}
