package com.popeftimov.automechanic.exception.user;

public class UserExceptions {

    public static class InvalidPhoneNumber extends RuntimeException {
        public InvalidPhoneNumber() {
            super("Invalid phone number.");
        }
    }
}
