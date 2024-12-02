package com.popeftimov.automechanic.auth.confirmationtoken;

import com.popeftimov.automechanic.auth.exception.ConfirmationExceptions;
import com.popeftimov.automechanic.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserService userService;

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token,
                LocalDateTime.now()
        );
    }

    @Transactional
    public ConfirmationTokenResponse confirmToken(String token) {
        ConfirmationToken confirmationToken = getToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new ConfirmationExceptions.EmailAlreadyConfirmed();
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ConfirmationExceptions.TokenExpired();
        }

        setConfirmedAt(token);
        userService.enableUser(confirmationToken.getUser().getEmail());

        return ConfirmationTokenResponse
                .builder()
                .confirmationToken("confirmed")
                .build();
    }
}
