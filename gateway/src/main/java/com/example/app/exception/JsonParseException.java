package com.example.app.exception;

public class JsonParseException extends RuntimeException {

    public JsonParseException(String message, Exception exception) {
        super(message, exception);
    }
}
