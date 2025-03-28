package com.pluuginstore.brand_analytics.dto;

import java.util.List;

public class InventoryUpdateRequest {
    private List<InventoryUpdateDTO> updates;

    // Getter and Setter
    public List<InventoryUpdateDTO> getUpdates() {
        return updates;
    }
    public void setUpdates(List<InventoryUpdateDTO> updates) {
        this.updates = updates;
    }
}

