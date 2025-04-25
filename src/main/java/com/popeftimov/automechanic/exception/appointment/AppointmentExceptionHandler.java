package com.popeftimov.automechanic.exception.appointment;

import com.popeftimov.automechanic.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppointmentExceptionHandler {

    @ExceptionHandler(AppointmentExceptions.AppointmentAtDateTimeExistsException.class)
    public ResponseEntity<ApiError> handleAppointmentAtDateTimeExistsException(AppointmentExceptions.AppointmentAtDateTimeExistsException ex,
                                                                      HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppointmentExceptions.AppointmentCannotScheduleInThePastException.class)
    public ResponseEntity<ApiError> handleAppointmentCannotScheduleInThePastException(AppointmentExceptions.AppointmentCannotScheduleInThePastException ex,
                                                                             HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppointmentExceptions.AppointmentNotFoundException.class)
    public ResponseEntity<ApiError> handleAppointmentNotFoundException(AppointmentExceptions.AppointmentNotFoundException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "NOT FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppointmentExceptions.AppointmentInvalidStatusException.class)
    public ResponseEntity<ApiError> handleAppointmentInvalidStatusException(AppointmentExceptions.AppointmentInvalidStatusException ex,
                                                                   HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "NOT FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppointmentExceptions.AppointmentNoCarSelectedException.class)
    public ResponseEntity<ApiError> handleAppointmentNoCarSelectedException(AppointmentExceptions.AppointmentNoCarSelectedException ex,
                                                                   HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
