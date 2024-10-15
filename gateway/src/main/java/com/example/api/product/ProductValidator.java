package com.example.api.product;

import com.example.api.exception.InvalidProductException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class ProductValidator {

    private static final int MIN_ID_LENGTH = 3;

    public void validate(Product product) throws InvalidProductException {
        if (isNull(product) || isNull(product.id()) || isNull(product.name())
                || product.id().isEmpty() || product.name().isEmpty()) {
            throw new InvalidProductException("Product cannot be empty", product);
        }
    }

    public void validateId(final String id) {
        if (isNull(id) || id.length() < MIN_ID_LENGTH) {
            throw new InvalidProductException("Product id is not valid", new Product(id, ""));
        }
    }

}
