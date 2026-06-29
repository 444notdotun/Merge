package com.mxr.usermanagement.health;

import org.springframework.boot.health.contributor.HealthIndicator;

import org.springframework.stereotype.Component;

import com.mxr.usermanagement.config.InstanceConfig;

import org.springframework.boot.health.contributor.Health;
import org.jspecify.annotations.Nullable;

@Component
public class ApplicationHealthIndicator implements HealthIndicator {
    private final InstanceConfig instanceConfig;
    public ApplicationHealthIndicator(InstanceConfig instanceConfig){
        this.instanceConfig = instanceConfig;
    }

    @Override
    public @Nullable Health health() {
        long freeMemory = Runtime.getRuntime().freeMemory();

        if (freeMemory < (50 * 1024 * 1024)) {
            return Health.down().withDetail("instanceId", instanceConfig.getInstanceId())
                    .withDetail("region", instanceConfig.getRegion())
                    .withDetail("freeMemory", freeMemory).build();
        }

        return Health.up().withDetail("instanceId", instanceConfig.getInstanceId())
                .withDetail("region", instanceConfig.getRegion())
                .withDetail("freeMemory", freeMemory).build();
    }
}
