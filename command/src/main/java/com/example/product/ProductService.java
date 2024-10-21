package com.example.product;

import com.example.product.exception.ProductSaveException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public UUID addProduct(ProductDTO productDTO) {
        ProductModel productModel = toModel(productDTO);
        try {
            return productRepository.save(productModel).getId();
        } catch (DataAccessException e) {
            throw new ProductSaveException("Failed to save product", productDTO);
        }
    }

    public void deleteProduct(UUID productId) {
        productRepository.deleteById(productId);
    }

    private ProductModel toModel(ProductDTO productDTO) {
        return new ProductModel(productDTO.id(), productDTO.name());
    }

}
