package com.popeftimov.automechanic.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String path;
    private long timestamp;

    public ApiError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = System.currentTimeMillis();
    }

}
