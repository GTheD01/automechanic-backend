package com.popeftimov.automechanic.global;

import com.popeftimov.automechanic.auth.exception.EmailExceptions;
import com.popeftimov.automechanic.auth.exception.PasswordExceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailExceptions.EmailAlreadyTakenException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyTakenException(EmailExceptions.EmailAlreadyTakenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(EmailExceptions.InvalidEmailException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmailException(EmailExceptions.InvalidEmailException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(PasswordExceptions.InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(PasswordExceptions.InvalidPasswordException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
