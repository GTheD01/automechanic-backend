package com.popeftimov.automechanic.service;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailService {

    void sendEmail(String to, String subject, String templateName, Map<String, Object> contextValues) throws
            MessagingException;
}
