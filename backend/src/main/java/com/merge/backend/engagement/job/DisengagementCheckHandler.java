package com.merge.backend.engagement.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merge.backend.engagement.dto.DisengagementCheckPayload;
import com.merge.backend.engagement.service.DisengagementService;
import com.merge.backend.infrastructure.queue.JobHandler;
import com.merge.backend.infrastructure.queue.JobPayload;
import com.merge.backend.infrastructure.queue.JobType;
import org.springframework.stereotype.Component;

@Component
public class DisengagementCheckHandler implements JobHandler {

    private final DisengagementService disengagementService;
    private final ObjectMapper objectMapper;

    public DisengagementCheckHandler(DisengagementService disengagementService, ObjectMapper objectMapper) {
        this.disengagementService = disengagementService;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobType jobType() {
        return JobType.DISENGAGEMENT_CHECK;
    }

    @Override
    public void handle(JobPayload payload) throws Exception {
        DisengagementCheckPayload data = objectMapper.readValue(
                payload.getPayloadJson(), DisengagementCheckPayload.class);
        disengagementService.checkDisengagement(data.getStudentId());
    }
}
