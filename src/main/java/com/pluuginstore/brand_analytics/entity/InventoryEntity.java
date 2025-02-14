package com.pluuginstore.brand_analytics.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "inventory")
@Getter
@Setter
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private SKUEntity sku;

    @Column(unique = true)
    private String batchId;

    @Column(nullable = false)
    private int quantity;

    private LocalDate expiryDate;
}


