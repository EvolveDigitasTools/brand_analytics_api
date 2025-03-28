package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.entity.SKUEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SKURepository extends JpaRepository<SKUEntity, Integer> {

    @Query("select distinct s from SKUEntity s " +
            "left join fetch s.details " +
            "left join fetch s.inventoryRecords")
    List<SKUEntity> findAllWithDetailsAndInventory();

    SKUEntity findBySkuCode(String skuCode);
}