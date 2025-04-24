package com.pluuginstore.brand_analytics.controller;

import com.pluuginstore.brand_analytics.dto.VendorDTO;
import com.pluuginstore.brand_analytics.repository.VendorRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor")
@CrossOrigin(origins = {"http://localhost:5173/", "https://brand-analytics.globalplugin.com/"})
@AllArgsConstructor
public class VendorController {
    private final VendorRepository vendorRepository;

    @GetMapping("/all")
    public ResponseEntity<List<VendorDTO>> getAllVendors() {
        return ResponseEntity.ok(vendorRepository.findAllVendors());
    }
}
