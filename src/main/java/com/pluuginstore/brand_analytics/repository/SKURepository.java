package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.entity.SKUEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SKURepository extends JpaRepository<SKUEntity, Integer> {

        @Query("select distinct s from SKUEntity s " +
                        "left join fetch s.details " +
                        "left join fetch s.inventoryRecords " +
                        "where s.isCombo = false")
        List<SKUEntity> findAllSingleWithDetailsAndInventory();

        @Query("select distinct s from SKUEntity s " +
                        "left join fetch s.details " +
                        "where s.isCombo = true")
        List<SKUEntity> findAllComboWithDetails();

        @Query("select distinct s from SKUEntity s " +
                        "left join fetch s.details " +
                        "left join fetch s.inventoryRecords " +
                        "where s.isCombo = false")
        List<SKUEntity> findAllSingleWithDetailsAndInventoryByVendor(@Param("vendorId") Integer vendorId);

        SKUEntity findBySkuCode(String skuCode);

        // Method to find SKUs by a list of codes
        List<SKUEntity> findBySkuCodeIn(List<String> skuCodes);

        @Query("SELECT oi.sku.skuCode AS skuCode, SUM(oi.quantity) AS totalSales " +
                        "FROM OrderItemEntity oi " +
                        "WHERE oi.order.orderDateTime >= :startDate " +
                        "GROUP BY oi.sku.skuCode")
        List<Object[]> findSalesfromStartDate(@Param("startDate") LocalDateTime startDate);
}