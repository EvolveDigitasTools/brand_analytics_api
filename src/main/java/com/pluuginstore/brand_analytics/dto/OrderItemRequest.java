package com.pluuginstore.brand_analytics.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private String skuCode;
    private int quantity;
    private double price;
}
