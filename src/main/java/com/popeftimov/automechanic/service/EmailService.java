package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.EmailRequest;
import com.rabbitmq.client.Channel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${rabbitmq.exchange.email.dlx-name}")
    private String emailDLXExchange;

    @Value("${rabbitmq.routingKey.email.dlx-name}")
    private String emailDLXRoutingKey;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;

    @Value("${rabbitmq.routingKey.email.name}")
    private String emailRoutingKey;

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
    public void processEmailMessage(EmailRequest emailRequest,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                    Channel channel,
                                    Message message) throws IOException {

        try {
            this.sendEmail(emailRequest.getTo(), emailRequest.getSubject(),
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
