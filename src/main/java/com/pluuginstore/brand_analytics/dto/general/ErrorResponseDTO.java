package com.pluuginstore.brand_analytics.dto.general;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Standard error response")
public class ErrorResponseDTO {
    public boolean success;
    public String message;
    public Map<String, String> data;
}
