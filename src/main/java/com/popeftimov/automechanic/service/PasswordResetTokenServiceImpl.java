package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.model.PasswordResetToken;
import com.popeftimov.automechanic.repository.PasswordResetTokenRepository;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService{

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final UserRepository userRepository;

    private final EmailService emailService;

    @Override
    public String generatePasswordResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }

        User user = userOpt.get();
//        Delete all reset password tokens of the user
        deleteAllByUser(user);

        String token = UUID.randomUUID().toString();

        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(expiryDate);

        passwordResetTokenRepository.save(passwordResetToken);

        sendPasswordResetEmail(email, token);

        return token;
    }

    @Override
    public boolean validatePasswordResetToken(String email, String token) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (userOptional.isEmpty() || passwordResetToken.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        User tokenUser = passwordResetToken.get().getUser();

        if (!user.equals(tokenUser)) {
            return false;
        }

        return !passwordResetToken.get().getExpiryDate().isBefore(LocalDateTime.now());
    }

    @Override
    public void deletePasswordResetToken(String token) {
        passwordResetTokenRepository.deleteByToken(token);
    }

    @Override
    public void deleteAllByUser(User user) {
        passwordResetTokenRepository.deleteAllByUser(user);
    }

    @Override
    public void sendPasswordResetEmail(String email, String code) {
        String subject = "Password reset";
        Context context = new Context();
        context.setVariable("subject", subject);
        context.setVariable("code", code);
        try {
            emailService.sendEmail(email, subject, "PasswordReset", context);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
