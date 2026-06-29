package com.mxr.usermanagement.health;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import com.mxr.usermanagement.config.InstanceConfig;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    private final DataSource dataSource; 
    private final InstanceConfig instanceConfig;
    public DatabaseHealthIndicator(DataSource dataSource, InstanceConfig instanceConfig) {
        this.dataSource = dataSource;
        this.instanceConfig = instanceConfig;
    }

    @Override
    public @Nullable Health health() {
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            return Health.up().withDetail("database", "reachable")
            .withDetail("instanceId", instanceConfig.getInstanceId())
            .build();
        }catch (Exception e) {
            return Health.down().withDetail("database", "unreachable").withDetail("error", e.getMessage())
            .withDetail("instanceId", instanceConfig.getInstanceId()).build();
        }
        finally{
            if(connection != null){
                try{
                    connection.close();
                }catch (Exception e) {
                    
                }
            }
        }
    }
}
