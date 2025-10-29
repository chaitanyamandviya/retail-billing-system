package com.vandana.retailbilling.controller;

import com.vandana.retailbilling.dto.CreateProductRequest;
import com.vandana.retailbilling.dto.ProductDTO;
import com.vandana.retailbilling.dto.UpdateProductRequest;
import com.vandana.retailbilling.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        List<ProductDTO> products = activeOnly ?
                productService.getActiveProducts() :
                productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        ProductDTO product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam String query) {
        List<ProductDTO> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductDTO createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductDTO updatedProduct = productService.updateProduct(productId, request);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}/hard")
    public ResponseEntity<Void> hardDeleteProduct(@PathVariable Long productId) {
        productService.hardDeleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getProductSuggestions(
            @RequestParam String query) {
        List<String> suggestions = productService.getProductNameSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }
}
