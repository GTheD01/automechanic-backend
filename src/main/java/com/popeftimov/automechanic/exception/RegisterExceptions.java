package com.popeftimov.automechanic.exception;

public class RegisterExceptions {

    public static class FailedToSendEmail extends RuntimeException {
        public FailedToSendEmail() {
            super("Failed to send verification email. Please try again later.");
        }
    }
}
