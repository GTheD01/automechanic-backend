package com.popeftimov.automechanic.exception.signup;

import com.popeftimov.automechanic.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SignUpExceptionsHandler {

    @ExceptionHandler(SignUpExceptions.FailedToSendVerificationEmailException.class)
    public ResponseEntity<ApiError> handleFailedToSendEmailException(SignUpExceptions.FailedToSendVerificationEmailException ex,
                                                                     HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
