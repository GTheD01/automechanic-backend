package com.popeftimov.automechanic.exception.report;

import com.popeftimov.automechanic.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReportExceptionsHandler {

    @ExceptionHandler(ReportExceptions.InvalidReportDescriptionException.class)
    public ResponseEntity<ApiError> handleCarNotFoundException(ReportExceptions.InvalidReportDescriptionException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReportExceptions.ReportNotFoundException.class)
    public ResponseEntity<ApiError> handleReportNotFoundException(ReportExceptions.ReportNotFoundException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "NOT FOUND",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReportExceptions.InvalidReportTypeException.class)
    public ResponseEntity<ApiError> handleInvalidReportTypeException(ReportExceptions.InvalidReportTypeException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReportExceptions.ReportTypeNotProvidedException.class)
    public ResponseEntity<ApiError> handleInvalidReportTypeException(ReportExceptions.ReportTypeNotProvidedException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReportExceptions.ReportAlreadyAnsweredException.class)
    public ResponseEntity<ApiError> handleReportAlreadyAnsweredException(ReportExceptions.ReportAlreadyAnsweredException ex, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "BAD REQUEST",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
