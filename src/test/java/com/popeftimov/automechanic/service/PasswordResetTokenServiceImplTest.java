package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.model.PasswordResetToken;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceImplTest {
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetTokenServiceImpl passwordResetTokenService;

    private User user;
    private PasswordResetToken passwordResetToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
    }

    @Test
    void testGetPasswordResetToken_shouldReturnToken() {
        // Arrange
        when(passwordResetTokenRepository.findByToken(passwordResetToken.getToken())).thenReturn(Optional.of(passwordResetToken));

        // Act
        PasswordResetToken result = passwordResetTokenService.getPasswordResetToken(passwordResetToken.getToken());

        // Assert
        assertNotNull(result);
        assertEquals(passwordResetToken.getToken(), result.getToken());
        verify(passwordResetTokenRepository).findByToken(passwordResetToken.getToken());
    }

    @Test
    void testDeletePasswordResetToken_shouldDeleteToken() {
        // Arrange
        doNothing().when(passwordResetTokenRepository).deleteByToken(passwordResetToken);

        // Act
        passwordResetTokenService.deletePasswordResetToken(passwordResetToken);

        // Assert
        verify(passwordResetTokenRepository).deleteByToken(passwordResetToken);
    }

    @Test
    void testGeneratePasswordResetToken_shouldGenerateAndSendToken() {
        // Arrange
        when(userService.findOptionalUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
        doNothing().when(emailService).sendPasswordResetEmail(eq(user.getEmail()), anyString());

        // Act
        passwordResetTokenService.generatePasswordResetToken(user.getEmail());

        // Assert
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(user.getEmail()), anyString());
    }

    @Test
    void testGeneratePasswordResetToken_shouldNotGenerateWhenUserNotFound() {
        // Arrange
        when(userService.findOptionalUserByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act
        passwordResetTokenService.generatePasswordResetToken(user.getEmail());

        // Assert
        verify(passwordResetTokenRepository, times(0)).save(any(PasswordResetToken.class));
        verify(emailService, times(0)).sendPasswordResetEmail(eq(user.getEmail()), anyString());
    }

    @Test
    void testValidatePasswordResetToken_shouldReturnTrueForValidToken() {
        // Arrange
        when(userService.findOptionalUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByToken(passwordResetToken.getToken())).thenReturn(Optional.of(passwordResetToken));

        // Act
        boolean result = passwordResetTokenService.validatePasswordResetToken(user.getEmail(), passwordResetToken.getToken());

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidatePasswordResetToken_shouldReturnFalseForInvalidToken() {
        // Arrange
        when(userService.findOptionalUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        boolean result = passwordResetTokenService.validatePasswordResetToken(user.getEmail(), "invalid-token");

        // Assert
        assertFalse(result);
    }

    @Test
    void testValidatePasswordResetToken_shouldReturnFalseForExpiredToken() {
        // Arrange
        passwordResetToken.setExpiryDate(LocalDateTime.now().minusMinutes(1)); // Set token to expired
        when(userService.findOptionalUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.findByToken(passwordResetToken.getToken())).thenReturn(Optional.of(passwordResetToken));

        // Act
        boolean result = passwordResetTokenService.validatePasswordResetToken(user.getEmail(), passwordResetToken.getToken());

        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteAllPasswordResetTokensOfUser_shouldDeleteAllTokens() {
        // Arrange
        doNothing().when(passwordResetTokenRepository).deleteAllByUser(user);

        // Act
        passwordResetTokenService.deleteAllPasswordResetTokensOfUser(user);

        // Assert
        verify(passwordResetTokenRepository).deleteAllByUser(user);
    }
}