package com.merge.backend.engagement.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merge.backend.engagement.dto.MomentumCalculationPayload;
import com.merge.backend.infrastructure.queue.JobQueueService;
import com.merge.backend.infrastructure.queue.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Fires every Monday at 00:00 UTC and enqueues one MOMENTUM_CALCULATION job for the
 * 7-day window that just ended (previous Monday → this Monday).
 */
@Component
public class MomentumCalculationTrigger {

    private static final Logger log = LoggerFactory.getLogger(MomentumCalculationTrigger.class);

    private final JobQueueService jobQueueService;
    private final ObjectMapper objectMapper;

    public MomentumCalculationTrigger(JobQueueService jobQueueService,
                                      ObjectMapper objectMapper) {
        this.jobQueueService = jobQueueService;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "0 0 0 * * MON", zone = "UTC")
    public void enqueue() {
        LocalDate weekStart = LocalDate.now(ZoneOffset.UTC).minusDays(7);
        MomentumCalculationPayload payload = new MomentumCalculationPayload(weekStart.toString());
        try {
            jobQueueService.enqueue(JobType.MOMENTUM_CALCULATION,
                    objectMapper.writeValueAsString(payload));
            log.info("[MomentumCalculationTrigger] Enqueued MOMENTUM_CALCULATION for week_start={}", weekStart);
        } catch (JsonProcessingException e) {
            log.error("[MomentumCalculationTrigger] Failed to enqueue job for week_start={}: {}",
                    weekStart, e.getMessage(), e);
        }
    }
}
