package com.vandana.retailbilling.repository;

import com.vandana.retailbilling.entity.ShopSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopSettingsRepository extends JpaRepository<ShopSettings, Long> {
    // Shop settings typically has only 1 row
}
