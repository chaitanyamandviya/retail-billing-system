package com.vandana.retailbilling.controller;

import com.vandana.retailbilling.dto.ShopSettingsDTO;
import com.vandana.retailbilling.dto.UpdateShopSettingsRequest;
import com.vandana.retailbilling.service.ShopSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop-settings")
@RequiredArgsConstructor
public class ShopSettingsController {

    private final ShopSettingsService shopSettingsService;

    @GetMapping
    public ResponseEntity<ShopSettingsDTO> getShopSettings() {
        ShopSettingsDTO settings = shopSettingsService.getShopSettings();
        return ResponseEntity.ok(settings);
    }

    @PutMapping
    public ResponseEntity<ShopSettingsDTO> updateShopSettings(
            @Valid @RequestBody UpdateShopSettingsRequest request) {
        ShopSettingsDTO updatedSettings = shopSettingsService.updateShopSettings(request);
        return ResponseEntity.ok(updatedSettings);
    }
}
