package com.pluuginstore.brand_analytics.controller;

import com.pluuginstore.brand_analytics.dto.SKUMapping;
import com.pluuginstore.brand_analytics.dto.UpdateMappingRequest;
import com.pluuginstore.brand_analytics.entity.ComboSKUItemEntity;
import com.pluuginstore.brand_analytics.entity.SKUEntity;
import com.pluuginstore.brand_analytics.repository.ComboSKUItemRepository;
import com.pluuginstore.brand_analytics.repository.SKURepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sku")
@Validated
@CrossOrigin(origins = {"http://localhost:5173/", "https://brand-analytics.globalplugin.com/"})
public class SKUController {

    @Autowired
    private SKURepository skuRepository;

    @Autowired
    private ComboSKUItemRepository comboSKUItemRepository;

    /**
     * Endpoint to update combo SKU mappings.
     *
     * @param request List of mappings to update, each containing mappingSkuId and mappings.
     * @return ResponseEntity<Void> indicating operation status (204 on success).
     */
    @PutMapping("/update-mapping")
    @Transactional
    public ResponseEntity<Void> updateComboSKUMapping(@RequestBody List<UpdateMappingRequest> request) {
        for (UpdateMappingRequest comboMapping : request) {
            SKUEntity comboSku = skuRepository.findBySkuCode(comboMapping.getMappingSkuCode());
            if (comboSku == null) {
                System.out.println(comboMapping.getMappingSkuCode() + " this is combo mapping sku code");
                comboSku = new SKUEntity();
                comboSku.setCombo(true);
                comboSku.setSkuCode(comboMapping.getMappingSkuCode());
            }

            // Save comboSku if new or modified
            comboSku = skuRepository.save(comboSku);

            // Delete old mappings
            comboSKUItemRepository.deleteByComboSku(comboSku);

            // Save new mappings
            for (SKUMapping mapping : comboMapping.getMappings()) {
                SKUEntity sku = skuRepository.findBySkuCode(mapping.getSkuCode());
                if (sku == null) {
                    throw new IllegalArgumentException("SKU with code " + mapping.getSkuCode() + " not found.");
                }

                ComboSKUItemEntity comboSKUItem = new ComboSKUItemEntity();
                comboSKUItem.setComboSku(comboSku);
                comboSKUItem.setSku(sku);
                comboSKUItem.setQuantity(mapping.getQuantity());

                comboSKUItemRepository.save(comboSKUItem);
            }
        }

        return ResponseEntity.noContent().build();
    }
}