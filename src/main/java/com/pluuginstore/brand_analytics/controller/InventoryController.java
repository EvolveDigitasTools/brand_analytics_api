package com.pluuginstore.brand_analytics.controller;

import com.pluuginstore.brand_analytics.amazon.InventoryUpdateService;
import com.pluuginstore.brand_analytics.entity.SKUDetailsEntity;
import com.pluuginstore.brand_analytics.entity.SKUEntity;
import com.pluuginstore.brand_analytics.repository.SKURepository;
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
@CrossOrigin(origins = "http://localhost:5173/")
public class InventoryController {

    @Autowired
    private SKURepository skuRepository;

    private final InventoryUpdateService inventoryUpdateService;

    public InventoryController(InventoryUpdateService inventoryUpdateService) {
        this.inventoryUpdateService = inventoryUpdateService;
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

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getInventory() {
        try {
            // Assuming the repository method fetches SKUs with their details and inventory
            List<SKUEntity> skus = skuRepository.findAllWithDetailsAndInventory();

            System.out.println(skus.toString());

            // Map each SKU to a response DTO-style map
            List<Map<String, Object>> inventoryList = skus.stream().map(sku -> {
                Map<String, Object> skuMap = new HashMap<>();
                SKUDetailsEntity details = sku.getDetails();
                System.out.println(details != null ? details.toString() : "no detail");

                skuMap.put("SKU Code", sku.getSkuCode());
                skuMap.put("Category", details != null ? details.getCategory() : null);
                skuMap.put("Product Title", sku.getName());
                skuMap.put("SAP Code", details != null ? details.getSapCode() : null);
                skuMap.put("HSN", details != null ? details.getHsn() : null);
                skuMap.put("EAN", sku.getEan());
                skuMap.put("Model Number", details != null ? details.getModelNumber() : null);

                // Map inventory if available; assuming SKU has a List<Inventory> getInventory()
                List<Map<String, Object>> invList = new ArrayList<>();
                if (sku.getInventoryRecords() != null) {
                    invList = sku.getInventoryRecords().stream().map(inv -> {
                        Map<String, Object> invMap = new HashMap<>();
                        invMap.put("count", inv.getQuantity());
                        invMap.put("expiry", inv.getExpiryDate());
                        return invMap;
                    }).collect(Collectors.toList());
                }
                skuMap.put("Current Inventory", invList);
                return skuMap;
            }).collect(Collectors.toList());

            Map<String, Object> data = new HashMap<>();
            data.put("inventory", inventoryList);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Inventory retrieved successfully");
            response.put("data", data);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("source", "InventoryController -> getInventory");

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("data", errorData);

            return new ResponseEntity<>(errorResponse, HttpStatus.GATEWAY_TIMEOUT);
        }
    }
}
