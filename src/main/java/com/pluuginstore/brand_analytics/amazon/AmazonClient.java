package com.pluuginstore.brand_analytics.amazon;

//import com.pluuginstore.brand_analytics.entity.OrderEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
public class AmazonClient {
//    private final RestTemplate restTemplate;
//    private final RateLimiterService rateLimiter;
//
//    public AmazonClient(RestTemplate restTemplate, RateLimiterService rateLimiter) {
//        this.restTemplate = restTemplate;
//        this.rateLimiter = rateLimiter;
//    }
//
//    public List<OrderEntity> getOrders(String lastUpdatedAfter) {
//        List<OrderEntity> allOrders = new ArrayList<>();
//        String nextToken = null;
//
////        do {
////            try {
////                rateLimiter.waitForSlot();
////                String url = buildAmazonOrderUrl(lastUpdatedAfter, nextToken);
////                AmazonOrderResponse response = restTemplate.getForObject(url, AmazonOrderResponse.class);
//
////                if (response != null && response.getOrders() != null) {
////                    allOrders.addAll(response.getOrders());
////                    nextToken = response.getNextToken();
////                } else {
////                    nextToken = null;
////                }
////            } catch (HttpClientErrorException.TooManyRequests e) {
////                handleRateLimitError();
////            }
////        } while (nextToken != null);
//
//        return allOrders;
//    }
//
//    private void handleRateLimitError() {
//        try {
//            Thread.sleep(60000); // Wait 1 min before retrying
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
}
