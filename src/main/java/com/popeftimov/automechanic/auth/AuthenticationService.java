package com.popeftimov.automechanic.auth;

import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    String register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    String requestPasswordReset(String email);

    ResponseEntity<String> resetUserPassword(String token, String newPassword);
}
