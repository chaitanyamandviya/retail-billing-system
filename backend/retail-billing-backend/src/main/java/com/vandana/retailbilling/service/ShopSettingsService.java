package com.vandana.retailbilling.service;

import com.vandana.retailbilling.dto.ShopSettingsDTO;
import com.vandana.retailbilling.dto.UpdateShopSettingsRequest;
import com.vandana.retailbilling.entity.ShopSettings;
import com.vandana.retailbilling.repository.ShopSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopSettingsService {

    private final ShopSettingsRepository shopSettingsRepository;

    public ShopSettingsDTO getShopSettings() {
        // Shop settings table should have only 1 row
        List<ShopSettings> settings = shopSettingsRepository.findAll();

        if (settings.isEmpty()) {
            throw new RuntimeException("Shop settings not configured");
        }

        return ShopSettingsDTO.fromEntity(settings.get(0));
    }

    @Transactional
    public ShopSettingsDTO updateShopSettings(UpdateShopSettingsRequest request) {
        List<ShopSettings> settings = shopSettingsRepository.findAll();

        ShopSettings shopSettings;

        if (settings.isEmpty()) {
            // Create new settings if doesn't exist
            shopSettings = new ShopSettings();
        } else {
            // Update existing settings
            shopSettings = settings.get(0);
        }

        shopSettings.setShopName(request.getShopName());
        shopSettings.setShopLogoPath(request.getShopLogoPath());
        shopSettings.setAddress(request.getAddress());
        shopSettings.setPhone(request.getPhone());
        shopSettings.setEmail(request.getEmail());
        shopSettings.setTaxRate(request.getTaxRate());

        ShopSettings savedSettings = shopSettingsRepository.save(shopSettings);
        return ShopSettingsDTO.fromEntity(savedSettings);
    }

    @Transactional
    public ShopSettingsDTO updateShopLogo(String logoPath) {
        List<ShopSettings> settings = shopSettingsRepository.findAll();

        if (settings.isEmpty()) {
            throw new RuntimeException("Shop settings not configured");
        }

        ShopSettings shopSettings = settings.get(0);
        shopSettings.setShopLogoPath(logoPath);

        ShopSettings savedSettings = shopSettingsRepository.save(shopSettings);
        return ShopSettingsDTO.fromEntity(savedSettings);
    }
}
