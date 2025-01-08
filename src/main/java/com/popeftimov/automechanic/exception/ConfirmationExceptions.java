package com.popeftimov.automechanic.exception;

public class ConfirmationExceptions {
    public static class EmailAlreadyConfirmed extends RuntimeException {
        public EmailAlreadyConfirmed() {
            super("Email already confirmed");
        }
    }

    public static class TokenInvalidExpired extends RuntimeException {
        public TokenInvalidExpired() {
            super("Invalid or expired token");
        }
    }

    public static class TokenNotFound extends RuntimeException {
        public TokenNotFound() {
            super("Token not found");
        }
    }
}