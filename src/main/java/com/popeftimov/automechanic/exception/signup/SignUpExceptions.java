package com.popeftimov.automechanic.exception.signup;

public class SignUpExceptions {

    public static class FailedToSendVerificationEmail extends RuntimeException {
        public FailedToSendVerificationEmail() {
            super("Failed to send verification email. Please try again later.");
        }
    }
}
