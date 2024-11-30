package com.popeftimov.automechanic.auth;

import com.popeftimov.automechanic.auth.exception.EmailExceptions;
import com.popeftimov.automechanic.auth.exception.PasswordExceptions;
import com.popeftimov.automechanic.auth.token.ConfirmationToken;
import com.popeftimov.automechanic.auth.token.ConfirmationTokenService;
import com.popeftimov.automechanic.auth.validator.EmailValidator;
import com.popeftimov.automechanic.auth.validator.PasswordValidator;
import com.popeftimov.automechanic.config.JwtService;
import com.popeftimov.automechanic.user.Role;
import com.popeftimov.automechanic.user.User;
import com.popeftimov.automechanic.user.UserRepository;
import com.popeftimov.automechanic.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;

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

        String token = userService.signUpUser(
                new User(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        Role.USER
                )
        );

        String link = "http://localhost:8080/api/v1/auth/register/confirm?token=" + token;
//        TODO: send email
        return token;
    }

    @Transactional
    public Map<String, String> confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Expired token");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.enableUser(confirmationToken.getUser().getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("message", "confirmed");

        return response;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
