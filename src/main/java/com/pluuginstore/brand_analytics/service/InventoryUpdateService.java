package com.pluuginstore.brand_analytics.service;

import com.pluuginstore.brand_analytics.amazon.AmazonConfig;
import com.pluuginstore.brand_analytics.amazon.AmazonTokenManager;
import com.pluuginstore.brand_analytics.amazon.response.*;
import com.pluuginstore.brand_analytics.dto.InventoryOverviewResponse;
import com.pluuginstore.brand_analytics.dto.InventoryUpdateDTO;
import com.pluuginstore.brand_analytics.dto.VendorInventoryDTO;
import com.pluuginstore.brand_analytics.entity.InventoryEntity;
import com.pluuginstore.brand_analytics.entity.SKUEntity;
import com.pluuginstore.brand_analytics.repository.InventoryRepository;
import com.pluuginstore.brand_analytics.repository.SKURepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryUpdateService {

    private static final Logger log = LoggerFactory.getLogger(InventoryUpdateService.class);

    @Autowired
    private AmazonTokenManager tokenManager;

    @Autowired
    private AmazonConfig amazonConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SKURepository skuRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    public InventoryOverviewResponse getInventoryOverview() {
        Integer totalInventory = inventoryRepository.findTotalInventory();
        if (totalInventory == null) {
            totalInventory = 0;
        }
        List<VendorInventoryDTO> vendorInventory = inventoryRepository.findInventoryByVendor();
        return new InventoryOverviewResponse(totalInventory, vendorInventory);
    }

    // Inject any necessary dependencies such as API clients, repositories, etc.
    public void updateInventory() throws Exception {
        String reportId = createReport();
        if (reportId == null) {
            throw new Exception("Failed to create report.");
        }

        String reportDocumentId = pollForReportCompletion(reportId);
        if (reportDocumentId == null) {
            throw new Exception("Report did not complete successfully.");
        }

        String downloadUrl = getReportDocumentUrl(reportDocumentId);
        if (downloadUrl == null) {
            throw new Exception("Failed to get report document URL.");
        }

//        List<InventoryRecord> inventoryRecords = downloadAndParseReport(downloadUrl);

        // 5. Update Inventory in the Database (Pseudo-code)
        // inventoryRecords.forEach(record -> inventoryRepository.save(record));

        System.out.println("Inventory update complete. Total records processed: " + downloadUrl);
        // 1. Call createReport API with required body to get reportId.
        // 2. Use reportId to call getReport and fetch reportDocumentId.
        // 3. Use reportDocumentId to get the download URL of the report document.
        // 4. Download the Tab-delimited file from the URL.
        // 5. Parse the file and update inventory records in the database.
        // 6. Handle API rate limit errors and throw exceptions accordingly.
        // For example, if an API call fails due to rate limits, throw a custom RateLimitException.
    }

    private String createReport() {
        String token = tokenManager.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-amz-access-token", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{"
                + "\"marketplaceIds\": [\"" + amazonConfig.getMarketplaceId() + "\"],"
                + "\"reportType\": \"GET_FLAT_FILE_OPEN_LISTINGS_DATA\","
                + "\"reportOptions\": {\"custom\": \"true\"}"
                + "}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<CreateReportResponse> response = restTemplate.exchange(
                amazonConfig.getReportUrl(), HttpMethod.POST, requestEntity, CreateReportResponse.class);

        if (response.getStatusCode() == HttpStatus.ACCEPTED || response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().getReportId();
        }
        return null;
    }

    private String pollForReportCompletion(String reportId) throws InterruptedException {
        // Poll every minute for up to 15 minutes
        int maxAttempts = 15;
        for (int i = 0; i < maxAttempts; i++) {
            String token = tokenManager.getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-amz-access-token", token);

            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<GetReportResponse> response = restTemplate.exchange(
                    amazonConfig.getReportUrl() + '/' + reportId, HttpMethod.GET, requestEntity, GetReportResponse.class);

            GetReportResponse reportResponse = response.getBody();
            if (reportResponse != null && reportResponse.isReportReady()) {
                return reportResponse.getReportDocumentId();
            }
            // Wait for 1 minute before retrying
            Thread.sleep(Duration.ofMinutes(1).toMillis());
        }
        return null;
    }

    private String getReportDocumentUrl(String reportDocumentId) {
        String token = tokenManager.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-amz-access-token", token);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<GetReportDocumentResponse> response = restTemplate.exchange(
                amazonConfig.getReportDocumentUrl() + '/' + reportDocumentId, HttpMethod.GET, requestEntity, GetReportDocumentResponse.class);

        GetReportDocumentResponse docResponse = response.getBody();
        if (docResponse != null) {
            return docResponse.getUrl();
        }
        return null;
    }

    private List<InventoryRecord> downloadAndParseReport(String downloadUrl) throws Exception {
        List<InventoryRecord> records = new ArrayList<>();
        URL url = new URL(downloadUrl);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String headerLine = reader.readLine(); // Read header
            if (headerLine == null) {
                throw new Exception("Empty report file");
            }
            String[] headers = headerLine.split("\t");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split("\t");
                InventoryRecord record = mapValuesToInventoryRecord(headers, values);
                records.add(record);
            }
        }
        return records;
    }

    private InventoryRecord mapValuesToInventoryRecord(String[] headers, String[] values) {
        InventoryRecord record = new InventoryRecord();
        // Map the columns from the file to your InventoryRecord model.
        // Example:
        record.setSku(values[0]);
        record.setAsin(values[1]);
        record.setPrice(Double.parseDouble(values[2]));
        if (values.length > 3 && !values[3].isEmpty()) {
            record.setQuantity(Integer.parseInt(values[3]));
        }
        // Map other fields as necessary...
        return record;
    }

