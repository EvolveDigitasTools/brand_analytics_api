package com.pluuginstore.brand_analytics.amazon;

import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {
    private final Semaphore semaphore = new Semaphore(1);

    public void waitForSlot() throws InterruptedException {
        semaphore.acquire();
        TimeUnit.MILLISECONDS.sleep((long)(1000 / 0.0167)); // Adjust for API rate
        semaphore.release();
    }
}
