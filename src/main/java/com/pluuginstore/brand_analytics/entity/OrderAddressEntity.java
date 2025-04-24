package com.pluuginstore.brand_analytics.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255)
    private String addressLine1;

    @Column(length = 255)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String postalCode;

    @OneToOne(mappedBy = "address", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private OrderEntity order;
}
