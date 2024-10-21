package com.example.product;

import com.example.product.exception.NoProductFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDTO> getProducts() {
        List<ProductModel> productModels = productRepository.findAll();
        if (productModels.isEmpty()) {
            throw new NoProductFoundException("No products found");
        }
        return productModels.stream()
                .map(this::toDTO)
                .toList();
    }

    private ProductDTO toDTO(ProductModel productModel) {
        return new ProductDTO(productModel.getId(), productModel.getName());
    }

}
