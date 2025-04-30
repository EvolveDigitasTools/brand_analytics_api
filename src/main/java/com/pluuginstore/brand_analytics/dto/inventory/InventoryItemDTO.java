package com.pluuginstore.brand_analytics.dto.inventory;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDTO {
    public String skuCode;
    public String category;
    public String productTitle;
    public String sapCode;
    public String ean;
    public int salesLast15Days;
    public List<InventoryDetailDTO> currentInventory;
}
