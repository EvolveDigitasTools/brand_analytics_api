package com.pluuginstore.brand_analytics.controller;

import com.pluuginstore.brand_analytics.dto.InventoryOverviewResponse;
import com.pluuginstore.brand_analytics.service.InventoryUpdateService;
import com.pluuginstore.brand_analytics.dto.InventoryUpdateRequest;
import com.pluuginstore.brand_analytics.entity.SKUDetailsEntity;
import com.pluuginstore.brand_analytics.entity.SKUEntity;
import com.pluuginstore.brand_analytics.repository.SKURepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = {"http://localhost:5173/", "https://brand-analytics.globalplugin.com/"})
@Tag(name = "Inventory Management API", description = "APIs for managing and viewing SKU inventory")
public class InventoryController {

    private static final Logger Log = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private SKURepository skuRepository;

    private final InventoryUpdateService inventoryUpdateService;

    public InventoryController(InventoryUpdateService inventoryUpdateService) {
        this.inventoryUpdateService = inventoryUpdateService;
    }

    @GetMapping("/overview")
    public ResponseEntity<InventoryOverviewResponse> getInventoryOverview() {
        InventoryOverviewResponse overview = inventoryUpdateService.getInventoryOverview();
        return ResponseEntity.ok(overview);
    }

    @PostMapping("/sync-amazon")
    public ResponseEntity<String> triggerManualSync() {
        try {
            inventoryUpdateService.updateInventory();
            return ResponseEntity.ok("Inventory update successfully triggered.");
//        } catch (RateLimitException e) {
//            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
//                    .body("Rate limit exceeded, please try after 60 minutes.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating inventory, please try after 60 minutes.");
        }
    }

