package com.pluuginstore.brand_analytics.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private SKUEntity sku;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double pricePerUnit;
}