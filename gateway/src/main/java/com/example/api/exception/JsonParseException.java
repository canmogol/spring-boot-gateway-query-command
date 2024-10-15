package com.example.api.exception;

public class JsonParseException extends RuntimeException {

    public JsonParseException(String message, Exception exception) {
        super(message, exception);
    }
}
