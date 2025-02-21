package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AuthenticationRequest;
import com.popeftimov.automechanic.dto.AuthenticationResponse;
import com.popeftimov.automechanic.dto.RegisterRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<?> register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response);
    String requestPasswordReset(String email);
    void sendVerificationEmail(String email, String link) throws MessagingException;
    ResponseEntity<?> resetUserPassword(String email, String token, String newPassword, String repeatNewPassword);
    void logout(HttpServletResponse response);
}
