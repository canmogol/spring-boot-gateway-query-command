package com.example.api.exception;

public class UserNotFoundException extends Exception {
    private final String username;

    public UserNotFoundException(final String message, final String username) {
        super(message);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
