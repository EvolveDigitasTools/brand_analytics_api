package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

//import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    // List<OrderEntity> findByOrderDateBetween(LocalDate startDate, LocalDate
    // endDate);
    Optional<OrderEntity> findByOrderId(String orderId);

    @Query("SELECT oi.sku.skuCode AS skuCode, SUM(oi.quantity) AS totalSales " +
            "FROM OrderItemEntity oi " +
            "WHERE oi.order.orderDateTime >= :startDate " +
            "GROUP BY oi.sku.skuCode")
    List<Object[]> findSalesInLast15Days(@Param("startDate") LocalDateTime startDate);
}
