package com.example.api.product;

import com.example.api.exception.InvalidProductException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductValidator productValidator;

    private final List<String> products = new ArrayList<>();

    public ProductController(final ProductValidator productValidator) {
        this.productValidator = productValidator;
        products.add("product1");
        products.add("product2");
        products.add("product3");
    }

    @GetMapping
    public List<String> getProducts() {
        return products;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public void addProduct(@RequestBody String product) throws InvalidProductException {
        productValidator.validate(product);
        products.add(product);
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping
    public void deleteProduct(@RequestBody String product) {
        products.remove(product);
    }

}
