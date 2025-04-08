package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.model.PasswordResetToken;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService{

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserService userService;
    private final EmailService emailService;

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token).orElse(null);
    }

    @Override
    public void deletePasswordResetToken(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.deleteByToken(passwordResetToken);
    }

    @Override
    public void generatePasswordResetToken(String email) {
        Optional<User> userOptional = userService.findOptionalUserByEmail(email);

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        this.deleteAllPasswordResetTokensOfUser(user);

        String token = UUID.randomUUID().toString();

        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(expiryDate);

        passwordResetTokenRepository.save(passwordResetToken);

        emailService.sendPasswordResetEmail(email, token);
    }

    @Override
    public boolean validatePasswordResetToken(String email, String token) {
        Optional<User> userOptional = userService.findOptionalUserByEmail(email);
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken.isEmpty() || userOptional.isEmpty()) {
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
    public void deleteAllPasswordResetTokensOfUser(User user) {
        passwordResetTokenRepository.deleteAllByUser(user);
    }
}
