package com.popeftimov.automechanic.auth.exception;

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
}
