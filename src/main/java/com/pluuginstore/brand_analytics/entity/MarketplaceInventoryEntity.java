package com.pluuginstore.brand_analytics.entity;

import com.pluuginstore.brand_analytics.enums.Marketplace;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marketplace_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceInventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private SKUEntity sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Marketplace marketplace;

    @Column(nullable = false)
    private int quantity;
}


