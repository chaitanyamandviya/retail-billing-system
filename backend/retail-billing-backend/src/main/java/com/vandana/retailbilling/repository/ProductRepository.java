package com.vandana.retailbilling.repository;

import com.vandana.retailbilling.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsActive(Boolean isActive);

    List<Product> findByProductNameContainingIgnoreCase(String productName);

    List<Product> findByIsActiveAndProductNameContainingIgnoreCase(Boolean isActive, String productName);

    // ✨ NEW: Case-insensitive exact match
    @Query("SELECT p FROM Product p WHERE LOWER(TRIM(p.productName)) = LOWER(TRIM(:productName))")
    Optional<Product> findByProductNameIgnoreCaseExact(@Param("productName") String productName);
}
