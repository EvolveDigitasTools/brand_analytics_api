package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
}
