package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AuthenticationRequest;
import com.popeftimov.automechanic.dto.SignUpUserRequest;
import com.popeftimov.automechanic.exception.confirmationtoken.ConfirmationTokenExceptions;
import com.popeftimov.automechanic.exception.signup.SignUpExceptions;
import com.popeftimov.automechanic.exception.user.UserExceptions;
import com.popeftimov.automechanic.model.ConfirmationToken;
import com.popeftimov.automechanic.model.PasswordResetToken;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.model.UserRole;
import com.popeftimov.automechanic.repository.UserRepository;
import com.popeftimov.automechanic.security.JwtService;
import com.popeftimov.automechanic.validator.EmailValidator;
import com.popeftimov.automechanic.validator.PasswordValidator;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final EmailService emailService;

    @Transactional
    @Override
    public ResponseEntity<Void> signUp(SignUpUserRequest request) {
        boolean isValidEmail = emailValidator
                .test(request.getEmail());

        if (!isValidEmail) {
            throw new UserExceptions.InvalidEmailException();
        }

        boolean passwordStrongEnough = passwordValidator
                .test(request.getPassword());

        if (!passwordStrongEnough) {
            throw new UserExceptions.InvalidPasswordException();
        }

        if (!Objects.equals(request.getPassword(), request.getRepeatPassword())) {
            throw new UserExceptions.PasswordDoNotMatchException();
        }

        boolean userExists = userRepository
                .findByEmail(request.getEmail())
                .isPresent();

        if (userExists) {
            throw new UserExceptions.EmailAlreadyTakenException();
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
            emailService.sendVerificationEmail(user.getEmail(), link);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (MessagingException e) {
            throw new SignUpExceptions.FailedToSendVerificationEmail();
        }
    }

    @Override
    public void requestPasswordReset(String email) {
        if (!emailValidator.test(email)) {
            throw new UserExceptions.InvalidEmailException();
        }
        passwordResetTokenService.generatePasswordResetToken(email);
    }

    @Override
    public void resetUserPassword(String email, String token, String newPassword, String repeatNewPassword) {
        boolean isValidToken = passwordResetTokenService.validatePasswordResetToken(email, token);

        if (!isValidToken) {
            throw new ConfirmationTokenExceptions.TokenInvalidExpired();
        }

        if (!passwordValidator.test(newPassword)) {
            throw new UserExceptions.InvalidPasswordException();
        }

        if (!newPassword.equals(repeatNewPassword)) {
            throw new UserExceptions.PasswordDoNotMatchException();
        }

        PasswordResetToken passwordResetToken = passwordResetTokenService.getPasswordResetToken(token);

        if (passwordResetToken == null) {
            return;
        }

        User user = passwordResetToken.getUser();

        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);

        userRepository.save(user);
        passwordResetTokenService.deletePasswordResetToken(passwordResetToken);
    }

    @Override
    public void logout(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    @Override
    public void authenticate(AuthenticationRequest request, HttpServletResponse response) {
        var authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = (UserDetails) authenticate.getPrincipal();
        var accessToken = jwtService.generateAccessToken(user);

        var refreshToken = jwtService.generateRefreshToken(user);

        jwtService.addJwtCookiesToResponse(response, accessToken, refreshToken);
    }
}
