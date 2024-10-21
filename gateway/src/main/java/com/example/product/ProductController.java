package com.example.product;

import com.example.app.downstream.ServiceClient;
import com.example.app.json.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductValidator productValidator;
    private final Mapper mapper;
    private final ServiceClient commandClient;
    private final ServiceClient queryClient;

    public ProductController(
            ProductValidator productValidator,
            HttpClient httpClient,
            Mapper mapper,
            @Value("${service.products.command.url}") String commandUrl,
            @Value("${service.products.query.url}") String queryUrl
    ) {
        this.productValidator = productValidator;
        this.mapper = mapper;
        this.commandClient = new ServiceClient(httpClient, commandUrl);
        this.queryClient = new ServiceClient(httpClient, queryUrl);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts() {
        final HttpResponse<String> response = queryClient.get("/products");
        if (!HttpStatusCode.valueOf(response.statusCode()).is2xxSuccessful()) {
            return ResponseEntity
                    .status(response.statusCode())
                    .build();
        }
        return ResponseEntity
                .status(response.statusCode())
                .body(mapper.readList(response.body(), ProductDTO.class));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> addProduct(@RequestBody ProductDTO productDTO) {
        productValidator.validate(productDTO);
        final HttpResponse<String> response = commandClient.post("/products", mapper.writeString(productDTO));
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
        productValidator.validateId(id);
        final HttpResponse<String> response = commandClient.delete("/products/%s".formatted(id));
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

}
