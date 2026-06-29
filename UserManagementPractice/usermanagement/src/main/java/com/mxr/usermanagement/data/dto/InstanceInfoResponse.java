package com.mxr.usermanagement.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor


public class InstanceInfoResponse {
    private String instanceId;
    private String region;
    private static final String status = "UP";
    private float uptimeSeconds;
    private static final String serviceName = "devarena-user-service";

}
