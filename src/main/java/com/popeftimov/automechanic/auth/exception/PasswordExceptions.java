package com.popeftimov.automechanic.auth.exception;

public class PasswordExceptions {

    public static class InvalidPasswordException extends RuntimeException {
        public InvalidPasswordException() {
            super("Password must be at least 8 characters long, " +
                    "contain at least one uppercase letter, one number, and one special character.");
        }
    }

    public static class PasswordDoNotMatchException extends RuntimeException {
        public PasswordDoNotMatchException() {
            super("Password do not match.");
        }
    }
}
