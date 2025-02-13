package com.pluuginstore.brand_analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Order Analytics Response")
public record OrderAnalyticsResponse(
        @Schema(description = "Total sales value in the given date range", example = "500000")
        double totalSales,

        @Schema(description = "Total number of orders in the given date range", example = "250")
        int totalOrders,

        @Schema(description = "List of daily sales data")
        List<DailySales> dailySales
) {}