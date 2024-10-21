package com.example.product.exception;

public class NoProductFoundException extends RuntimeException {

    public NoProductFoundException(final String message) {
        super(message);
    }

}
