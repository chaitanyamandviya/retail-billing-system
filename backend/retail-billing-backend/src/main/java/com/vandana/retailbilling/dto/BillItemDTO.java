package com.vandana.retailbilling.dto;

import com.vandana.retailbilling.entity.BillItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItemDTO {

    private Long itemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public static BillItemDTO fromEntity(BillItem item) {
        return new BillItemDTO(
                item.getItemId(),
                item.getProduct() != null ? item.getProduct().getProductId() : null,
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
        );
    }
}
