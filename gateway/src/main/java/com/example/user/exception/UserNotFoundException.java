package com.example.user.exception;

public class UserNotFoundException extends RuntimeException {
    private final String username;

    public UserNotFoundException(final String message, final String username) {
        super(message);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
