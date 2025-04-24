package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.entity.SKUEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SKURepository extends JpaRepository<SKUEntity, Integer> {

    @Query("select distinct s from SKUEntity s " +
            "left join fetch s.details " +
            "left join fetch s.inventoryRecords " +
            "where s.isCombo = false")
    List<SKUEntity> findAllWithDetailsAndInventory();

    @Query("SELECT s FROM SKUEntity s " +
            "LEFT JOIN FETCH s.details " +
            "LEFT JOIN FETCH s.inventoryRecords " +
            "LEFT JOIN s.vendor v " + // Join SKUEntity (s) with its VendorEntity (v)
            "WHERE v.vendorCode = :vendorCode") // Filter on the vendorCode within the VendorEntity (v)
    List<SKUEntity> findAllWithDetailsAndInventoryByVendorCode(@Param("vendorCode") String vendorCode);

    SKUEntity findBySkuCode(String skuCode);

    // Method to find SKUs by a list of codes
    List<SKUEntity> findBySkuCodeIn(List<String> skuCodes);
}