package com.pluuginstore.brand_analytics.exception;

import com.pluuginstore.brand_analytics.dto.general.APIResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponseDTO<Object>> handleException(Exception ex) {
        APIResponseDTO<Object> response = new APIResponseDTO<>(
                false,
                "Internal server error: " + ex.getMessage(),
                Map.of("source", "GlobalExceptionHandler")
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
