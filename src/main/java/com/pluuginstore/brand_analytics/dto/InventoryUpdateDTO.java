package com.pluuginstore.brand_analytics.dto;

import java.time.LocalDate;

public class InventoryUpdateDTO {
    private String skuCode;
    private Integer updatedInventory;
    private LocalDate expiryDate;

    // Getters and setters

    public String getSkuCode() {
        return skuCode;
    }
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
    public Integer getUpdatedInventory() {
        return updatedInventory;
    }
    public void setUpdatedInventory(Integer updatedInventory) {
        this.updatedInventory = updatedInventory;
    }
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}