package com.mxr.usermanagement.controller;

import com.mxr.usermanagement.config.UserServiceProperties;
import com.mxr.usermanagement.data.dto.requests.ServiceInfoDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/service")

public class HealthController {
    private final UserServiceProperties userServiceProperties;
    public HealthController(UserServiceProperties userServiceProperties) {
        this.userServiceProperties = userServiceProperties;
    }
    
    @GetMapping("/info")
    public ServiceInfoDTO info() {
        ServiceInfoDTO response = ServiceInfoDTO.builder()
            .serviceName(userServiceProperties.getServiceName())
            .version(userServiceProperties.getVersion())
            .environment(userServiceProperties.getEnvironment())
            .maxUsersPerPage(userServiceProperties.getMaxUsersPerPage())
            .status("OK")
            .build();
        return response;
        
    }
}
