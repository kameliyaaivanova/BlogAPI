package com.example.Blog.Project.exception;

public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(final String message) {
        super(message);
    }

    public AuthenticationFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
