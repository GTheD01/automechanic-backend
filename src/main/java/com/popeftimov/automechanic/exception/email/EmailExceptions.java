package com.popeftimov.automechanic.exception.email;

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
