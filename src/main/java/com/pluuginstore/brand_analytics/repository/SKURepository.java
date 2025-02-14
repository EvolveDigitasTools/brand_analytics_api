package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SKURepository extends JpaRepository<OrderItemEntity, Long> {
}