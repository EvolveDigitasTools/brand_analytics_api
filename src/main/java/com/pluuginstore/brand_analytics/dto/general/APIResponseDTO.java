package com.pluuginstore.brand_analytics.dto.general;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class APIResponseDTO<T> {
    public boolean success;
    public String message;
    public T data;
}
