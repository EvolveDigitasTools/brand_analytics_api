package com.pluuginstore.brand_analytics.repository;

import com.pluuginstore.brand_analytics.dto.VendorDTO;
import com.pluuginstore.brand_analytics.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<OrderItemEntity, Integer> {
    @Query("SELECT new com.pluuginstore.brand_analytics.dto.VendorDTO(v.brandName, v.vendorCode) FROM VendorEntity v")
    List<VendorDTO> findAllVendors();
}
