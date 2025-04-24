package com.pluuginstore.brand_analytics.amazon;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "amazon")
@Data
public class AmazonConfig {
    private String baseUrl;
    private String authUrl;
    private String ordersEndpoint;
    private String reportsEndpoint;
    private String reportDocumentEndpoint;
    private String marketplaceId;

    // Sensitive credentials; override these per profile or via environment variables
    private String clientId;
    private String clientSecret;
    private String refreshToken;

    private RateLimits rateLimits = new RateLimits();

    @Data
    public static class RateLimits {
        private GetOrders getOrders = new GetOrders();
        private GetOrder getOrder = new GetOrder();

        @Data
        public static class GetOrders {
            private double ratePerSecond;
            private int burstRequests;
        }

        @Data
        public static class GetOrder {
            private double ratePerSecond;
            private int burstRequests;
        }
    }

    public String getReportUrl() {
        return this.getBaseUrl() + this.getReportsEndpoint();
    }

    public String getReportDocumentUrl() {
        return this.getBaseUrl() + this.getReportDocumentEndpoint();
    }
}
