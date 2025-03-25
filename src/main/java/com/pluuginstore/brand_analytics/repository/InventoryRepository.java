package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {
}