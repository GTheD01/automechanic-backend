package com.popeftimov.automechanic.exception;

public class GlobalException {

    public static class InvalidEnumValueException extends RuntimeException {
        public InvalidEnumValueException(String message) {
            super(message);
        }
    }

}
