package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.dto.VendorInventoryDTO;
import com.pluuginstore.brand_analytics.entity.InventoryEntity;
import com.pluuginstore.brand_analytics.entity.SKUEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Integer> {
    @Query("select sum(i.quantity) from InventoryEntity i")
    Integer findTotalInventory();

    @Query("SELECT new com.pluuginstore.brand_analytics.dto.VendorInventoryDTO( " +
            "v.vendorCode, v.brandName, COALESCE(SUM(i.quantity), 0)) " +
            "FROM VendorEntity v " +
            "LEFT JOIN v.skus s " +
            "LEFT JOIN s.inventoryRecords i " +
            "GROUP BY v.id, v.brandName")
    List<VendorInventoryDTO> findInventoryByVendor();

    List<InventoryEntity> findBySku(SKUEntity sku);

    // Method to find inventory by a list of SKUs
    // We fetch all inventory for the relevant SKUs and filter/map by expiry date in memory
    // as querying by SKU list AND a list of expiry dates (including null) can be complex/inefficient in JPQL/SQL.
    List<InventoryEntity> findBySkuIn(List<SKUEntity> skus);
}