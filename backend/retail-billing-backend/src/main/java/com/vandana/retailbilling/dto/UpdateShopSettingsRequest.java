package com.vandana.retailbilling.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShopSettingsRequest {

    @NotBlank(message = "Shop name is required")
    private String shopName;

    private String shopLogoPath;

    private String address;

    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate cannot be negative")
    private BigDecimal taxRate;
}
