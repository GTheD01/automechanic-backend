package com.popeftimov.automechanic.exception.confirmationtoken;

import com.popeftimov.automechanic.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ConfirmationTokenExceptionsHandler {
    @ExceptionHandler(ConfirmationTokenExceptions.EmailAlreadyConfirmed.class)
    public ResponseEntity<ApiError> handleEmailAlreadyConfirmed(ConfirmationTokenExceptions.EmailAlreadyConfirmed ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConfirmationTokenExceptions.TokenInvalidExpired.class)
    public ResponseEntity<ApiError> handleTokenExpired(ConfirmationTokenExceptions.TokenInvalidExpired ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConfirmationTokenExceptions.TokenNotFound.class)
    public ResponseEntity<ApiError> handleTokenExpired(ConfirmationTokenExceptions.TokenNotFound ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

}
