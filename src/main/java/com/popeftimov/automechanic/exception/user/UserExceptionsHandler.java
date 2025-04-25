package com.popeftimov.automechanic.exception.user;

import com.popeftimov.automechanic.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionsHandler {

    @ExceptionHandler(UserExceptions.UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserExceptions.UserNotFoundException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "NOT FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserExceptions.InvalidPhoneNumberException.class)
    public ResponseEntity<ApiError> handleInvalidPhoneNumberException(UserExceptions.InvalidPhoneNumberException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExceptions.InvalidPasswordException.class)
    public ResponseEntity<ApiError> handleInvalidPasswordException(UserExceptions.InvalidPasswordException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExceptions.PasswordDoNotMatchException.class)
    public ResponseEntity<ApiError> handlePasswordDoNotMatchException(UserExceptions.PasswordDoNotMatchException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExceptions.EmailAlreadyTakenException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyTakenException(UserExceptions.EmailAlreadyTakenException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExceptions.InvalidEmailException.class)
    public ResponseEntity<ApiError> handleInvalidEmailException(UserExceptions.InvalidEmailException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExceptions.PermissionDeniedException.class)
    public ResponseEntity<ApiError> handlePermissionDeniedException(UserExceptions.PermissionDeniedException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                "FORBIDDEN",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }
}
