package com.popeftimov.automechanic.exception.confirmationtoken;

public class ConfirmationTokenExceptions {
    public static class EmailAlreadyConfirmedException extends RuntimeException {
        public EmailAlreadyConfirmedException() {
            super("Email already confirmed");
        }
    }

    public static class TokenInvalidExpiredException extends RuntimeException {
        public TokenInvalidExpiredException() {
            super("Invalid or expired token");
        }
    }
}
