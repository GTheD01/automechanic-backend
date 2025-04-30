package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.exception.confirmationtoken.ConfirmationTokenExceptions;
import com.popeftimov.automechanic.model.ConfirmationToken;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.ConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;

    @Test
    void testSaveConfirmationToken() {
        ConfirmationToken token = new ConfirmationToken();
        confirmationTokenService.saveConfirmationToken(token);

        verify(confirmationTokenRepository, times(1)).save(token);
    }

    @Test
    void testGetToken_found() {
        String tokenValue = "valid-token";
        ConfirmationToken token = new ConfirmationToken();
        token.setToken(tokenValue);

        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        Optional<ConfirmationToken> result = confirmationTokenService.getToken(tokenValue);

        assertTrue(result.isPresent());
        verify(confirmationTokenRepository, times(1)).findByToken(tokenValue);
    }

    @Test
    void testGetToken_notFound() {
        String tokenValue = "invalid-token";
        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        Optional<ConfirmationToken> result = confirmationTokenService.getToken(tokenValue);

        assertTrue(result.isEmpty());
        verify(confirmationTokenRepository, times(1)).findByToken(tokenValue);
    }

    @Test
    void testSetConfirmedAt() {
        String token = "valid-token";
        confirmationTokenService.setConfirmedAt(token);

        verify(confirmationTokenRepository, times(1)).updateConfirmedAt(any(), any());
    }

    @Test
    void testConfirmToken_tokenNotFound() {
        String tokenValue = "invalid-token";

        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        assertThrows(ConfirmationTokenExceptions.TokenInvalidExpiredException.class, () -> {
            confirmationTokenService.confirmToken(tokenValue);
        });

        verify(confirmationTokenRepository, times(1)).findByToken(tokenValue);
    }

    @Test
    void testConfirmToken_emailAlreadyConfirmed() {
        String tokenValue = "valid-token";
        ConfirmationToken token = new ConfirmationToken();
        token.setToken(tokenValue);
        token.setConfirmedAt(LocalDateTime.now());

        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        assertThrows(ConfirmationTokenExceptions.EmailAlreadyConfirmedException.class, () -> {
            confirmationTokenService.confirmToken(tokenValue);
        });

        verify(confirmationTokenRepository, times(1)).findByToken(tokenValue);
    }

    @Test
    void testConfirmToken_tokenExpired() {
        String tokenValue = "expired-token";
        ConfirmationToken token = new ConfirmationToken();
        token.setToken(tokenValue);
        token.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        assertThrows(ConfirmationTokenExceptions.TokenInvalidExpiredException.class, () -> {
            confirmationTokenService.confirmToken(tokenValue);
        });

        verify(confirmationTokenRepository, times(1)).findByToken(tokenValue);
    }

    @Test
    void testConfirmToken_success() {
        String tokenValue = "valid-token";

        User user = mock(User.class);
        when(user.getEmail()).thenReturn("user@example.com");

        ConfirmationToken token = new ConfirmationToken();
        token.setToken(tokenValue);
        token.setExpiresAt(LocalDateTime.now().plusDays(1));
        token.setConfirmedAt(null);
        token.setUser(user);

        when(confirmationTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        when(confirmationTokenRepository.updateConfirmedAt(any(), any())).thenReturn(1);

        doNothing().when(userService).enableUser(any());

        ResponseEntity<Void> response = confirmationTokenService.confirmToken(tokenValue);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(confirmationTokenRepository, times(1)).findByToken(tokenValue);
        verify(confirmationTokenRepository, times(1)).updateConfirmedAt(any(), any());
        verify(userService, times(1)).enableUser("user@example.com");
    }


}
