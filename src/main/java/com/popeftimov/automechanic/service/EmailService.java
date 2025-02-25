package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
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


    @RabbitListener(queues = "${rabbitmq.queue.email.name}")
    public void processEmailMessage(EmailRequest emailRequest) throws MessagingException {
        try {
            this.sendEmail(emailRequest.getTo(), emailRequest.getSubject(),
                    emailRequest.getTemplateName(), emailRequest.getContextValues());
        } catch (MessagingException e) {
            log.error("Error processing email for {}", emailRequest.getTo());
        }
    }

}
