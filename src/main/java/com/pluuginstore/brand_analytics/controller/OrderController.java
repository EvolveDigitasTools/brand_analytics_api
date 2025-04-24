package com.pluuginstore.brand_analytics.controller;

import com.pluuginstore.brand_analytics.dto.OrderAddressRequest;
import com.pluuginstore.brand_analytics.dto.OrderItemRequest;
import com.pluuginstore.brand_analytics.dto.OrderRequest;
import com.pluuginstore.brand_analytics.entity.*;
import com.pluuginstore.brand_analytics.enums.OrderStatus;
import com.pluuginstore.brand_analytics.repository.InventoryRepository;
import com.pluuginstore.brand_analytics.repository.OrderRepository;
import com.pluuginstore.brand_analytics.repository.SKURepository;
import com.pluuginstore.brand_analytics.repository.ComboSKUItemRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:5173/", "https://brand-analytics.globalplugin.com/"})
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SKURepository skuRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ComboSKUItemRepository comboSKUItemRepository;

    @PostMapping("/update")
    @Transactional
    public ResponseEntity<Map<String, Object>> addOrUpdateOrders(@RequestBody List<OrderRequest> orderRequests) {
        Map<String, Object> response = new HashMap<>();
        List<OrderEntity> ordersToSave = new ArrayList<>();
        Map<SKUEntity, Integer> inventoryAdjustments = new HashMap<>();
        // Use a Set to store unique missing SKU codes
        Set<String> missingSkuCodes = new HashSet<>();
        // Keep track if any order item processing failed due to missing SKU

        try {
            for (OrderRequest orderRequest : orderRequests) {
                OrderEntity orderEntity = orderRepository.findByOrderId(orderRequest.getOrderId())
                        .orElse(new OrderEntity());
                int inventoryAdjustmentFactor = getInventoryAdjustment(orderRequest, orderEntity);

                orderEntity.setOrderId(orderRequest.getOrderId());
                orderEntity.setOrderStatus(orderRequest.getOrderStatus());
                orderEntity.setOrderDateTime(orderRequest.getOrderDate());
                orderEntity.setOrderValue(orderRequest.getOrderValue());
                orderEntity.setMarketplace(orderRequest.getMarketplace());

                if (orderRequest.getAddress() != null) {
                    OrderAddressEntity addressEntity = getOrderAddressEntity(orderRequest);
                    orderEntity.setAddress(addressEntity);
                }

                boolean orderHasMissingSku = false;

                List<OrderItemEntity> orderItems = new ArrayList<>();
                if (orderRequest.getOrderItems() != null) {
                    for (OrderItemRequest orderItemRequest : orderRequest.getOrderItems()) {
                        SKUEntity sku = skuRepository.findBySkuCode(orderItemRequest.getSkuCode());
                        if (sku == null) {
                            // SKU not found: Record it and skip this item
                            missingSkuCodes.add(orderItemRequest.getSkuCode());
                            log.warn("SKU not found: {} for Order ID: {}. Skipping this item.",
                                    orderItemRequest.getSkuCode(), orderRequest.getOrderId());
                            orderHasMissingSku = true; // Mark this order as having issues
                            continue; // Move to the next OrderItemRequest
                        }
                        OrderItemEntity orderItemEntity = new OrderItemEntity();
                        orderItemEntity.setOrder(orderEntity);
                        orderItemEntity.setSku(sku);
                        orderItemEntity.setQuantity(orderItemRequest.getQuantity());
                        orderItems.add(orderItemEntity);

                        // Adjust inventory for the SKU
                        if (inventoryAdjustmentFactor != 0) {
                            LocalDateTime skuInventoryUpdateTime = sku.getInventoryUpdatedAt();
                            LocalDateTime orderDateTime = orderRequest.getOrderDate();

                            // Check if inventory needs adjustment based on timestamp
                            // Adjust if inventoryUpdatedAt is null OR if it's strictly before the orderDate
                            boolean timestampAllowsAdjustment = skuInventoryUpdateTime == null ||
                                    (orderDateTime != null && skuInventoryUpdateTime.isBefore(orderDateTime));

                            if (timestampAllowsAdjustment) {
                                // Calculate the quantity to adjust for this item
                                int quantityToAdjust = orderItemRequest.getQuantity() * inventoryAdjustmentFactor;
                                // Add to the overall adjustments map
                                inventoryAdjustments.put(sku, inventoryAdjustments.getOrDefault(sku, 0) + quantityToAdjust);
                                log.debug("Adding inventory adjustment for SKU: {}, Order: {}, QtyChange: {}, Reason: Timestamp valid ({} vs {})",
                                        sku.getSkuCode(), orderRequest.getOrderId(), quantityToAdjust, skuInventoryUpdateTime, orderDateTime);
                            } else {
                                // Optional: Log why the adjustment was skipped
                                log.info("Skipping inventory adjustment for SKU: {} (Order: {}) because inventoryUpdatedAt ({}) is not before orderDate ({})",
                                        sku.getSkuCode(), orderRequest.getOrderId(), skuInventoryUpdateTime, orderDateTime);
                            }
                        }
                    }
                }

                // Only add the order to be saved if it didn't contain any missing SKUs
                if (!orderHasMissingSku) {
                    orderEntity.setOrderItems(orderItems); // Set the list of valid items
                    ordersToSave.add(orderEntity);
                } else {
                    log.warn("Order ID {} will not be saved due to missing SKUs in its items.", orderRequest.getOrderId());
                }
            }

            if (!missingSkuCodes.isEmpty()) {
                String missingSkusString = String.join(", ", missingSkuCodes);
                // Throwing exception here will trigger rollback due to @Transactional
                throw new RuntimeException("Processing failed. The following SKUs were not found: " + missingSkusString +
                        ". Orders containing these SKUs were not processed.");
            }

            // Validate and update inventory in bulk
            validateAndUpdateInventory(inventoryAdjustments);

            // Save all orders in bulk
            orderRepository.saveAll(ordersToSave);

            response.put("success", true);
            response.put("message", "Orders added/updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding/updating orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private static OrderAddressEntity getOrderAddressEntity(OrderRequest orderRequest) {
        OrderAddressRequest addressRequest = orderRequest.getAddress();
        OrderAddressEntity addressEntity = new OrderAddressEntity();
        addressEntity.setAddressLine1(addressRequest.getAddressLine1());
        addressEntity.setAddressLine2(addressRequest.getAddressLine2());
        addressEntity.setCity(addressRequest.getCity());
        addressEntity.setState(addressRequest.getState());
        addressEntity.setPostalCode(addressRequest.getPostalCode());
        return addressEntity;
    }

    private static int getInventoryAdjustment(OrderRequest orderRequest, OrderEntity orderEntity) {
        int inventoryAdjustment = 0;
        if (orderEntity.getOrderId() == null) {
            if (orderRequest.getOrderStatus().equals(OrderStatus.SHIPPED) ||
                    orderRequest.getOrderStatus().equals(OrderStatus.DELIVERED) ||
                    orderRequest.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                inventoryAdjustment = 1;
            }
        } else {
            // Existing order: handle status changes
            OrderStatus previousStatus = orderEntity.getOrderStatus();
            OrderStatus newStatus = orderRequest.getOrderStatus();

            if (previousStatus.equals(OrderStatus.SHIPPED) && newStatus.equals(OrderStatus.CANCELLED)) {
                // SHIPPED to CANCELLED: add inventory back
                inventoryAdjustment = -1;
            }
            // Add more conditions here for other status transitions if needed
        }
        return inventoryAdjustment;
    }

    private void validateAndUpdateInventory(Map<SKUEntity, Integer> inventoryAdjustments) {
        for (Map.Entry<SKUEntity, Integer> entry : inventoryAdjustments.entrySet()) {
            SKUEntity sku = entry.getKey();
            int quantityToReduce = entry.getValue();

            System.out.println("Inventory Update: " + sku.getSkuCode() + " " + quantityToReduce);

            if (!sku.isCombo()) {
                // Handle single SKU
                updateSingleSKUInventory(sku, quantityToReduce);
            } else {
                // Handle combo SKU
                List<ComboSKUItemEntity> comboItems = comboSKUItemRepository.findByComboSku(sku);
                if (comboItems.isEmpty()) {
                    throw new RuntimeException("No mappings found for combo SKU: " + sku.getSkuCode());
                }

                for (ComboSKUItemEntity comboItem : comboItems) {
                    SKUEntity singleSku = comboItem.getSku();
                    int totalQuantityToReduce = comboItem.getQuantity() * quantityToReduce;
                    updateSingleSKUInventory(singleSku, totalQuantityToReduce);
                }
            }
        }
    }

    private void updateSingleSKUInventory(SKUEntity sku, int quantityToReduce) {
        List<InventoryEntity> inventoryList = inventoryRepository.findBySku(sku);
        if (inventoryList.isEmpty()) {
            log.warn("No inventory found for SKU: " + sku.getSkuCode());
            return;
        }

        inventoryList.sort((inv1, inv2) -> {
            if (inv1.getExpiryDate() == null && inv2.getExpiryDate() != null) {
                return -1; // No expiry comes first
            } else if (inv1.getExpiryDate() != null && inv2.getExpiryDate() == null) {
                return 1; // Expiry comes later
            } else if (inv1.getExpiryDate() != null) {
                return inv1.getExpiryDate().compareTo(inv2.getExpiryDate()); // Earliest expiry first
            }
            return 0; // Both have no expiry
        });

        List<InventoryEntity> toDelete = new ArrayList<>();

        for (InventoryEntity inventory : inventoryList) {
            if (quantityToReduce <= 0) {
                break;
            }

            if (inventory.getQuantity() >= quantityToReduce) {
                inventory.setQuantity(inventory.getQuantity() - quantityToReduce);
                quantityToReduce = 0;
            } else {
                quantityToReduce -= inventory.getQuantity();
                inventory.setQuantity(0);
            }

            if (inventory.getQuantity() == 0) {
                toDelete.add(inventory);
            }
        }

        if (quantityToReduce > 0) {
            log.warn("Insufficient inventory for SKU: " + sku.getSkuCode());
        }

        // Save updated inventory in bulk
        inventoryRepository.saveAll(inventoryList);

        if (!toDelete.isEmpty()) {
            inventoryRepository.deleteAll(toDelete);
        }
    }
}
