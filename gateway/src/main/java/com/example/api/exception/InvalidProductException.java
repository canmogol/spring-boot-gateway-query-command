package com.example.api.exception;

import com.example.api.product.Product;

public class InvalidProductException extends RuntimeException {

    private final Product product;

    public InvalidProductException(final String message, final Product product) {
        super(message);
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
