package com.pluuginstore.brand_analytics.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class InventoryUpdateDTO {
    private String skuCode;
    private Integer updatedInventory;
    private LocalDate expiryDate;
}