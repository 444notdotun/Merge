package com.mxr.usermanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "user.service")
public class AppConfig {

    private final UserServiceProperties userServiceProperties;

    public AppConfig(UserServiceProperties userServiceProperties){
        this.userServiceProperties = userServiceProperties;
    }
    
    String serviceName;
    String version;
    
    @Bean
    public String getServiceIdentifier(){
        String serviceIdentifier = serviceName + " " + version;
        return serviceIdentifier;
    }
}