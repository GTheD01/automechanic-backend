package com.popeftimov.automechanic.exception;

public class EmailExceptions {

    public static class EmailAlreadyTakenException extends RuntimeException {
        public EmailAlreadyTakenException() {
            super("Email already taken");
        }
    }


    public static class InvalidEmailException extends RuntimeException {
        public InvalidEmailException() {
            super("Invalid email");
        }
    }
}
