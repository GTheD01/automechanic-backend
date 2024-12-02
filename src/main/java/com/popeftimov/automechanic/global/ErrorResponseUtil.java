package com.popeftimov.automechanic.global;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

public class ErrorResponseUtil {

    public static ResponseEntity<Object> createErrorResponse(String field, String message, HttpStatus status) {
        Map<String, String> errorResponse = Collections.singletonMap(field, message);

        return new ResponseEntity<>(errorResponse, status);
    }
}
