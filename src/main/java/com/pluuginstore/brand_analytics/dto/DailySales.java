package com.pluuginstore.brand_analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Daily Sales Data")
public record DailySales(
        @Schema(description = "Date of sales", example = "2024-02-10")
        String date,

        @Schema(description = "Total sales for the day", example = "100000")
        double sales,

        @Schema(description = "Total number of orders for the day", example = "50")
        int orders
) {}