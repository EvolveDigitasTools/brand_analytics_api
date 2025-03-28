package com.pluuginstore.brand_analytics.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class InventoryOverviewResponse {
    private Integer totalInventory;
    private List<VendorInventoryDTO> vendorInventory;
}
