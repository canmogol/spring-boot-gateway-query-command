package com.example.app.exception;

public class JwtParserException extends RuntimeException {
    public JwtParserException(final String message, final Exception e) {
        super(message, e);
    }
}
