package com.popeftimov.automechanic.auth;

import com.popeftimov.automechanic.auth.exception.EmailExceptions;
import com.popeftimov.automechanic.auth.exception.PasswordExceptions;
import com.popeftimov.automechanic.auth.confirmationtoken.ConfirmationToken;
import com.popeftimov.automechanic.auth.confirmationtoken.ConfirmationTokenService;
import com.popeftimov.automechanic.auth.validator.EmailValidator;
import com.popeftimov.automechanic.auth.validator.PasswordValidator;
import com.popeftimov.automechanic.config.JwtService;
import com.popeftimov.automechanic.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                Role.USER
        );

        boolean userExists = userRepository
                .findByEmail(user.getEmail())
                .isPresent();

        if (userExists) {
            throw new EmailExceptions.EmailAlreadyTakenException();
        }

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
//        TODO: send email
        return token;
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
