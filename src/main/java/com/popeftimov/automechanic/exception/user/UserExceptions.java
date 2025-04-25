package com.popeftimov.automechanic.exception.user;

public class UserExceptions {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class PermissionDeniedException extends RuntimeException {
        public PermissionDeniedException() {
            super("Permission denied");
        }
    }

    public static class InvalidPhoneNumberException extends RuntimeException {
        public InvalidPhoneNumberException() {
            super("Invalid phone number.");
        }
    }

    public static class InvalidPasswordException extends RuntimeException {
        public InvalidPasswordException() {
            super("Password must be at least 8 characters long, " +
                    "contain at least one uppercase letter, one number, and one special character.");
        }
    }

    public static class PasswordDoNotMatchException extends RuntimeException {
        public PasswordDoNotMatchException() {
            super("Passwords do not match.");
        }
    }

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