//    private void updateMarketplaceInventory(List<InventoryRecord> inventoryRecords) {
//        // Assume that the inventory report is for AMAZON marketplace.
//        Marketplace marketplace = Marketplace.AMAZON;
//
//        for (InventoryRecord record : inventoryRecords) {
//            // Lookup SKUEntity by its skuCode from the parsed record.
//            SKUEntity skuEntity = skuRepository.findBySkuCode(record.getSku());
//            if (skuEntity == null) {
//                // Optionally log and skip if SKU doesn't exist.
//                System.err.println("SKU not found for code: " + record.getSku());
//                continue;
//            }
//            // Check if a MarketplaceInventoryEntity exists for this SKU and marketplace.
//            MarketplaceInventoryEntity inventoryEntity =
//                    marketplaceInventoryRepository.findBySkuAndMarketplace(skuEntity, marketplace);
//
//            if (inventoryEntity == null) {
//                // Create a new inventory record.
//                inventoryEntity = new MarketplaceInventoryEntity();
//                inventoryEntity.setSku(skuEntity);
//                inventoryEntity.setMarketplace(marketplace);
//                inventoryEntity.setQuantity(record.getQuantity());
//            } else {
//                // Update the existing record's quantity.
//                inventoryEntity.setQuantity(record.getQuantity());
//            }
//            // Save or update the record.
//            marketplaceInventoryRepository.save(inventoryEntity);
//        }
//    }

    /**
     * Updates inventory based on a list of DTOs, setting the update timestamp on affected SKUs.
     * Optimized for bulk operations.
     *
     * @param updates         List of inventory updates.
     * @param updateTimestamp The timestamp to set on updated SKUs.
     */
    @Transactional
    public void updateInventory(List<InventoryUpdateDTO> updates, LocalDateTime updateTimestamp) {
        if (CollectionUtils.isEmpty(updates)) {
            log.info("No inventory updates provided.");
            return;
        }

        // 1. Extract SKU codes and fetch SKUs in bulk
        List<String> skuCodes = updates.stream()
                .map(InventoryUpdateDTO::getSkuCode)
                .distinct()
                .toList();

        List<SKUEntity> fetchedSkus = skuRepository.findBySkuCodeIn(skuCodes);
        Map<String, SKUEntity> skuMap = fetchedSkus.stream()
                .collect(Collectors.toMap(SKUEntity::getSkuCode, sku -> sku));

        // 2. Fetch existing inventory records for these SKUs in bulk
        List<InventoryEntity> existingInventories = inventoryRepository.findBySkuIn(fetchedSkus);

        // 3. Create a map for quick lookup: Map<SkuId, Map<ExpiryDate (can be null), InventoryEntity>>
        Map<Integer, Map<LocalDate, InventoryEntity>> inventoryMap = new HashMap<>();
        for (InventoryEntity inv : existingInventories) {
            inventoryMap
                    .computeIfAbsent(inv.getSku().getId(), k -> new HashMap<>())
                    .put(inv.getExpiryDate(), inv);
        }

        // 4. Prepare lists for bulk saving
        List<InventoryEntity> inventoryToSave = new ArrayList<>();
        List<InventoryEntity> inventoryToDelete = new ArrayList<>();
        Set<SKUEntity> skusToUpdateTimestamp = new HashSet<>();

        // 5. Process updates
        for (InventoryUpdateDTO update : updates) {
            SKUEntity sku = skuMap.get(update.getSkuCode());
            if (sku == null) {
                // Option 1: Throw exception for bad data
                throw new RuntimeException("SKU not found during bulk update: " + update.getSkuCode());
                // Option 2: Log and skip
                // log.warn("SKU not found, skipping inventory update for: {}", update.getSkuCode());
                // continue;
            }

            LocalDate expiryDate = update.getExpiryDate(); // Can be null
            Integer skuId = sku.getId();
            int newQuantity = update.getUpdatedInventory();

            // Find existing inventory or create a new one
            InventoryEntity inventoryEntity = inventoryMap
                    .getOrDefault(skuId, Collections.emptyMap())
                    .get(expiryDate);

            if (inventoryEntity != null) {
                // Update existing inventory record
                // Only add to save list if quantity actually changes
                if (inventoryEntity.getQuantity() != update.getUpdatedInventory()) {
                    if(newQuantity == 0) {
                        if (!inventoryToDelete.contains(inventoryEntity)) {
                            inventoryToDelete.add(inventoryEntity);
                        }
                        inventoryToSave.remove(inventoryEntity);
                        inventoryMap.getOrDefault(skuId, Collections.emptyMap()).remove(expiryDate);
                    } else {
                        inventoryEntity.setQuantity(newQuantity);
                        if (!inventoryToSave.contains(inventoryEntity)) {
                            inventoryToSave.add(inventoryEntity);
                        }
                        inventoryToDelete.remove(inventoryEntity);
                    }
                }
                // Ensure it's correctly mapped back if it was newly created in this transaction run
                // (though less likely with this bulk fetch approach)
                inventoryMap.computeIfAbsent(skuId, k -> new HashMap<>()).put(expiryDate, inventoryEntity);

            } else if(newQuantity > 0){
                // Create new inventory record
                inventoryEntity = new InventoryEntity();
                inventoryEntity.setSku(sku);
                inventoryEntity.setQuantity(update.getUpdatedInventory());
                inventoryEntity.setExpiryDate(expiryDate);
                // Add the new entity to the save list
                inventoryToSave.add(inventoryEntity);
                // Add the new entity to our lookup map for potential subsequent updates in the same batch
                inventoryMap.computeIfAbsent(skuId, k -> new HashMap<>()).put(expiryDate, inventoryEntity);
            }

            // Mark SKU for timestamp update
            sku.setInventoryUpdatedAt(updateTimestamp);
            skusToUpdateTimestamp.add(sku);
        }

        // 6. Perform bulk database operations
        if (!inventoryToDelete.isEmpty()) {
            log.info("Deleting {} inventory records.", inventoryToDelete.size());
            inventoryRepository.deleteAllInBatch(inventoryToDelete); // Use deleteAllInBatch for efficiency
        }

        // Filter out any entities that might be marked for both save and delete (shouldn't happen with current logic, but safe)
        List<InventoryEntity> finalInventoryToSave = inventoryToSave.stream()
                .filter(inv -> !inventoryToDelete.contains(inv))
                .collect(Collectors.toList());

        if (!finalInventoryToSave.isEmpty()) {
            log.info("Saving {} inventory records.", finalInventoryToSave.size());
            inventoryRepository.saveAll(finalInventoryToSave);
        } else if (inventoryToDelete.isEmpty()) { // Only log this if nothing was saved OR deleted
            log.info("No inventory quantities needed updating or deleting.");
        }

        if (!skusToUpdateTimestamp.isEmpty()) {
            log.info("Updating inventoryUpdatedAt timestamp for {} SKUs.", skusToUpdateTimestamp.size());
            // Note: saveAll will perform UPDATE statements for existing entities
            skuRepository.saveAll(skusToUpdateTimestamp);
        }

        log.info("Inventory update process completed successfully for timestamp: {}", updateTimestamp);
    }
}
