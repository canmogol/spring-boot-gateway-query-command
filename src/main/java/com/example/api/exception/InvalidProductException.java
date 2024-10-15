package com.example.api.exception;

public class InvalidProductException extends Exception {

    private final String product;

    public InvalidProductException(final String message, final String product) {
        super(message);
        this.product = product;
    }

    public String getProduct() {
        return product;
    }
}
