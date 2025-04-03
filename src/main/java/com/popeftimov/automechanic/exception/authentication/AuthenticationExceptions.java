package com.popeftimov.automechanic.exception.authentication;

public class AuthenticationExceptions {

    public static class UserNotAuthorizedException extends RuntimeException {
        public UserNotAuthorizedException() {
            super("You are not authorized to update this profile.");
        }
    }

    public static class InvalidOrExpiredRefreshToken extends RuntimeException {
        public InvalidOrExpiredRefreshToken() {
            super("Invalid or expired refresh token.");
        }
    }

    public static class InvalidOrExpiredAccessToken extends RuntimeException {
        public InvalidOrExpiredAccessToken() {
            super("Invalid or expired access token.");
        }
    }

}
