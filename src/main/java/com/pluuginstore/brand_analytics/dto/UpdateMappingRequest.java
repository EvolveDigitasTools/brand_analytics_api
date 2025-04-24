package com.pluuginstore.brand_analytics.dto;

import com.pluuginstore.brand_analytics.controller.SKUController;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateMappingRequest {
    private String mappingSkuCode;
    private List<SKUMapping> mappings;
}
