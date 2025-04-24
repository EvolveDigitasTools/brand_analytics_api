package com.pluuginstore.brand_analytics.amazon;

import com.pluuginstore.brand_analytics.amazon.response.TokenResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AmazonTokenManager {
    private final AmazonConfig amazonConfig;

    private String accessToken;
    private Instant expiryTime;
    private final ReentrantLock lock = new ReentrantLock();
    private final RestTemplate restTemplate;

    public AmazonTokenManager(AmazonConfig amazonConfig, RestTemplate restTemplate) {
        this.amazonConfig = amazonConfig;
        this.restTemplate = restTemplate;
    }

    /**
     * Returns a valid access token. Refreshes it if necessary.
     */
    public String getAccessToken() {
        if (accessToken == null || isTokenExpired()) {
            refreshToken();
        }
        return accessToken;
    }

    /**
     * Checks if the token is near expiry (less than 5 minutes remaining).
     */
    private boolean isTokenExpired() {
        return expiryTime == null || Instant.now().isAfter(expiryTime.minus(Duration.ofMinutes(5)));
    }

    /**
     * Refreshes the access token by making a POST request to Amazon's auth URL.
     */
    private void refreshToken() {
        lock.lock();
        try {
            // Double-check to avoid redundant refreshes.
            if (accessToken == null || isTokenExpired()) {
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.add("grant_type", "refresh_token");
                formData.add("refresh_token", amazonConfig.getRefreshToken());
                formData.add("client_id", amazonConfig.getClientId());
                formData.add("client_secret", amazonConfig.getClientSecret());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

                TokenResponse tokenResponse = restTemplate.postForObject(amazonConfig.getAuthUrl(), requestEntity, TokenResponse.class);

                if (tokenResponse != null) {
                    this.accessToken = tokenResponse.getAccess_token();
                    // Token expires in "expires_in" seconds from now.
                    this.expiryTime = Instant.now().plusSeconds(tokenResponse.getExpires_in());
                    System.out.println("Token refreshed: " + accessToken);
                } else {
                    throw new RuntimeException("Failed to refresh access token: response is null");
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
