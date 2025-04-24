package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.entity.SKUEntity;

public interface SkuLookupRepositoryCustom {
    SKUEntity findBySkuCode(String skuCode);
}
