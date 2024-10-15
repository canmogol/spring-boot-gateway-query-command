package com.example.api.product;

import com.example.app.downstream.ServiceClient;
import com.example.app.json.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductValidator productValidator;
    private final Mapper mapper;
    private final ServiceClient client;

    public ProductController(
            ProductValidator productValidator,
            HttpClient httpClient,
            Mapper mapper,
            @Value("${service.products.url}") String url
    ) {
        this.productValidator = productValidator;
        this.mapper = mapper;
        this.client = new ServiceClient(httpClient, url);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        final HttpResponse<String> response = client.get("/products");
        if (!HttpStatusCode.valueOf(response.statusCode()).is2xxSuccessful()) {
            return ResponseEntity.status(response.statusCode()).build();
        }
        return ResponseEntity.ok(mapper.readList(response.body(), Product.class));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> addProduct(@RequestBody Product product) {
        productValidator.validate(product);
        final HttpResponse<String> response = client.post("/products", mapper.writeString(product));
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        productValidator.validateId(id);
        final HttpResponse<String> response = client.delete("/products/%s".formatted(id));
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

}
