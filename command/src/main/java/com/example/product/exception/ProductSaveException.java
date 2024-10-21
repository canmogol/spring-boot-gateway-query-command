package com.example.product.exception;

import com.example.product.ProductDTO;

public class ProductSaveException extends RuntimeException {

    private final ProductDTO product;

    public ProductSaveException(final String message, final ProductDTO product) {
        super(message);
        this.product = product;
    }

    public ProductDTO getProduct() {
        return product;
    }
}
