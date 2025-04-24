package com.pluuginstore.brand_analytics.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class InventoryUpdateRequest {
    private List<InventoryUpdateDTO> updates;
    private LocalDateTime updateTimestamp;
}

