package com.vandana.retailbilling.service;

import com.vandana.retailbilling.dto.BillDTO;
import com.vandana.retailbilling.dto.BillItemRequest;
import com.vandana.retailbilling.dto.CreateBillRequest;
import com.vandana.retailbilling.entity.Bill;
import com.vandana.retailbilling.entity.BillItem;
import com.vandana.retailbilling.entity.Product;
import com.vandana.retailbilling.entity.User;
import com.vandana.retailbilling.repository.BillItemRepository;
import com.vandana.retailbilling.repository.BillRepository;
import com.vandana.retailbilling.repository.ProductRepository;
import com.vandana.retailbilling.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public BillDTO createBill(CreateBillRequest request) {
        // 1. Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        // 2. Generate bill number (Format: VNDB-YYYYMMDD-XXXX)
        String billNumber = generateBillNumber();

        // 3. Calculate subtotal from all items
        BigDecimal subtotal = request.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Calculate discounts
        BigDecimal fixedDiscountPercent = BigDecimal.valueOf(10.00); // 10% fixed
        BigDecimal fixedDiscountAmount = subtotal.multiply(fixedDiscountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal manualDiscountAmount = request.getManualDiscountAmount() != null ?
                request.getManualDiscountAmount() : BigDecimal.ZERO;

        BigDecimal totalDiscountAmount = fixedDiscountAmount.add(manualDiscountAmount);

        // 5. Calculate total
        BigDecimal totalAmount = subtotal.subtract(totalDiscountAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        // 6. Create Bill entity
        Bill bill = new Bill();
        bill.setBillNumber(billNumber);
        bill.setUser(user);
        bill.setCustomerName(request.getCustomerName());
        bill.setCustomerPhone(request.getCustomerPhone());
        bill.setSubtotal(subtotal);
        bill.setDiscountPercent(fixedDiscountPercent);
        bill.setDiscountAmount(totalDiscountAmount);
        bill.setTotalAmount(totalAmount);
        bill.setPaymentMethod(Bill.PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
        bill.setBillStatus(Bill.BillStatus.COMPLETED);

        // 7. Save bill first
        Bill savedBill = billRepository.save(bill);

        // 8. Create bill items
        List<BillItem> billItems = request.getItems().stream()
                .map(itemRequest -> createBillItem(itemRequest, savedBill))
                .collect(Collectors.toList());

        // 9. Save all bill items
        billItemRepository.saveAll(billItems);
        savedBill.setBillItems(billItems);

        return BillDTO.fromEntity(savedBill);
    }

    private BillItem createBillItem(BillItemRequest request, Bill bill) {
        BillItem billItem = new BillItem();
        billItem.setBill(bill);
        billItem.setProductName(request.getProductName());
        billItem.setQuantity(request.getQuantity());
        billItem.setUnitPrice(request.getUnitPrice());

        // Calculate total price
        BigDecimal totalPrice = request.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));
        billItem.setTotalPrice(totalPrice);

        // Link to product if productId provided
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId()).orElse(null);
            billItem.setProduct(product);
        } else {
            // ✨ NEW FEATURE: Auto-add product name to suggestions
            Product autoCreatedProduct = autoAddProductIfNew(
                    request.getProductName(),
                    request.getUnitPrice()
            );
            billItem.setProduct(autoCreatedProduct);
        }

        return billItem;
    }

    /**
     * ✨ NEW METHOD: Auto-add product name to suggestions list
     * If product name doesn't exist, create it for future autocomplete
     */
    /**
     * ✨ ENHANCED: Auto-add product name with case-insensitive matching
     * Prevents duplicates like "T-Shirt", "t-shirt", "T-SHIRT"
     */
    private Product autoAddProductIfNew(String productName, BigDecimal lastUsedPrice) {
        // Trim and clean product name
        String cleanedName = productName.trim();

        // Check if product already exists (case-insensitive exact match)
        Optional<Product> existingProduct = productRepository
                .findByProductNameIgnoreCaseExact(cleanedName);

        if (existingProduct.isPresent()) {
            // Product exists - update last used price
            Product existing = existingProduct.get();
            existing.setPrice(lastUsedPrice);

            // Update the name to match the casing from the bill (optional - keeps latest format)
            // Comment this line if you want to keep original casing
            existing.setProductName(cleanedName);

            return productRepository.save(existing);
        } else {
            // Product doesn't exist - create new entry
            Product newProduct = new Product();
            newProduct.setProductName(cleanedName);
            newProduct.setPrice(lastUsedPrice);
            newProduct.setStockQuantity(0);
            newProduct.setIsActive(true);

            return productRepository.save(newProduct);
        }
    }



    private String generateBillNumber() {
        // Format: VNDB-YYYYMMDD-XXXX
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Count today's bills and increment
        Long todayCount = billRepository.countTodaysBills();
        String sequence = String.format("%04d", todayCount + 1);

        return "VNDB-" + dateStr + "-" + sequence;
    }

    public List<BillDTO> getAllBills() {
        return billRepository.findAll().stream()
                .map(BillDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public BillDTO getBillById(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + billId));
        return BillDTO.fromEntity(bill);
    }

    public BillDTO getBillByNumber(String billNumber) {
        Bill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new RuntimeException("Bill not found with number: " + billNumber));
        return BillDTO.fromEntity(bill);
    }

    public List<BillDTO> getTodaysBills() {
        return billRepository.findTodaysBills().stream()
                .map(BillDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<BillDTO> getBillsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return billRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(BillDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