    /**
     * Retrieves inventory for ALL SKUs.
     * Mapped to GET /api/inventory and /api/inventory/
     */
    @GetMapping({"", "/"})
    @Operation(
            summary = "Get Inventory for All SKUs",
            description = "Retrieves detailed information and current inventory levels (including expiry dates and recent sales) for all SKUs in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All inventory retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "Successful Inventory Response",
                                            value = """
                        {
                            "success": true,
                            "message": "All inventory retrieved successfully",
                            "data": {
                                "inventory": [
                                    {
                                        "SKU Code": "SKU001",
                                        "Category": "Electronics",
                                        "Product Title": "Wireless Mouse",
                                        "SAP Code": "SAP123",
                                        "EAN": "1234567890123",
                                        "salesLast15Days": 75,
                                        "Current Inventory": [
                                            {
                                                "count": 50,
                                                "expiry": null
                                            },
                                            {
                                                "count": 10,
                                                "expiry": "2024-12-31"
                                            }
                                        ]
                                    },
                                    {
                                        "SKU Code": "SKU002",
                                        "Category": "Accessories",
                                        "Product Title": "USB-C Cable",
                                        "SAP Code": "SAP456",
                                        "EAN": "9876543210987",
                                        "salesLast15Days": 150,
                                        "Current Inventory": [
                                            {
                                                "count": 200,
                                                "expiry": null
                                            }
                                        ]
                                    }
                                ]
                            }
                        }
                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class),
                                    examples = @ExampleObject(
                                            name = "Error Response",
                                            value = """
                        {
                            "success": false,
                            "message": "An internal error occurred while retrieving inventory: [Error Details]",
                            "data": {
                                "source": "InventoryController -> getAllInventory"
                            }
                        }
                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<Map<String, Object>> getAllInventory() {
        try {
            List<SKUEntity> skus = skuRepository.findAllWithDetailsAndInventory();
            return buildInventoryResponse(skus, "All inventory retrieved successfully");
        } catch (Exception e) {
            return buildErrorResponse(e, "InventoryController -> getAllInventory");
        }
    }

    /**
     * Retrieves inventory for SKUs belonging to a specific vendor.
     * Mapped to GET /api/inventory/{vendorCode}
     * @param vendorCode The code of the vendor whose inventory is requested.
     */
    @GetMapping("/{vendorCode}")
    public ResponseEntity<Map<String, Object>> getInventoryByVendor(@PathVariable String vendorCode) {
        try {
            // Use the new repository method
            List<SKUEntity> skus = skuRepository.findAllWithDetailsAndInventoryByVendorCode(vendorCode);
            String message = String.format("Inventory for vendor '%s' retrieved successfully", vendorCode);
            return buildInventoryResponse(skus, message);
        } catch (Exception e) {
            String source = String.format("InventoryController -> getInventoryByVendor(vendorCode=%s)", vendorCode);
            return buildErrorResponse(e, source);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateInventory(@RequestBody InventoryUpdateRequest request) {
        try {
            inventoryUpdateService.updateInventory(request.getUpdates(), request.getUpdateTimestamp());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Inventory updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // --- Helper Methods ---

    /**
     * Builds the successful inventory response structure.
     * @param skus List of SKUEntity objects.
     * @param message Success message for the response.
     * @return ResponseEntity containing the structured inventory data.
     */
    private ResponseEntity<Map<String, Object>> buildInventoryResponse(List<SKUEntity> skus, String message) {
        List<Map<String, Object>> inventoryList = mapSkusToResponseFormat(skus);

        Map<String, Object> data = new HashMap<>();
        data.put("inventory", inventoryList);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Maps a list of SKUEntity objects to the desired response map format.
     * @param skus List of SKUEntity objects.
     * @return List of maps, each representing an SKU's inventory details.
     */
    private List<Map<String, Object>> mapSkusToResponseFormat(List<SKUEntity> skus) {
        return skus.stream().map(sku -> {
            Map<String, Object> skuMap = new HashMap<>();
            SKUDetailsEntity details = sku.getDetails(); // Assumes getDetails() exists

            skuMap.put("SKU Code", sku.getSkuCode());
            // Safely access details - check if details is null
            skuMap.put("Category", details != null ? details.getCategory() : null);
            skuMap.put("Product Title", sku.getName());
            skuMap.put("SAP Code", details != null ? details.getSapCode() : null);
            skuMap.put("HSN", details != null ? details.getHsn() : null);
            skuMap.put("EAN", sku.getEan());
            skuMap.put("Model Number", details != null ? details.getModelNumber() : null);
            // Add vendor code if needed/available
            // skuMap.put("Vendor Code", sku.getVendorCode()); // Example

            // Map inventory if available
            List<Map<String, Object>> invList = new ArrayList<>();
            // Check if inventoryRecords is not null before streaming
            if (sku.getInventoryRecords() != null) {
                invList = sku.getInventoryRecords().stream().map(inv -> {
                    Map<String, Object> invMap = new HashMap<>();
                    invMap.put("count", inv.getQuantity());
                    invMap.put("expiry", inv.getExpiryDate()); // Ensure getExpiryDate() returns a suitable format
                    return invMap;
                }).collect(Collectors.toList());
            }
            skuMap.put("Current Inventory", invList);
            return skuMap;
        }).collect(Collectors.toList());
    }

    /**
     * Builds the error response structure.
     * @param e The exception that occurred.
     * @param source A string indicating where the error originated.
     * @return ResponseEntity containing the error details.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception e, String source) {
        // Consider using a proper logger (like SLF4j)
        // log.error("Error retrieving inventory from {}: {}", source, e.getMessage(), e);
        System.err.printf("Error in %s: %s%n", source, e.getMessage()); // Log the error source

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("source", source);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        // Provide a more user-friendly message for non-technical users if needed
        errorResponse.put("message", "An internal error occurred while retrieving inventory: " + e.getMessage());
        errorResponse.put("data", errorData);

        // Consider mapping specific exceptions to different HTTP statuses (e.g., 404 if vendor not found)
        // Using 500 (Internal Server Error) as a general fallback. GATEWAY_TIMEOUT (504) might not be appropriate here.
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
