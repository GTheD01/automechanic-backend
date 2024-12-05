package com.popeftimov.automechanic.auth.passwordresettoken;

import com.popeftimov.automechanic.user.User;

public interface PasswordResetTokenService {

    String generatePasswordResetToken(String email);
    boolean validatePasswordResetToken(String token);
    void deletePasswordResetToken(String token);
    void deleteAllByUser(User user);
}
