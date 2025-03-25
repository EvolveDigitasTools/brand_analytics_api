package com.pluuginstore.brand_analytics.amazon.response;

import lombok.Data;

@Data
public class InventoryRecord {
    private String sku;
    private String asin;
    private double price;
    private int quantity;
}
