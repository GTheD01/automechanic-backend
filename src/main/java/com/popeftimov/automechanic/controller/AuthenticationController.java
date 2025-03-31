package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.exception.authentication.AuthenticationExceptions;
import com.popeftimov.automechanic.security.JwtService;
import com.popeftimov.automechanic.service.AuthenticationService;
import com.popeftimov.automechanic.dto.*;
import com.popeftimov.automechanic.service.ConfirmationTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final ConfirmationTokenService confirmationTokenService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Void> register(
            @RequestBody RegisterRequest request
    ) {
        return authenticationService.register(request);
    }

    @GetMapping(path = "/register/confirm-email")
    public ResponseEntity<ConfirmationTokenResponse> confirm(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        authenticationService.authenticate(request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        authenticationService.logout(response);
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequest emailRequest) {
        String email = emailRequest.getEmail();
        return ResponseEntity.ok(authenticationService.requestPasswordReset(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        String email = passwordResetDTO.getEmail();
        String token = passwordResetDTO.getToken();
        String newPassword = passwordResetDTO.getNewPassword();
        String repeatNewPassword = passwordResetDTO.getRepeatNewPassword();
        authenticationService.resetUserPassword(email, token, newPassword, repeatNewPassword);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/jwt/refresh-token")
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtService.getRefreshTokenFromCookies(request);

        if (refreshToken == null) {
            throw new AuthenticationExceptions.InvalidOrMissingRefreshToken();
        }

        String userEmail = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if(jwtService.isTokenValid(refreshToken, userDetails)) {

            String newAccessToken = jwtService.generateAccessToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            jwtService.addJwtCookiesToResponse(response, newAccessToken, newRefreshToken);
        } else {
            throw new AuthenticationExceptions.InvalidOrMissingRefreshToken();
        };
    }
}
