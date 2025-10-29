package com.vandana.retailbilling.dto;

import com.vandana.retailbilling.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDTO {

    private Long billId;
    private String billNumber;
    private Long userId;
    private String customerName;
    private String customerPhone;
    private BigDecimal subtotal;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String billStatus;
    private LocalDateTime createdAt;
    private List<BillItemDTO> items;

    public static BillDTO fromEntity(Bill bill) {
        return new BillDTO(
                bill.getBillId(),
                bill.getBillNumber(),
                bill.getUser().getUserId(),
                bill.getCustomerName(),
                bill.getCustomerPhone(),
                bill.getSubtotal(),
                bill.getDiscountPercent(),
                bill.getDiscountAmount(),
                bill.getTotalAmount(),
                bill.getPaymentMethod().name(),
                bill.getBillStatus().name(),
                bill.getCreatedAt(),
                bill.getBillItems().stream()
                        .map(BillItemDTO::fromEntity)
                        .collect(Collectors.toList())
        );
    }
}
