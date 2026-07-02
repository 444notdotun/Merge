package com.merge.backend.engagement.service;

import com.merge.backend.assessment.repository.BuildRepository;
import com.merge.backend.curriculum.domain.Concept;
import com.merge.backend.curriculum.repository.ConceptUnlockRepository;
import com.merge.backend.engagement.domain.Session;
import com.merge.backend.engagement.domain.SessionMood;
import com.merge.backend.engagement.domain.SessionType;
import com.merge.backend.engagement.dto.SessionStartRequest;
import com.merge.backend.engagement.dto.SessionStartResponse;
import com.merge.backend.engagement.repository.SessionRepository;
import com.merge.backend.identity.domain.Student;
import com.merge.backend.identity.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class SessionStartService {

    private final StudentRepository studentRepository;
    private final ConceptUnlockRepository conceptUnlockRepository;
    private final BuildRepository buildRepository;
    private final SessionRepository sessionRepository;

    public SessionStartService(StudentRepository studentRepository,
                               ConceptUnlockRepository conceptUnlockRepository,
                               BuildRepository buildRepository,
                               SessionRepository sessionRepository) {
        this.studentRepository = studentRepository;
        this.conceptUnlockRepository = conceptUnlockRepository;
        this.buildRepository = buildRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public SessionStartResponse start(SessionStartRequest req, String studentEmail) {
        SessionMood mood = parseMood(req.mood());

        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));

        Concept concept = conceptUnlockRepository
                .findTopByStudentIdAndConceptStageNameOrderByConceptSequenceOrderDesc(
                        student.getId(), student.getCurrentStage())
                .map(unlock -> unlock.getConcept())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "No concept unlocked for stage " + student.getCurrentStage()));

        SessionType sessionType = buildRepository.existsByStudentIdAndStageNameAndUnlockedTrue(
                student.getId(), student.getCurrentStage())
                ? SessionType.BUILD
                : SessionType.DRILL;

        Session session = new Session();
        session.setId(UUID.randomUUID().toString());
        session.setStudent(student);
        session.setConcept(concept);
        session.setMood(mood);
        session.setSessionType(sessionType);
        session.setStartedAt(Instant.now());
        sessionRepository.save(session);

        return new SessionStartResponse(session.getId(), concept.getId(), sessionType.name());
    }

    private SessionMood parseMood(String raw) {
        if (raw == null) {
            throw new InvalidMoodException("null");
        }
        try {
            return SessionMood.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidMoodException(raw);
        }
    }
}
