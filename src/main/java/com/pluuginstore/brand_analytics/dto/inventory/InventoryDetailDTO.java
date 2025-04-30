package com.pluuginstore.brand_analytics.dto.inventory;

import java.time.LocalDate;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InventoryDetailDTO {
    public int count;
    public LocalDate expiry;
}
