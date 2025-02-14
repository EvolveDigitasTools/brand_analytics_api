package com.pluuginstore.brand_analytics.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "combo_sku_items")
@Getter
@Setter
public class ComboSKUItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "combo_sku_id", nullable = false)
    private SKUEntity comboSku;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private SKUEntity sku;

    @Column(nullable = false)
    private int quantity;
}
