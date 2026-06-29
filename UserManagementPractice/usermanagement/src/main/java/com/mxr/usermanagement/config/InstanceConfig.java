package com.mxr.usermanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter

@Configuration

public class InstanceConfig {
    @Value("${app.instance.id:instance-1}")
    private String instanceId;

    @Value("${app.instance.region:us-east-1}")
    private String region;
}
