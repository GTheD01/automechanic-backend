package com.popeftimov.automechanic.auth.exception;

public class ConfirmationExceptions {
    public static class EmailAlreadyConfirmed extends RuntimeException {
        public EmailAlreadyConfirmed() {
            super("Email already confirmed");
        }
    }

    public static class TokenExpired extends RuntimeException {
        public TokenExpired() {
            super("Token expired");
        }
    }
}
