package com.pluuginstore.brand_analytics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sku_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SKUDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String category;

    @Column(name = "subCategory")
    private String subCategory;

    @Column(name = "sapCode")
    private String sapCode;

    @Column
    private String hsn;

    @Column(name = "modelNumber")
    private String modelNumber;

    @Column
    private Float mrp;

    @Column
    private Float gst;

    @Column(name = "createdBy", nullable = false)
    private String createdBy;

    @Column(name = "isVerified")
    private Boolean isVerified = false;

    // Establish the one-to-one relationship with SKU using a foreign key column skuId
    @OneToOne
    @JoinColumn(name = "skuId")
    private SKUEntity sku;
}
