package com.popeftimov.automechanic.auth;

import com.popeftimov.automechanic.auth.confirmationtoken.ConfirmationTokenResponse;
import com.popeftimov.automechanic.auth.confirmationtoken.ConfirmationTokenService;
import com.popeftimov.automechanic.auth.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @GetMapping(path = "/register/confirm-email")
    public ConfirmationTokenResponse confirm(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request, response));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequest emailRequest) {
        String email = emailRequest.getEmail();
        return ResponseEntity.ok(authenticationService.requestPasswordReset(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        String email = passwordResetDTO.getEmail();
        String token = passwordResetDTO.getToken();
        String newPassword = passwordResetDTO.getNewPassword();
        String repeatNewPassword = passwordResetDTO.getRepeatNewPassword();

        return authenticationService.resetUserPassword(email, token, newPassword, repeatNewPassword);
    }
}
