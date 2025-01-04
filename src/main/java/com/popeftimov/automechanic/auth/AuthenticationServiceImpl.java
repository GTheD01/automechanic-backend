package com.popeftimov.automechanic.auth;

import com.popeftimov.automechanic.auth.dto.AuthenticationRequest;
import com.popeftimov.automechanic.auth.dto.AuthenticationResponse;
import com.popeftimov.automechanic.auth.dto.RegisterRequest;
import com.popeftimov.automechanic.auth.exception.ConfirmationExceptions;
import com.popeftimov.automechanic.auth.exception.EmailExceptions;
import com.popeftimov.automechanic.auth.exception.PasswordExceptions;
import com.popeftimov.automechanic.auth.confirmationtoken.ConfirmationToken;
import com.popeftimov.automechanic.auth.confirmationtoken.ConfirmationTokenService;
import com.popeftimov.automechanic.auth.passwordresettoken.PasswordResetTokenService;
import com.popeftimov.automechanic.auth.validator.EmailValidator;
import com.popeftimov.automechanic.auth.validator.PasswordValidator;
import com.popeftimov.automechanic.config.JwtService;
import com.popeftimov.automechanic.global.EmailService;
import com.popeftimov.automechanic.user.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserService userService;
    private final EmailService emailService;

    public String register(RegisterRequest request) {
        boolean isValidEmail = emailValidator
                .test(request.getEmail());

        if (!isValidEmail) {
            throw new EmailExceptions.InvalidEmailException();
        }

        boolean passwordStrongEnough = passwordValidator
                .test(request.getPassword());

        if (!passwordStrongEnough) {
            throw new PasswordExceptions.InvalidPasswordException();
        }

        if (!Objects.equals(request.getPassword(), request.getRepeatPassword())) {
            throw new PasswordExceptions.PasswordDoNotMatchException();
        }

        boolean userExists = userRepository
                .findByEmail(request.getEmail())
                .isPresent();

        if (userExists) {
            throw new EmailExceptions.EmailAlreadyTakenException();
        }

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                Role.USER
        );

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);

        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = "http://localhost:8080/api/v1/auth/register/confirm?token=" + token;
        sendVerificationEmail(user.getEmail(), link);
        return token;
    }

    @Override
    public ResponseEntity<?> resetUserPassword(String email, String token, String newPassword, String repeatNewPassword) {
        boolean isValidToken = passwordResetTokenService.validatePasswordResetToken(email, token);

        if (!isValidToken) {
            throw new ConfirmationExceptions.TokenInvalidExpired();
        }

        if (!passwordValidator.test(newPassword)) {
            throw new PasswordExceptions.InvalidPasswordException();
        }

        if (!newPassword.equals(repeatNewPassword)) {
            throw new PasswordExceptions.PasswordDoNotMatchException();
        }

        userService.resetPassword(email, token, newPassword, repeatNewPassword);

        return ResponseEntity.ok().build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        Cookie jwtCookie = new Cookie("accessToken", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60*60);

        response.addCookie(jwtCookie);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    @Override
    public String requestPasswordReset(String email) {
        if (!emailValidator.test(email)) {
            throw new EmailExceptions.InvalidEmailException();
        }

        return passwordResetTokenService.generatePasswordResetToken(email);
    }

    public void sendVerificationEmail(String email, String link) {
        String subject = "Account verification";
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f7f7f7;\">"
                + "<div style=\"max-width: 600px; margin: 30px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">"
                + "<h2 style=\"color: #333; text-align: center; font-size: 24px; margin-bottom: 20px;\">Welcome to Our App!</h2>"
                + "<p style=\"font-size: 16px; color: #555; line-height: 1.6;\">Hi there,</p>"
                + "<p style=\"font-size: 16px; color: #555; line-height: 1.6;\">Thank you for registering with us. To complete your account activation, please click the verification link below:</p>"
                + "<div style=\"background-color: #f9f9f9; padding: 20px; border-radius: 5px; box-shadow: 0 0 8px rgba(0,0,0,0.1); margin-top: 20px;\">"
                + "<h3 style=\"color: #333; font-size: 18px; margin-bottom: 10px;\">Your Verification Link:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff; word-wrap: break-word; text-align: center;\">" + link + "</p>"
                + "</div>"
                + "<p style=\"font-size: 16px; color: #555; line-height: 1.6; margin-top: 20px;\">If you didn't request this, please ignore this email.</p>"
                + "<p style=\"font-size: 16px; color: #555; line-height: 1.6;\">Best regards,<br/>The Automechanic Team</p>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }

    }
}
