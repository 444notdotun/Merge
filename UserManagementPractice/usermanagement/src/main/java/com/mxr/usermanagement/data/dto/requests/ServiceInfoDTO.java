package com.mxr.usermanagement.data.dto.requests;

import lombok.Getter;
import lombok.Builder;

@Builder
@Getter
public class ServiceInfoDTO {
    private String serviceName;
    private String version;
    private String environment;
    private int maxUsersPerPage;
    private String status;

    public ServiceInfoDTO(String serviceName, String version, String environment, int maxUsersPerPage, String status){
        this.serviceName = serviceName;
        this.version = version;
        this.environment = environment;
        this.maxUsersPerPage = maxUsersPerPage;
        this.status = status;
    }
    
}
