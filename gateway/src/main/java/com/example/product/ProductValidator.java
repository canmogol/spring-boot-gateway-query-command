package com.example.product;

import com.example.product.exception.InvalidProductException;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
public class ProductValidator {

    private static final int MIN_ID_LENGTH = 3;

    public void validate(ProductDTO productDTO) throws InvalidProductException {
        if (isNull(productDTO) || isNull(productDTO.id()) || isNull(productDTO.name())
                || productDTO.name().isEmpty()) {
            throw new InvalidProductException("Product cannot be empty", productDTO);
        }
    }

    public void validateId(final UUID id) {
        if (isNull(id) || id.toString().length() < MIN_ID_LENGTH) {
            throw new InvalidProductException("Product id is not valid", new ProductDTO(id, ""));
        }
    }

}
