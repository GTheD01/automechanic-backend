package com.popeftimov.automechanic.exception.authentication;

public class AuthenticationExceptions {

    public static class UserNotAuthorizedException extends RuntimeException {
        public UserNotAuthorizedException() {
            super("You are not authorized to update this profile.");
        }
    }

    public static class InvalidOrExpiredRefreshTokenException extends RuntimeException {
        public InvalidOrExpiredRefreshTokenException() {
            super("Invalid or expired refresh token.");
        }
    }

    public static class InvalidOrExpiredAccessTokenException extends RuntimeException {
        public InvalidOrExpiredAccessTokenException() {
            super("Invalid or expired access token.");
        }
    }

}
