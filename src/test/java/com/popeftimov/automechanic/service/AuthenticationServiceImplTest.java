package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.AuthenticationRequest;
import com.popeftimov.automechanic.dto.SignUpUserRequest;
import com.popeftimov.automechanic.exception.confirmationtoken.ConfirmationTokenExceptions;
import com.popeftimov.automechanic.exception.signup.SignUpExceptions;
import com.popeftimov.automechanic.exception.user.UserExceptions;
import com.popeftimov.automechanic.model.PasswordResetToken;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.security.JwtService;
import com.popeftimov.automechanic.validator.EmailValidator;
import com.popeftimov.automechanic.validator.PasswordValidator;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserService userService;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailValidator emailValidator;
    @Mock
    private PasswordValidator passwordValidator;
    @Mock
    private PasswordResetTokenService passwordResetTokenService;
    @Mock
    private EmailService emailService;
    @Mock
    private HttpServletResponse response;

    // --- SignUp Tests ---

    @Test
    void signUp_success() throws Exception {
        SignUpUserRequest request = new SignUpUserRequest("John", "Doe", "john@test.com", "StrongPass123!", "StrongPass123!");

        when(emailValidator.test("john@test.com")).thenReturn(true);
        when(passwordValidator.test("StrongPass123!")).thenReturn(true);
        when(userService.findOptionalUserByEmail("john@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        doNothing().when(userService).saveUser(any());
        doNothing().when(confirmationTokenService).saveConfirmationToken(any());
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());

        ResponseEntity<Void> response = authenticationService.signUp(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(emailService).sendVerificationEmail(eq("john@test.com"), contains("verify-email"));
    }

    @Test
    void signUp_invalidEmail_throwsException() {
        SignUpUserRequest request = new SignUpUserRequest("John", "Doe", "invalid", "pass", "pass");
        when(emailValidator.test("invalid")).thenReturn(false);
        assertThrows(UserExceptions.InvalidEmailException.class, () -> authenticationService.signUp(request));
    }

    @Test
    void signUp_weakPassword_throwsException() {
        SignUpUserRequest request = new SignUpUserRequest("John", "Doe", "john@test.com", "123", "123");
        when(emailValidator.test("john@test.com")).thenReturn(true);
        when(passwordValidator.test("123")).thenReturn(false);
        assertThrows(UserExceptions.InvalidPasswordException.class, () -> authenticationService.signUp(request));
    }

    @Test
    void signUp_passwordMismatch_throwsException() {
        SignUpUserRequest request = new SignUpUserRequest("John", "Doe", "john@test.com", "pass1", "pass2");
        when(emailValidator.test("john@test.com")).thenReturn(true);
        when(passwordValidator.test("pass1")).thenReturn(true);
        assertThrows(UserExceptions.PasswordDoNotMatchException.class, () -> authenticationService.signUp(request));
    }

    @Test
    void signUp_existingEmail_throwsException() {
        SignUpUserRequest request = new SignUpUserRequest("John", "Doe", "john@test.com", "pass", "pass");
        when(emailValidator.test("john@test.com")).thenReturn(true);
        when(passwordValidator.test("pass")).thenReturn(true);
        when(userService.findOptionalUserByEmail("john@test.com")).thenReturn(Optional.of(new User()));
        assertThrows(UserExceptions.EmailAlreadyTakenException.class, () -> authenticationService.signUp(request));
    }

    @Test
    void signUp_emailSendingFails_throwsException() throws Exception {
        SignUpUserRequest request = new SignUpUserRequest("John", "Doe", "john@test.com", "pass", "pass");
        when(emailValidator.test("john@test.com")).thenReturn(true);
        when(passwordValidator.test("pass")).thenReturn(true);
        when(userService.findOptionalUserByEmail("john@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        doThrow(MessagingException.class).when(emailService).sendVerificationEmail(any(), any());
        assertThrows(SignUpExceptions.FailedToSendVerificationEmailException.class, () -> authenticationService.signUp(request));
    }

    // --- Password Reset Tests ---

    @Test
    void requestPasswordReset_validEmail_callsService() {
        when(emailValidator.test("john@test.com")).thenReturn(true);
        authenticationService.requestPasswordReset("john@test.com");
        verify(passwordResetTokenService).generatePasswordResetToken("john@test.com");
    }

    @Test
    void requestPasswordReset_invalidEmail_throwsException() {
        when(emailValidator.test("bad")).thenReturn(false);
        assertThrows(UserExceptions.InvalidEmailException.class, () -> authenticationService.requestPasswordReset("bad"));
    }

    @Test
    void resetUserPassword_validData_success() {
        User user = new User();
        PasswordResetToken token = new PasswordResetToken(1L, "token", user, LocalDateTime.now().plusMinutes(10), LocalDateTime.now());

        when(passwordResetTokenService.validatePasswordResetToken("john@test.com", "token")).thenReturn(true);
        when(passwordValidator.test("newPass")).thenReturn(true);
        when(passwordResetTokenService.getPasswordResetToken("token")).thenReturn(token);
        when(passwordEncoder.encode("newPass")).thenReturn("encoded");

        authenticationService.resetUserPassword("john@test.com", "token", "newPass", "newPass");

        verify(userService).saveUser(user);
        verify(passwordResetTokenService).deletePasswordResetToken(token);
    }

    @Test
    void resetUserPassword_tokenInvalid_throwsException() {
        when(passwordResetTokenService.validatePasswordResetToken("john@test.com", "bad")).thenReturn(false);
        assertThrows(ConfirmationTokenExceptions.TokenInvalidExpiredException.class, () ->
                authenticationService.resetUserPassword("john@test.com", "bad", "p", "p"));
    }

    @Test
    void resetUserPassword_invalidPassword_throwsException() {
        when(passwordResetTokenService.validatePasswordResetToken(any(), any())).thenReturn(true);
        when(passwordValidator.test(any())).thenReturn(false);
        assertThrows(UserExceptions.InvalidPasswordException.class, () ->
                authenticationService.resetUserPassword("e", "t", "123", "123"));
    }

    @Test
    void resetUserPassword_passwordsDoNotMatch_throwsException() {
        when(passwordResetTokenService.validatePasswordResetToken(any(), any())).thenReturn(true);
        when(passwordValidator.test(any())).thenReturn(true);
        assertThrows(UserExceptions.PasswordDoNotMatchException.class, () ->
                authenticationService.resetUserPassword("e", "t", "pass1", "pass2"));
    }

    @Test
    void resetUserPassword_tokenNotFound_doesNothing() {
        when(passwordResetTokenService.validatePasswordResetToken(any(), any())).thenReturn(true);
        when(passwordValidator.test(any())).thenReturn(true);
        when(passwordResetTokenService.getPasswordResetToken(any())).thenReturn(null);

        assertDoesNotThrow(() -> authenticationService.resetUserPassword("e", "t", "pass", "pass"));

        verify(userService, never()).saveUser(any());
        verify(passwordResetTokenService, never()).deletePasswordResetToken(any());
    }

    // --- Authenticate & Logout Tests ---

    @Test
    void authenticate_validCredentials_addsCookies() {
        AuthenticationRequest request = new AuthenticationRequest("john@test.com", "pass");

        UserDetails userDetails = mock(UserDetails.class);
        Authentication auth = mock(Authentication.class);

        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateAccessToken(userDetails)).thenReturn("access");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh");

        authenticationService.authenticate(request, response);

        verify(jwtService).addJwtCookiesToResponse(response, "access", "refresh");
    }

    @Test
    void logout_removesCookies() {
        authenticationService.logout(response);
        verify(response, times(2)).addCookie(any());
    }
}
