package com.vandana.retailbilling.dto;

import com.vandana.retailbilling.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imagePath;
    private Integer stockQuantity;
    private Boolean isActive;

    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                product.getImagePath(),
                product.getStockQuantity(),
                product.getIsActive()
        );
    }
}
