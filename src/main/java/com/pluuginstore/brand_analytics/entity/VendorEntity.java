package com.pluuginstore.brand_analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vendor",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "vendorCode"),
                @UniqueConstraint(columnNames = "companyName")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String vendorCode;

    @Column(nullable = false)
    private String productCategory;

    @Column(nullable = false, unique = true)
    private String companyName;

    @Column
    private String brandName;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SKUEntity> skus;
}
