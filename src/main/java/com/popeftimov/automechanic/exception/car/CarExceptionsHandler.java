package com.popeftimov.automechanic.exception.car;

import com.popeftimov.automechanic.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CarExceptionsHandler {

    @ExceptionHandler(CarExceptions.CarBrandNotFoundException.class)
    public ResponseEntity<ApiError> handleInvalidPhoneNumberException(CarExceptions.CarBrandNotFoundException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CarExceptions.CarModelNotFoundException.class)
    public ResponseEntity<ApiError> handleInvalidPhoneNumberException(CarExceptions.CarModelNotFoundException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CarExceptions.CarYearInvalidException.class)
    public ResponseEntity<ApiError> handleInvalidPhoneNumberException(CarExceptions.CarYearInvalidException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CarExceptions.CarNotFoundException.class)
    public ResponseEntity<ApiError> handleCarNotFoundException(CarExceptions.CarNotFoundException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "NOT FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CarExceptions.CarBrandExistsException.class)
    public ResponseEntity<ApiError> handleCarBrandExistsException(CarExceptions.CarBrandExistsException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CarExceptions.CarModelExistsException.class)
    public ResponseEntity<ApiError> handleCarModelExistsException(CarExceptions.CarModelExistsException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CarExceptions.CarBrandNotProvidedException.class)
    public ResponseEntity<ApiError> handleCarBrandNotProvidedException(CarExceptions.CarBrandNotProvidedException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
