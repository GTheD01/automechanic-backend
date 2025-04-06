package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailPublisher emailPublisher;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String templateName, Map<String, Object> contextValues) throws
            MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);

        Context context = new Context();

        contextValues.forEach(context::setVariable);

        String htmlContent = templateEngine.process(templateName, context);

        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(htmlContent, true);

        mailSender.send(message);
    }

    @Override
    public void sendVerificationEmail(String email, String link) throws MessagingException{
        String subject = "Account verification";
        Map<String, Object> values = new HashMap<>();
        values.put("subject", subject);
        values.put("link", link);
        EmailRequest emailRequest = new EmailRequest(email, subject, "AccountVerification", values);
        emailPublisher.publishEmailRequest(emailRequest);
    }

    @Override
    public void sendPasswordResetEmail(String email, String code) {
        String subject = "Password reset";
        Map<String, Object> values = new HashMap<>();
        values.put("subject", subject);
        values.put("code", code);
        EmailRequest emailRequest = new EmailRequest(email, subject, "PasswordReset", values);
        emailPublisher.publishEmailRequest(emailRequest);
    }
}
