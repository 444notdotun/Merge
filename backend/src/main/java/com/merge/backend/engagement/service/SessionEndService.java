package com.merge.backend.engagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merge.backend.assessment.repository.DrillCompletionRepository;
import com.merge.backend.engagement.domain.Session;
import com.merge.backend.engagement.dto.SessionEndResponse;
import com.merge.backend.engagement.repository.SessionRepository;
import com.merge.backend.identity.repository.StudentRepository;
import com.merge.backend.infrastructure.queue.JobQueueService;
import com.merge.backend.infrastructure.queue.JobType;
import com.merge.backend.personalisation.dto.SessionAnalysisPayload;
import com.merge.backend.progression.repository.XpEntryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class SessionEndService {

    private final SessionRepository sessionRepository;
    private final StudentRepository studentRepository;
    private final DrillCompletionRepository drillCompletionRepository;
    private final XpEntryRepository xpEntryRepository;
    private final JobQueueService jobQueueService;
    private final ObjectMapper objectMapper;

    public SessionEndService(SessionRepository sessionRepository,
                             StudentRepository studentRepository,
                             DrillCompletionRepository drillCompletionRepository,
                             XpEntryRepository xpEntryRepository,
                             JobQueueService jobQueueService,
                             ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.studentRepository = studentRepository;
        this.drillCompletionRepository = drillCompletionRepository;
        this.xpEntryRepository = xpEntryRepository;
        this.jobQueueService = jobQueueService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public SessionEndResponse end(String sessionId, String studentEmail) {
        Long studentId = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"))
                .getId();

        Session session = sessionRepository.findByIdAndStudentId(sessionId, studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        if (session.getEndedAt() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Session already ended");
        }

        Instant endedAt = Instant.now();
        int drillsCompleted = drillCompletionRepository.countPassedInWindow(
                studentId, session.getStartedAt(), endedAt);
        int xpEarned = xpEntryRepository.sumByStudentIdInWindow(
                studentId, session.getStartedAt(), endedAt);

        session.setEndedAt(endedAt);
        session.setDrillsCompleted(drillsCompleted);
        session.setXpEarned(xpEarned);
        sessionRepository.save(session);

        enqueuePersonalisationUpdate(session, endedAt, drillsCompleted);

        return new SessionEndResponse(
                session.getId(),
                endedAt.toEpochMilli() - session.getStartedAt().toEpochMilli(),
                drillsCompleted,
                xpEarned);
    }

    private void enqueuePersonalisationUpdate(Session session, Instant endedAt, int drillsCompleted) {
        SessionAnalysisPayload payload = new SessionAnalysisPayload(
                session.getStudent().getId(),
                session.getId(),
                endedAt.toEpochMilli() - session.getStartedAt().toEpochMilli(),
                0,
                Map.of(),
                Map.of(),
                Map.of(),
                drillsCompleted > 0 ?
                    (endedAt.toEpochMilli() - session.getStartedAt().toEpochMilli()) / drillsCompleted : 0L,
                List.of()
        );
        try {
            jobQueueService.enqueue(JobType.PERSONALISATION_UPDATE, objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialise PERSONALISATION_UPDATE payload", e);
        }
    }
}
