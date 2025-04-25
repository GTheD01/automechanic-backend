package com.popeftimov.automechanic.exception.authentication;

import com.popeftimov.automechanic.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthenticationExceptionsHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationExceptions.UserNotAuthorizedException.class)
    public ResponseEntity<ApiError> handleUserNotAuthorizedException(AuthenticationExceptions.UserNotAuthorizedException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationExceptions.InvalidOrExpiredRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidOrExpiredRefreshTokenException(
            AuthenticationExceptions.InvalidOrExpiredRefreshTokenException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationExceptions.InvalidOrExpiredAccessTokenException.class)
    public ResponseEntity<ApiError> handleInvalidOrExpiredAccessTokenException(
            AuthenticationExceptions.InvalidOrExpiredAccessTokenException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }
}
