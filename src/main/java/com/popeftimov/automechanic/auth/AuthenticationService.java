package com.popeftimov.automechanic.auth;

import com.popeftimov.automechanic.auth.dto.AuthenticationRequest;
import com.popeftimov.automechanic.auth.dto.AuthenticationResponse;
import com.popeftimov.automechanic.auth.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    String register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    String requestPasswordReset(String email);
    void sendVerificationEmail(String email, String link);

    ResponseEntity<String> resetUserPassword(String token, String newPassword, String repeatNewPassword);
}
