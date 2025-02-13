package com.pluuginstore.brand_analytics.controller;

import com.pluuginstore.brand_analytics.dto.DailySales;
import com.pluuginstore.brand_analytics.dto.OrderAnalyticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Analytics", description = "APIs to fetch order analytics data")
public class OrderAnalyticsController {

    @Operation(
            summary = "Get order analytics",
            description = "Fetches total sales, total orders, and daily sales data within a given date range. Supports filters for brand, marketplace, and SKU.",
            parameters = {
                    @Parameter(name = "startDate", description = "Start date (YYYY-MM-DD)", required = true, example = "2024-02-01"),
                    @Parameter(name = "endDate", description = "End date (YYYY-MM-DD, defaults to today if not provided)", example = "2024-02-10"),
                    @Parameter(name = "brand", description = "Filter by brand name", example = "AIWA"),
                    @Parameter(name = "marketplace", description = "Filter by marketplace (Amazon, Flipkart)", example = "Amazon"),
                    @Parameter(name = "sku", description = "Filter by specific SKU", example = "SKU1234")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful response",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OrderAnalyticsResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/analytics")
    public OrderAnalyticsResponse getOrderAnalytics(
            @RequestParam String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String marketplace,
            @RequestParam(required = false) String sku) {

        // Placeholder response
        return new OrderAnalyticsResponse(500000, 250, List.of(
                new DailySales("2024-02-10", 100000, 50),
                new DailySales("2024-02-11", 150000, 75)
        ));
    }
}
