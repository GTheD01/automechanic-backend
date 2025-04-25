package com.popeftimov.automechanic.exception.signup;

public class SignUpExceptions {

    public static class FailedToSendVerificationEmailException extends RuntimeException {
        public FailedToSendVerificationEmailException() {
            super("Failed to send verification email. Please try again later.");
        }
    }
}
