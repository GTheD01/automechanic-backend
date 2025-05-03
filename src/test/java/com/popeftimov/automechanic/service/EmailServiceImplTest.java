package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailPublisher emailPublisher;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void testSendEmail_shouldSendMimeMessage() throws MessagingException {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String templateName = "emailTemplate";
        Map<String, Object> contextValues = new HashMap<>();
        contextValues.put("name", "John");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(templateName), any(Context.class)))
                .thenReturn("<html>Test</html>");

        // Act
        emailService.sendEmail(to, subject, templateName, contextValues);

        // Assert
        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq(templateName), any(Context.class));
    }

    @Test
    void testSendVerificationEmail_shouldPublishEmailRequest() throws MessagingException {
        // Arrange
        String email = "user@example.com";
        String link = "http://verification-link";

        // Act
        emailService.sendVerificationEmail(email, link);

        // Assert
        ArgumentCaptor<EmailRequest> captor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(emailPublisher).publishEmailRequest(captor.capture());

        EmailRequest request = captor.getValue();
        assertEquals(email, request.getTo());
        assertEquals("Account verification", request.getSubject());
        assertEquals("AccountVerification", request.getTemplateName());
        assertEquals(link, request.getContextValues().get("link"));
    }

    @Test
    void testSendPasswordResetEmail_shouldPublishEmailRequest() {
        // Arrange
        String email = "user@example.com";
        String code = "123456";

        // Act
        emailService.sendPasswordResetEmail(email, code);

        // Assert
        ArgumentCaptor<EmailRequest> captor = ArgumentCaptor.forClass(EmailRequest.class);
        verify(emailPublisher).publishEmailRequest(captor.capture());

        EmailRequest request = captor.getValue();
        assertEquals(email, request.getTo());
        assertEquals("Password reset", request.getSubject());
        assertEquals("PasswordReset", request.getTemplateName());
        assertEquals(code, request.getContextValues().get("code"));
    }
  
}