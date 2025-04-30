package com.pluuginstore.brand_analytics.service;

import com.pluuginstore.brand_analytics.dto.InventoryOverviewResponse;
import com.pluuginstore.brand_analytics.dto.VendorInventoryDTO;
import com.pluuginstore.brand_analytics.dto.inventory.InventoryItemDTO;
import com.pluuginstore.brand_analytics.entity.ComboSKUItemEntity;
import com.pluuginstore.brand_analytics.entity.SKUEntity;
import com.pluuginstore.brand_analytics.repository.ComboSKUItemRepository;
import com.pluuginstore.brand_analytics.repository.InventoryRepository;
import com.pluuginstore.brand_analytics.repository.SKURepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private SKURepository skuRepository;

    @Autowired
    private ComboSKUItemRepository comboSKUItemRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<InventoryItemDTO> getAllInventoryAndRecentSales() {
        try {
            List<SKUEntity> skus = skuRepository.findAllSingleWithDetailsAndInventory();

            LocalDateTime date15DaysAgo = LocalDateTime.now().minusDays(15);
            List<Object[]> salesData = skuRepository.findSalesfromStartDate(date15DaysAgo);

            Map<String, Integer> salesMap = salesData.stream()
                    .collect(Collectors.toMap(
                            data -> (String) data[0],
                            data -> ((Long) data[1]).intValue()
                    ));

            adjustSalesForCombos(salesMap);

            return skus.stream()
                .map(sku -> new InventoryItemDTO(
                    sku.getSkuCode(),
                    sku.getDetails().getCategory(),
                    sku.getName(),
                    sku.getDetails().getSapCode(),
                    sku.getEan(),
                    salesMap.getOrDefault(sku.getSkuCode(), 0),
                    sku.getCurrentInventory()
            ))
            .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving inventory and sales data: {}", e.getMessage(), e);
            return new ArrayList<InventoryItemDTO>();
            // return buildErrorResponse(e, "InventoryController -> getAllInventory");
        }
    }

    public List<InventoryItemDTO> getAllInventoryAndRecentSalesByVendor(String vendorCode) {
        return new ArrayList<InventoryItemDTO>();
    }

    private void adjustSalesForCombos(Map<String, Integer> salesMap) {
        List<SKUEntity> comboSkus = skuRepository.findAllComboWithDetails();

        List<String> comboSkuCodes = comboSkus.stream()
                .map(SKUEntity::getSkuCode)
                .collect(Collectors.toList());

        List<ComboSKUItemEntity> comboItems = comboSKUItemRepository.findByComboSkuCodes(comboSkuCodes);

        log.info("Retrieved {} combo items", comboItems.size());

        Map<String, List<ComboSKUItemEntity>> comboItemMap = comboItems.stream()
                .collect(Collectors.groupingBy(comboItem -> comboItem.getComboSku().getSkuCode()));

        log.info("Combo items mapped successfully");

        for (SKUEntity comboSku : comboSkus) {
            int comboSales = salesMap.getOrDefault(comboSku.getSkuCode(), 0);

            log.info("Combo SKU: {}, Sales: {}", comboSku.getSkuCode(), comboSales);

            if(comboSales > 0) {
                List<ComboSKUItemEntity> items = comboItemMap.getOrDefault(comboSku.getSkuCode(), new ArrayList<>());
                log.info("Combo SKU {} has {} items", comboSku.getSkuCode(), items.size());
                for (ComboSKUItemEntity comboItem: items) {
                    SKUEntity singleSku = comboItem.getSku();
                    int quantityInCombo = comboItem.getQuantity();
                    int distributedSales = comboSales * quantityInCombo;
                    salesMap.put(singleSku.getSkuCode(), salesMap.getOrDefault(singleSku.getSkuCode(), 0) + distributedSales);
                    log.info("SKU: {}, Distributed Sales: {}", singleSku.getSkuCode(), distributedSales);
                }
            }
            salesMap.remove(comboSku.getSkuCode());
        }
    }

    public InventoryOverviewResponse getInventoryOverview() {
        Integer totalInventory = inventoryRepository.findTotalInventory();
        if (totalInventory == null) {
            totalInventory = 0;
        }
        List<VendorInventoryDTO> vendorInventory = inventoryRepository.findInventoryByVendor();
        return new InventoryOverviewResponse(totalInventory, vendorInventory);
    }
}
