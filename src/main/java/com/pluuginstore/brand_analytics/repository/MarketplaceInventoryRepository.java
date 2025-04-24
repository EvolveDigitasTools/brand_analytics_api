package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.entity.MarketplaceInventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketplaceInventoryRepository extends JpaRepository<MarketplaceInventoryEntity, Long> {
}
