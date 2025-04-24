package com.pluuginstore.brand_analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "skuId", nullable = false)
    private SKUEntity sku;

    @Column(unique = true)
    private String batchId;

    @Column(nullable = false)
    private int quantity;

    @Column
    private LocalDate expiryDate;
}


