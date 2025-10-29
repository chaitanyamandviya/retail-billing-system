package com.vandana.retailbilling.service;

import com.vandana.retailbilling.dto.CreateProductRequest;
import com.vandana.retailbilling.dto.ProductDTO;
import com.vandana.retailbilling.dto.UpdateProductRequest;
import com.vandana.retailbilling.entity.Product;
import com.vandana.retailbilling.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getActiveProducts() {
        return productRepository.findByIsActive(true)
                .stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        return ProductDTO.fromEntity(product);
    }

    public List<ProductDTO> searchProducts(String searchTerm) {
        return productRepository.findByProductNameContainingIgnoreCase(searchTerm)
                .stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setImagePath(request.getImagePath());
        product.setStockQuantity(request.getStockQuantity());
        product.setIsActive(request.getIsActive());

        Product savedProduct = productRepository.save(product);
        return ProductDTO.fromEntity(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        if (request.getProductName() != null) {
            product.setProductName(request.getProductName());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getImagePath() != null) {
            product.setImagePath(request.getImagePath());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        Product updatedProduct = productRepository.save(product);
        return ProductDTO.fromEntity(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Soft delete - just mark as inactive
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Transactional
    public void hardDeleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        productRepository.deleteById(productId);
    }

    @Transactional
    public ProductDTO quickAddProduct(String productName) {
        // Check if product already exists
        List<Product> existing = productRepository
                .findByProductNameContainingIgnoreCase(productName);

        if (!existing.isEmpty()) {
            // Return existing product
            return ProductDTO.fromEntity(existing.get(0));
        }

        // Create new product with name only
        Product product = new Product();
        product.setProductName(productName);
        product.setPrice(BigDecimal.ZERO); // No default price
        product.setStockQuantity(0); // No stock tracking
        product.setIsActive(true);

        Product savedProduct = productRepository.save(product);
        return ProductDTO.fromEntity(savedProduct);
    }

    public List<String> getProductNameSuggestions(String searchTerm) {
        return productRepository
                .findByIsActiveAndProductNameContainingIgnoreCase(true, searchTerm)
                .stream()
                .map(Product::getProductName)
                .distinct()
                .limit(10) // Top 10 suggestions
                .collect(Collectors.toList());
    }

    public ProductDTO getOrCreateProductByName(String productName) {
        String cleanedName = productName.trim();

        Optional<Product> existingProduct = productRepository
                .findByProductNameIgnoreCaseExact(cleanedName);

        if (existingProduct.isPresent()) {
            return ProductDTO.fromEntity(existingProduct.get());
        }

        // Create new product if doesn't exist
        Product newProduct = new Product();
        newProduct.setProductName(cleanedName);
        newProduct.setPrice(BigDecimal.ZERO);
        newProduct.setStockQuantity(0);
        newProduct.setIsActive(true);

        Product savedProduct = productRepository.save(newProduct);
        return ProductDTO.fromEntity(savedProduct);
    }

}
