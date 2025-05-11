package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMqEmailListenerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private Channel channel;

    @Mock
    private Message message;

    @Mock
    private MessageProperties messageProperties;

    @InjectMocks
    private RabbitMqEmailListener rabbitMqEmailListener;

    @Test
    void testProcessEmailMessage_Success() throws Exception {
        // Arrange
        EmailRequest emailRequest = new EmailRequest("test@example.com", "Test Subject", "templateName", null);

        // Act
        rabbitMqEmailListener.processEmailMessage(emailRequest, 123L, channel, message);

        // Assert
        verify(emailService).sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getTemplateName(), emailRequest.getContextValues());
        verify(channel, never()).basicReject(anyLong(), anyBoolean());
    }

    @Test
    void testProcessEmailMessage_Failure_MaxRetriesReached() throws Exception {
        // Arrange
        EmailRequest emailRequest = new EmailRequest("test@example.com", "Test Subject", "templateName", null);
        doThrow(new RuntimeException("Email sending failed")).when(emailService)
                .sendEmail(anyString(), anyString(), anyString(), anyMap());

        // Mock the message and properties
        when(message.getMessageProperties()).thenReturn(messageProperties);
        when(messageProperties.getRetryCount()).thenReturn(2L); // Setting retry count to 2

        // Act
        rabbitMqEmailListener.processEmailMessage(emailRequest, 123L, channel, message);

        // Assert
        verify(channel, never()).basicReject(anyLong(), anyBoolean());  // Should not reject if retry count is 2 or more
    }

    @Test
    void testProcessEmailMessage_Failure_RetryNotMax() throws Exception {
        // Arrange
        EmailRequest emailRequest = new EmailRequest("test@example.com", "Test Subject", "templateName", null);
        doThrow(new RuntimeException("Email sending failed")).when(emailService)
                .sendEmail(anyString(), anyString(), anyString(), anyMap());

        // Mock the message and properties
        when(message.getMessageProperties()).thenReturn(messageProperties);
        when(messageProperties.getRetryCount()).thenReturn(1L); // Setting retry count to 1

        // Act
        rabbitMqEmailListener.processEmailMessage(emailRequest, 123L, channel, message);

        // Assert
        verify(channel).basicReject(123L, false);  // Should reject the message if retry count is less than max
    }
}
