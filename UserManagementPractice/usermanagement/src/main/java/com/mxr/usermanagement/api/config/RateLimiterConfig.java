package com.mxr.usermanagement.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration  

public class RateLimiterConfig {

    @Bean
    public int rateLimitMaxRequests() {
        int rateLimitRequests = 100;
        return rateLimitRequests;
    }
    
    @Bean
    public Duration rateLimitWindowDuration() {
        Duration rateLimitWindowDuration = Duration.ofSeconds(60);
        return rateLimitWindowDuration;
    }
    
}
