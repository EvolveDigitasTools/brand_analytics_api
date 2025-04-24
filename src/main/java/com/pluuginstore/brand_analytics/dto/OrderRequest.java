package com.pluuginstore.brand_analytics.dto;

import com.pluuginstore.brand_analytics.enums.Marketplace;
import com.pluuginstore.brand_analytics.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderRequest {
    private String orderId;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
    private double orderValue;
    private Marketplace marketplace;
    private List<OrderItemRequest> orderItems;
    private OrderAddressRequest address;
}
