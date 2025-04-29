package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.entity.ComboSKUItemEntity;
import com.pluuginstore.brand_analytics.entity.SKUEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ComboSKUItemRepository extends JpaRepository<ComboSKUItemEntity, Integer> {
    void deleteByComboSku(SKUEntity comboSku);
    
    List<ComboSKUItemEntity> findByComboSku(SKUEntity comboSku);

    @Query("Select c from ComboSKUItemEntity c where c.comboSku.skuCode in :comboSkuCodes")
    List<ComboSKUItemEntity> findByComboSkuCodes(List<String> comboSkuCodes);
}
