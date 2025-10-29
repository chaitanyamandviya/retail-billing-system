package com.vandana.retailbilling.dto;

import com.vandana.retailbilling.entity.ShopSettings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopSettingsDTO {

    private Long settingId;
    private String shopName;
    private String shopLogoPath;
    private String address;
    private String phone;
    private String email;
    private BigDecimal taxRate;

    public static ShopSettingsDTO fromEntity(ShopSettings settings) {
        return new ShopSettingsDTO(
                settings.getSettingId(),
                settings.getShopName(),
                settings.getShopLogoPath(),
                settings.getAddress(),
                settings.getPhone(),
                settings.getEmail(),
                settings.getTaxRate()
        );
    }
}
