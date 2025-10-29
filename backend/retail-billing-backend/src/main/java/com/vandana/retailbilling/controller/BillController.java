package com.vandana.retailbilling.controller;

import com.vandana.retailbilling.dto.BillDTO;
import com.vandana.retailbilling.dto.CreateBillRequest;
import com.vandana.retailbilling.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    public ResponseEntity<BillDTO> createBill(@Valid @RequestBody CreateBillRequest request) {
        BillDTO createdBill = billService.createBill(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBill);
    }

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllBills() {
        List<BillDTO> bills = billService.getAllBills();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{billId}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Long billId) {
        BillDTO bill = billService.getBillById(billId);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/number/{billNumber}")
    public ResponseEntity<BillDTO> getBillByNumber(@PathVariable String billNumber) {
        BillDTO bill = billService.getBillByNumber(billNumber);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/today")
    public ResponseEntity<List<BillDTO>> getTodaysBills() {
        List<BillDTO> bills = billService.getTodaysBills();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/range")
    public ResponseEntity<List<BillDTO>> getBillsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<BillDTO> bills = billService.getBillsByDateRange(startDate, endDate);
        return ResponseEntity.ok(bills);
    }
}
