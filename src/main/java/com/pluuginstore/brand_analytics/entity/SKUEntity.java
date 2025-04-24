package com.pluuginstore.brand_analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sku")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SKUEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "isCombo")
    private boolean isCombo;

    @OneToMany(mappedBy = "sku", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryEntity> inventoryRecords = new ArrayList<>();

    @Column(name = "skuCode", nullable = false, unique = true)
    private String skuCode;

    @Column
    private String name;

    @Column
    private String ean;

    @Column
    private LocalDateTime inventoryUpdatedAt;

    // One-to-one relation with SKUDetails; SKUDetails owns the relationship (skuId FK)
    @OneToOne(mappedBy = "sku", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SKUDetailsEntity details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendorId")
    private VendorEntity vendor;

}
