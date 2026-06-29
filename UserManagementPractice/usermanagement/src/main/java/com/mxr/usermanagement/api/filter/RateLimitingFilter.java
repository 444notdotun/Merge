package com.mxr.usermanagement.api.filter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.mxr.usermanagement.api.model.RateLimitBucket;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;


@Component
@Order(1)
public class RateLimitingFilter implements Filter {
    private ConcurrentHashMap<String, RateLimitBucket> rateLimitBuckets = new ConcurrentHashMap<>();
    private int maxRequests;
    private Duration windowDuration;

    @Autowired
    RateLimitingFilter(@Qualifier("rateLimitMaxRequests") int maxRequests, @Qualifier("rateLimitWindowDuration") Duration windowDuration) {
        this.maxRequests = maxRequests;
        this.windowDuration = windowDuration;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        String clientIpAddress = httpRequest.getRemoteAddr();

        RateLimitBucket rateLimitBucket = rateLimitBuckets.computeIfAbsent(clientIpAddress,
                                            k -> new RateLimitBucket());
                    
        if (rateLimitBucket.isAllowed(maxRequests, windowDuration)) {
            chain.doFilter(request, response);
        }
        else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.setHeader("Retry-After", "60");
            String json = """
                {
                    "status" : 429,
                    "error" : "Too Many Requests",
                    "message": "Rate limit exceeded. Try again in 60 seconds"
                }
                """;
            httpResponse.getWriter().write(json);
        }

        
    }
    
}
