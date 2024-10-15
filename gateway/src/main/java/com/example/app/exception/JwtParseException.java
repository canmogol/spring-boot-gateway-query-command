package com.example.app.exception;

public class JwtParseException extends RuntimeException {
    public JwtParseException(final String message) {
        super(message);
    }
}
