package com.example.app.exception;

public class DownstreamServiceException extends RuntimeException {

    public DownstreamServiceException(final String message, final Exception exception) {
        super(message, exception);
    }

}
