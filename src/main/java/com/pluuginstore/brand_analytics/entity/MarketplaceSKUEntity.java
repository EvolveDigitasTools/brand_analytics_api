package com.pluuginstore.brand_analytics.entity;

import com.pluuginstore.brand_analytics.enums.Marketplace;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceSKUEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private SKUEntity sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Marketplace marketplace;

    @Column(nullable = false, unique = true)
    private String skuCode;
}
