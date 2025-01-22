package com.popeftimov.automechanic.exception;

public class UserExceptions {

    public static class InvalidPhoneNumber extends RuntimeException {
        public InvalidPhoneNumber() {
            super("Invalid phone number.");
        }
    }
}
