package com.popeftimov.automechanic.service;

import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailService {

    void sendEmail(String to, String subject, String templateName, Map<String, Object> contextValues) throws
            MessagingException;
    void sendVerificationEmail(String email, String link) throws MessagingException;
    void sendPasswordResetEmail(String email, String link);
}
