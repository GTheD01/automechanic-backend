package com.popeftimov.automechanic.global;

import com.popeftimov.automechanic.appointment.AppointmentExceptions;
import com.popeftimov.automechanic.auth.exception.ConfirmationExceptions;
import com.popeftimov.automechanic.auth.exception.EmailExceptions;
import com.popeftimov.automechanic.auth.exception.PasswordExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailExceptions.EmailAlreadyTakenException.class)
    public ResponseEntity<Object> handleEmailAlreadyTakenException(EmailExceptions.EmailAlreadyTakenException ex) {
        return ErrorResponseUtil.createErrorResponse("email", ex.getMessage(), HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(EmailExceptions.InvalidEmailException.class)
    public ResponseEntity<Object> handleInvalidEmailException(EmailExceptions.InvalidEmailException ex) {
        return ErrorResponseUtil.createErrorResponse("email", ex.getMessage(), HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(PasswordExceptions.InvalidPasswordException.class)
    public ResponseEntity<Object> handleInvalidPasswordException(PasswordExceptions.InvalidPasswordException ex) {
        return ErrorResponseUtil.createErrorResponse("newPassword", ex.getMessage(), HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(PasswordExceptions.PasswordDoNotMatchException.class)
    public ResponseEntity<Object> handlePasswordDoNotMatchException(PasswordExceptions.PasswordDoNotMatchException ex) {
        return ErrorResponseUtil.createErrorResponse("repeatNewPassword", ex.getMessage(), HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(AppointmentExceptions.AppointmentAtDateTimeExists.class)
    public ResponseEntity<Object> handleAppointmentAtDateTimeExists(AppointmentExceptions.AppointmentAtDateTimeExists ex) {
        return ErrorResponseUtil.createErrorResponse("appointment", ex.getMessage(), HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(AppointmentExceptions.AppointmentCannotScheduleInThePast.class)
    public ResponseEntity<Object> handleAppointmentCannotScheduleInThePast(AppointmentExceptions.AppointmentCannotScheduleInThePast ex) {
        return ErrorResponseUtil.createErrorResponse("appointment", ex.getMessage(), HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(ConfirmationExceptions.EmailAlreadyConfirmed.class)
    public ResponseEntity<Object> handleEmailAlreadyConfirmed(ConfirmationExceptions.EmailAlreadyConfirmed ex) {
        return ErrorResponseUtil.createErrorResponse("email", ex.getMessage(), HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(ConfirmationExceptions.TokenInvalidExpired.class)
    public ResponseEntity<Object> handleTokenExpired(ConfirmationExceptions.TokenInvalidExpired ex) {
        return ErrorResponseUtil.createErrorResponse("token", ex.getMessage(), HttpStatus.BAD_REQUEST );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        errors.put("error", "METHOD NOT ALLOWED");
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status", HttpStatus.UNAUTHORIZED.value());
        errors.put("error", "UNAUTHORIZED");
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }
}
