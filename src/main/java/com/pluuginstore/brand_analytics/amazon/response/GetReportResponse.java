package com.pluuginstore.brand_analytics.amazon.response;

import lombok.Data;

@Data
public class GetReportResponse {
    private String processingStatus;
    private String reportDocumentId;

    /**
     * Helper method to check if the report processing is complete.
     */
    public boolean isReportReady() {
        return "DONE".equalsIgnoreCase(processingStatus);
    }
}
