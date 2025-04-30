package com.pluuginstore.brand_analytics.controller;

import com.pluuginstore.brand_analytics.docs.SwaggerExamples;
import com.pluuginstore.brand_analytics.dto.InventoryOverviewResponse;
import com.pluuginstore.brand_analytics.dto.general.APIResponseDTO;
import com.pluuginstore.brand_analytics.dto.inventory.InventoryDataDTO;
import com.pluuginstore.brand_analytics.dto.inventory.InventoryItemDTO;
import com.pluuginstore.brand_analytics.service.InventoryService;
import com.pluuginstore.brand_analytics.dto.InventoryUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = {"http://localhost:5173/", "https://brand-analytics.globalplugin.com/"})
@Tag(name = "Inventory Management API", description = "APIs for managing and viewing SKU inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/overview")
    public ResponseEntity<InventoryOverviewResponse> getInventoryOverview() {
        InventoryOverviewResponse overview = inventoryService.getInventoryOverview();
        return ResponseEntity.ok(overview);
    }

    @PostMapping("/sync-amazon")
    public ResponseEntity<String> triggerManualSync() {
        try {
            // inventoryUpdateService.updateInventory();
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
    
    @GetMapping({"", "/"})
    @Operation(
            summary = "Get Inventory and recent sales for All SKUs",
            description = "Retrieves detailed information and current inventory levels (including expiry dates and recent sales) for all SKUs in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All inventory retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Success",
                                            value = SwaggerExamples.SUCCESS_INVENTORY_RESPONSE
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<APIResponseDTO<InventoryDataDTO>> getAllInventoryAndRecentSales() {
        try {

            List<InventoryItemDTO> inventoryList = inventoryService.getAllInventoryAndRecentSales();
            if (inventoryList == null || inventoryList.isEmpty()) {
                InventoryDataDTO inventoryData = new InventoryDataDTO(Collections.emptyList());
                return ResponseEntity.ok(new APIResponseDTO<>(true, "No inventory found", inventoryData));
            }

            InventoryDataDTO inventoryData = new InventoryDataDTO(inventoryList);
            return ResponseEntity.ok(new APIResponseDTO<>(true, "All inventory retrieved", inventoryData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new APIResponseDTO<>(false, "An error occurred while retrieving inventory: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{vendorCode}")
    @Operation(
            summary = "Get Inventory for All SKUs by Vendor",
            description = "Retrieves detailed information and current inventory levels (including expiry dates and recent sales) for all SKUs of a vendor in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All inventory retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = APIResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Success",
                                            value = SwaggerExamples.SUCCESS_INVENTORY_RESPONSE
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<APIResponseDTO<InventoryDataDTO>> getAllInventoryAndRecentSalesByVendor(@PathVariable String vendorCode) {
        try {
            if (vendorCode == null || vendorCode.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new APIResponseDTO<>(false, "Vendor code must not be null or empty", null));
            }
            List<InventoryItemDTO> inventoryList = inventoryService.getAllInventoryAndRecentSalesByVendor(vendorCode);
            if (inventoryList == null || inventoryList.isEmpty()) {
                InventoryDataDTO inventoryData = new InventoryDataDTO(Collections.emptyList());
                return ResponseEntity.ok(new APIResponseDTO<>(true, "No inventory found", inventoryData));
            }
            
            InventoryDataDTO inventoryData = new InventoryDataDTO(inventoryList);
            return ResponseEntity.ok(new APIResponseDTO<>(true, "All vendor inventory retrieved", inventoryData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponseDTO<>(false, "An error occurred while retrieving vendor inventory: " + e.getMessage(), null));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateInventory(@RequestBody InventoryUpdateRequest request) {
        try {
            // inventoryUpdateService.updateInventory(request.getUpdates(), request.getUpdateTimestamp());
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
}
