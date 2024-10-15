package com.example.api.product;

import com.example.api.exception.InvalidProductException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ProductValidator {

    public void validate(String product) throws InvalidProductException {
        if (Objects.isNull(product) || product.isBlank()) {
            throw new InvalidProductException("Product cannot be empty", product);
        }
    }

}
