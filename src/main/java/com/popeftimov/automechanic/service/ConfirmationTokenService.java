package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.exception.confirmationtoken.ConfirmationTokenExceptions;
import com.popeftimov.automechanic.model.ConfirmationToken;
import com.popeftimov.automechanic.repository.ConfirmationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    public void setConfirmedAt(String token) {
        confirmationTokenRepository.updateConfirmedAt(
                token,
                LocalDateTime.now()
        );
    }

    @Transactional
    public ResponseEntity<Void> confirmToken(String token) {
        ConfirmationToken confirmationToken = getToken(token)
                .orElseThrow(ConfirmationTokenExceptions.TokenInvalidExpiredException::new);

        if (confirmationToken.getConfirmedAt() != null) {
            throw new ConfirmationTokenExceptions.EmailAlreadyConfirmedException();
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new ConfirmationTokenExceptions.TokenInvalidExpiredException();
        }

        this.setConfirmedAt(token);
        userService.enableUser(confirmationToken.getUser().getEmail());

        return ResponseEntity.ok().build();
    }
}
