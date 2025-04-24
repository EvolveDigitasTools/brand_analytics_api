package com.pluuginstore.brand_analytics.amazon;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//@Service
public class InventoryScheduledJob {
//    private final InventoryUpdateService inventoryUpdateService;
//
//    public InventoryScheduledJob(InventoryUpdateService inventoryUpdateService) {
//        this.inventoryUpdateService = inventoryUpdateService;
//    }

    // Run daily at 3 AM (adjust the cron as needed)
//    @Scheduled(cron = "0 0 3 * * ?")
//    public void dailyInventoryUpdate() {
//        try {
//            inventoryUpdateService.updateInventory();
//        } catch (RateLimitException e) {
//            // If a rate limit error occurs, log the error and schedule a retry after 4 hours.
//            // You could implement a retry mechanism here or simply log and wait until the next scheduled run.
//            // Example:
//            System.err.println("Rate limit exceeded. Will retry inventory update in 4 hours.");
//            // Optionally, you could programmatically schedule a one-off retry.
//        } catch (Exception e) {
//            // Handle other exceptions (log, alert, etc.)
//            System.err.println("Error updating inventory: " + e.getMessage());
//        }
//    }
}
