package com.pluuginstore.brand_analytics.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class VendorInventoryDTO {
    private String vendorCode;
    private String vendorName;
    private Long inventoryCount;
}