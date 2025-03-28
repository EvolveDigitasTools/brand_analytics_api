package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.dto.VendorInventoryDTO;
import com.pluuginstore.brand_analytics.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
    @Query("select sum(i.quantity) from InventoryEntity i")
    Integer findTotalInventory();

    @Query("SELECT new com.pluuginstore.brand_analytics.dto.VendorInventoryDTO( " +
            "v.id, v.companyName, COALESCE(SUM(i.quantity), 0)) " +
            "FROM VendorEntity v " +
            "LEFT JOIN v.skus s " +
            "LEFT JOIN s.inventoryRecords i " +
            "GROUP BY v.id, v.companyName")
    List<VendorInventoryDTO> findInventoryByVendor();
}