package com.pluuginstore.brand_analytics.amazon;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AmazonOrderService {

    private final AmazonConfig amazonConfig;

    public AmazonOrderService(AmazonConfig amazonConfig) {
        this.amazonConfig = amazonConfig;
    }

    public String buildAmazonOrderUrl(String lastUpdatedAfter, String nextToken) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(amazonConfig.getBaseUrl()+amazonConfig.getOrdersEndpoint())
                .queryParam("MarketplaceIds", amazonConfig.getMarketplaceId())
                .queryParam("CreatedAfter", lastUpdatedAfter)
                .queryParam("OrderStatuses", "Shipped,Unshipped,PartiallyShipped,Canceled");

        if (nextToken != null) {
            builder.queryParam("NextToken", nextToken);
        }

        return builder.toUriString();
    }
}
