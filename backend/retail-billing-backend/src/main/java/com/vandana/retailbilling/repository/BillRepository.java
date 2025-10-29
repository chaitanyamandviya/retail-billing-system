package com.vandana.retailbilling.repository;

import com.vandana.retailbilling.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByBillNumber(String billNumber);

    List<Bill> findByUserUserId(Long userId);

    List<Bill> findByBillStatus(Bill.BillStatus billStatus);

    List<Bill> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Fixed: Use CAST to compare dates properly
    @Query("SELECT b FROM Bill b WHERE CAST(b.createdAt AS LocalDate) = CURRENT_DATE ORDER BY b.createdAt DESC")
    List<Bill> findTodaysBills();

    @Query("SELECT COUNT(b) FROM Bill b WHERE CAST(b.createdAt AS LocalDate) = CURRENT_DATE")
    Long countTodaysBills();
}
