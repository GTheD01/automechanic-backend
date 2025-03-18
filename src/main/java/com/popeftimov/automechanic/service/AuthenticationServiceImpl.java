package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AuthenticationRequest;
import com.popeftimov.automechanic.dto.AuthenticationResponse;
import com.popeftimov.automechanic.dto.EmailRequest;
import com.popeftimov.automechanic.dto.RegisterRequest;
import com.popeftimov.automechanic.exception.ConfirmationExceptions;
import com.popeftimov.automechanic.exception.EmailExceptions;
import com.popeftimov.automechanic.exception.PasswordExceptions;
import com.popeftimov.automechanic.exception.RegisterExceptions;
import com.popeftimov.automechanic.model.ConfirmationToken;
import com.popeftimov.automechanic.model.UserRole;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.validator.EmailValidator;
import com.popeftimov.automechanic.validator.PasswordValidator;
import com.popeftimov.automechanic.repository.UserRepository;
import com.popeftimov.automechanic.security.JwtService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
    private final EmailPublisher emailPublisher;

    public ResponseEntity<Void> register(RegisterRequest request) {
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
                UserRole.USER
        );

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);

        try {
            userRepository.save(user);

            String token = UUID.randomUUID().toString();

            ConfirmationToken confirmationToken = new ConfirmationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    user
            );

            confirmationTokenService.saveConfirmationToken(confirmationToken);

            String link = "http://localhost:5173/verify-email?token=" + token;
            sendVerificationEmail(user.getEmail(), link);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (MessagingException e) {
            throw new RegisterExceptions.FailedToSendEmail();
        }
    }

    @Override
    public String requestPasswordReset(String email) {
        if (!emailValidator.test(email)) {
            throw new EmailExceptions.InvalidEmailException();
        }

        return passwordResetTokenService.generatePasswordResetToken(email);
    }

    @Override
    public void resetUserPassword(String email, String token, String newPassword, String repeatNewPassword) {
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
    }

    @Override
    public void logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("accessToken", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userService.loadUser(request.getEmail());
        var jwtToken = jwtService.generateToken(user);
        Cookie jwtCookie = new Cookie("accessToken", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60*60*24);

        response.addCookie(jwtCookie);

        return new AuthenticationResponse(jwtToken);
    }

    public void sendVerificationEmail(String email, String link) throws MessagingException{
        String subject = "Account verification";
        Map<String, Object> values = new HashMap<>();
        values.put("subject", subject);
        values.put("link", link);
        EmailRequest emailRequest = new EmailRequest(email, subject, "AccountVerification", values);
        emailPublisher.publishEmailRequest(emailRequest);
    }
}
