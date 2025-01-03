package com.popeftimov.automechanic.auth.passwordresettoken;

import com.popeftimov.automechanic.user.User;

public interface PasswordResetTokenService {

    String generatePasswordResetToken(String email);
    boolean validatePasswordResetToken(String email, String token);
    void deletePasswordResetToken(String token);
    void deleteAllByUser(User user);
    void sendPasswordResetEmail(String email, String link);
}
