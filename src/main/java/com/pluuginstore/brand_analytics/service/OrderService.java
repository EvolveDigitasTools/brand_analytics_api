package com.pluuginstore.brand_analytics.service;

import com.pluuginstore.brand_analytics.entity.OrderEntity;
import com.pluuginstore.brand_analytics.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderEntity> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate == null) {
            endDate = LocalDate.now(); // Default to today's date if endDate is not provided.
        }
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }
}