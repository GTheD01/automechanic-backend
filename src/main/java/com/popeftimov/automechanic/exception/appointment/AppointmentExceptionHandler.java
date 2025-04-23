package com.popeftimov.automechanic.exception.appointment;

import com.popeftimov.automechanic.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppointmentExceptionHandler {

    @ExceptionHandler(AppointmentExceptions.AppointmentAtDateTimeExists.class)
    public ResponseEntity<ApiError> handleAppointmentAtDateTimeExists(AppointmentExceptions.AppointmentAtDateTimeExists ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppointmentExceptions.AppointmentCannotScheduleInThePast.class)
    public ResponseEntity<ApiError> handleAppointmentCannotScheduleInThePast(AppointmentExceptions.AppointmentCannotScheduleInThePast ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppointmentExceptions.AppointmentNotFound.class)
    public ResponseEntity<ApiError> handleAppointmentNotFound(AppointmentExceptions.AppointmentNotFound ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "NOT FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppointmentExceptions.AppointmentInvalidStatus.class)
    public ResponseEntity<ApiError> handleAppointmentInvalidStatus(AppointmentExceptions.AppointmentInvalidStatus ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "NOT FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
}
