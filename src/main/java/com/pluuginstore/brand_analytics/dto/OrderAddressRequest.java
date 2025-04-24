package com.pluuginstore.brand_analytics.dto;

import com.pluuginstore.brand_analytics.entity.OrderEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class OrderAddressRequest {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
}
