package com.popeftimov.automechanic.auth.passwordresettoken;

import com.popeftimov.automechanic.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    List<PasswordResetToken> findByUser(User user);
    void deleteByToken(String token);
    @Transactional
    void deleteAllByUser(User user);
}
