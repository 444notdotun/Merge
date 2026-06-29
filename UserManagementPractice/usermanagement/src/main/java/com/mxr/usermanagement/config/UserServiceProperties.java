package com.mxr.usermanagement.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;



@ConfigurationProperties(prefix = "user.service")
@Component
@Data

public class UserServiceProperties {
    private String serviceName;
    private String version;
    private int maxUsersPerPage;
    private String environment;
    private int port;
}
