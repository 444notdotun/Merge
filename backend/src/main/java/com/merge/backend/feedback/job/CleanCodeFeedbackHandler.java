package com.merge.backend.feedback.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merge.backend.feedback.dto.CleanCodeFeedbackPayload;
import com.merge.backend.feedback.service.CleanCodeFeedbackService;
import com.merge.backend.infrastructure.queue.JobHandler;
import com.merge.backend.infrastructure.queue.JobPayload;
import com.merge.backend.infrastructure.queue.JobType;
import org.springframework.stereotype.Component;

@Component
public class CleanCodeFeedbackHandler implements JobHandler {

    private final CleanCodeFeedbackService cleanCodeFeedbackService;
    private final ObjectMapper objectMapper;

    public CleanCodeFeedbackHandler(CleanCodeFeedbackService cleanCodeFeedbackService, ObjectMapper objectMapper) {
        this.cleanCodeFeedbackService = cleanCodeFeedbackService;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobType jobType() {
        return JobType.CLEAN_CODE_FEEDBACK;
    }

    @Override
    public void handle(JobPayload payload) throws Exception {
        CleanCodeFeedbackPayload data = objectMapper.readValue(
                payload.getPayloadJson(), CleanCodeFeedbackPayload.class);
        cleanCodeFeedbackService.generateFeedback(
                data.getSubmissionId(),
                payload.getAttemptCount(),
                JobType.CLEAN_CODE_FEEDBACK.maxAttempts
        );
    }
}
