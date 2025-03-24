package com.popeftimov.automechanic.exception.authentication;

public class AuthenticationExceptions {

    public static class UserNotAuthorizedException extends RuntimeException {
        public UserNotAuthorizedException() {
            super("You are not authorized to update this profile.");
        }
    }

}
