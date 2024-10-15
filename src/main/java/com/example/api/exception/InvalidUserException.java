package com.example.api.exception;

public class InvalidUserException extends Exception {
    public InvalidUserException(final String message) {
        super(message);
    }
}
