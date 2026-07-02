package com.merge.backend.engagement.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merge.backend.engagement.dto.MomentumCalculationPayload;
import com.merge.backend.engagement.service.MomentumCalculationService;
import com.merge.backend.infrastructure.queue.JobHandler;
import com.merge.backend.infrastructure.queue.JobPayload;
import com.merge.backend.infrastructure.queue.JobType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MomentumCalculationHandler implements JobHandler {

    private final MomentumCalculationService calculationService;
    private final ObjectMapper objectMapper;

    public MomentumCalculationHandler(MomentumCalculationService calculationService,
                                      ObjectMapper objectMapper) {
        this.calculationService = calculationService;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobType jobType() {
        return JobType.MOMENTUM_CALCULATION;
    }

    @Override
    public void handle(JobPayload payload) throws Exception {
        MomentumCalculationPayload data = objectMapper.readValue(
                payload.getPayloadJson(), MomentumCalculationPayload.class);
        LocalDate weekStart = LocalDate.parse(data.getWeekStart());
        calculationService.calculateAll(weekStart);
    }
}
