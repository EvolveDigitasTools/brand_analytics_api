package com.pluuginstore.brand_analytics.dto;

import com.pluuginstore.brand_analytics.enums.Marketplace;
import com.pluuginstore.brand_analytics.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private String orderId;
    private LocalDateTime orderDateTime;
    private double orderValue;
    private Marketplace marketplace;
    private OrderStatus orderStatus;
}
