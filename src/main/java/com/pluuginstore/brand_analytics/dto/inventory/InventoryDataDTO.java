package com.pluuginstore.brand_analytics.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class InventoryDataDTO {
    public List<InventoryItemDTO> inventory;
}
