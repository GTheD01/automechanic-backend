package com.popeftimov.automechanic.auth;

public interface AuthenticationService {
    String register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
