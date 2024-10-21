package com.example.product.exception;

import com.example.product.ProductDTO;

public class InvalidProductException extends RuntimeException {

    private final ProductDTO productDTO;

    public InvalidProductException(final String message, final ProductDTO productDTO) {
        super(message);
        this.productDTO = productDTO;
    }

    public ProductDTO getProduct() {
        return productDTO;
    }
}
