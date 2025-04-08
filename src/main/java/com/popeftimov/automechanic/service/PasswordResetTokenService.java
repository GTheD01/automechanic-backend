package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.model.PasswordResetToken;
import com.popeftimov.automechanic.model.User;

public interface PasswordResetTokenService {

    PasswordResetToken getPasswordResetToken(String token);
    void deletePasswordResetToken(PasswordResetToken token);
    void generatePasswordResetToken(String email);
    boolean validatePasswordResetToken(String email, String token);
    void deleteAllPasswordResetTokensOfUser(User user);
}
