package com.popeftimov.automechanic.controller;

import com.popeftimov.automechanic.dto.*;
import com.popeftimov.automechanic.exception.ApiError;
import com.popeftimov.automechanic.exception.authentication.AuthenticationExceptions;
import com.popeftimov.automechanic.security.JwtService;
import com.popeftimov.automechanic.service.AuthenticationService;
import com.popeftimov.automechanic.service.ConfirmationTokenService;
import com.popeftimov.automechanic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final ConfirmationTokenService confirmationTokenService;
    private final JwtService jwtService;
    private final UserService userService;

    @Operation(
            summary = "Sign up a new user",
            description = "This endpoint allows a user to sign up by providing their email, password, and personal details. It validates the email and password, checks if the email is already registered, and sends a verification email with a confirmation token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created and verification email sent",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request, invalid email, weak password, password mismatch or email already taken",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error, failed to send verification email",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(
            @RequestBody SignUpUserRequest request
    ) {
        return authenticationService.signUp(request);
    }

    @Operation(
            summary = "Confirm the user email via confirmation token",
            description = "This endpoint confirms the user's email address by validating the confirmation token. The token must not be expired, and the email should not have already been confirmed. If the token is valid, the user's account is enabled, and they are notified of the successful confirmation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email successfully confirmed",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Token invalid, expired or already confirmed",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @GetMapping(path = "/register/confirm-email")
    public ResponseEntity<Void> confirm(@RequestParam("token") String token) {
        return confirmationTokenService.confirmToken(token);
    }

    @Operation(
            summary = "Authenticate user",
            description = "This endpoint authenticates the user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized, invalid credentials",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        authenticationService.authenticate(request, response);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Logout the user",
            description = "This endpoint logs out the user by clearing the authentication cookies (accessToken and refreshToken)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully logged out, cookies removed",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        authenticationService.logout(response);
    }

    @Operation(
            summary = "Request password reset",
            description = "This endpoint allows a user to request a password reset by providing their email. A password reset token is generated and sent to the provided email address."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset token generated and sent to email",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email format",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error, unable to send email.",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/request-password-reset")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody PasswordResetRequest emailRequest) {
        String email = emailRequest.getEmail();
        authenticationService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Reset user password",
            description = "This endpoint allows a user to reset their password using a valid password reset token. The user must provide a new password and confirm it by repeating the new password. The password is validated for strength, and the reset token is verified before the password is updated."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password successfully reset",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired token, weak password or passwords do not match",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        String email = passwordResetDTO.getEmail();
        String token = passwordResetDTO.getToken();
        String newPassword = passwordResetDTO.getNewPassword();
        String repeatNewPassword = passwordResetDTO.getRepeatNewPassword();
        authenticationService.resetUserPassword(email, token, newPassword, repeatNewPassword);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Refresh access token using refresh token",
            description = "This endpoint allows a user to refresh their access token by using a valid refresh token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token and refresh token successfully refreshed",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired refresh token",
                    content = @Content(mediaType = "application/json")
            ),
    })
    @PostMapping("/jwt/refresh-token")
    public void refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtService.getRefreshTokenFromCookies(request);

        if (refreshToken == null) {
            throw new AuthenticationExceptions.InvalidOrExpiredRefreshToken();
        }

        String userEmail = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(userEmail);

        if(jwtService.isTokenValid(refreshToken, userDetails)) {

            String newAccessToken = jwtService.generateAccessToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            jwtService.addJwtCookiesToResponse(response, newAccessToken, newRefreshToken);
        } else {
            throw new AuthenticationExceptions.InvalidOrExpiredRefreshToken();
        };
    }

    @Operation(
            summary = "Verify access token",
            description = "This endpoint verifies the validity of the provided access token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token is valid",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired access token",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Full authentication is required to access this resource.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
    })
    @PostMapping("/jwt/verify-token")
    public ResponseEntity<Void> verifyToken(HttpServletRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails userDetails = userService.loadUserByUsername(email);

        String accessToken = jwtService.getAccessTokenFromCookies(request);
        boolean tokenIsValid = jwtService.isTokenValid(accessToken, userDetails);
        if (!tokenIsValid) {
            throw new AuthenticationExceptions.InvalidOrExpiredAccessToken();
        }

        return ResponseEntity.ok().build();
    }
}
