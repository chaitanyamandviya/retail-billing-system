package com.vandana.retailbilling.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private String customerName;

    private String customerPhone;

    @NotEmpty(message = "Bill must have at least one item")
    @Valid
    private List<BillItemRequest> items;

    // Optional: Manual discount amount (in addition to 10% fixed)
    @DecimalMin(value = "0.0", inclusive = true, message = "Manual discount cannot be negative")
    private BigDecimal manualDiscountAmount = BigDecimal.ZERO;

    @NotNull(message = "Payment method is required")
    private String paymentMethod; // CASH, ONLINE, CARD, UPI
}
