package com.popeftimov.automechanic.auth.passwordresettoken;

import com.popeftimov.automechanic.global.EmailService;
import com.popeftimov.automechanic.user.User;
import com.popeftimov.automechanic.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
        passwordResetToken.setCreatedAt(LocalDateTime.now());

        passwordResetTokenRepository.save(passwordResetToken);

        sendPasswordResetEmail(email, token);

        return token;
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken.isEmpty()) {
            return false;
        }

        if (passwordResetToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
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
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f7f7f7;\">"
                + "<div style=\"max-width: 600px; margin: 30px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);\">"
                + "<h2 style=\"color: #333; text-align: center; font-size: 24px; margin-bottom: 20px;\">Password Reset Request</h2>"
                + "<p style=\"font-size: 16px; color: #555; line-height: 1.6;\">Hi there,</p>"
                + "<p style=\"font-size: 16px; color: #555; line-height: 1.6;\">We received a request to reset your password. Please use the code below to complete the process:</p>"
                + "<div style=\"background-color: #f9f9f9; padding: 20px; border-radius: 5px; box-shadow: 0 0 8px rgba(0,0,0,0.1); margin-top: 20px;\">"
                + "<h3 style=\"color: #333; font-size: 18px; margin-bottom: 10px;\">Your Reset Code:</h3>"
                + "<p style=\"font-size: 20px; font-weight: bold; color: #007bff; text-align: center;\">" + code + "</p>"
                + "</div>"
                + "<p style=\"font-size: 16px; color: #555; line-height: 1.6; margin-top: 20px;\">If you did not request a password reset, please ignore this email.</p>"
                + "<p style=\"font-size: 16px; color: #555; line-height: 1.6;\">Best regards,<br/>The Automechanic Team</p>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendEmail(email, subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
