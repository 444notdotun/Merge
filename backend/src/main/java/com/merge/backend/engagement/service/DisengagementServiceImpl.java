package com.merge.backend.engagement.service;

import com.merge.backend.ai.gateway.GeminiGateway;
import com.merge.backend.assessment.domain.DrillSubmission;
import com.merge.backend.assessment.repository.DrillSubmissionRepository;
import com.merge.backend.engagement.domain.MomentumState;
import com.merge.backend.engagement.domain.WeeklyMomentum;
import com.merge.backend.engagement.repository.WeeklyMomentumRepository;
import com.merge.backend.identity.domain.Student;
import com.merge.backend.identity.repository.StudentRepository;
import com.merge.backend.integration.service.IntercomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DisengagementServiceImpl implements DisengagementService {

    private static final Logger log = LoggerFactory.getLogger(DisengagementServiceImpl.class);

    private final StudentRepository studentRepository;
    private final WeeklyMomentumRepository weeklyMomentumRepository;
    private final DrillSubmissionRepository drillSubmissionRepository;
    private final GeminiGateway geminiGateway;
    private final IntercomService intercomService;

    public DisengagementServiceImpl(StudentRepository studentRepository,
                                     WeeklyMomentumRepository weeklyMomentumRepository,
                                     DrillSubmissionRepository drillSubmissionRepository,
                                     GeminiGateway geminiGateway,
                                     IntercomService intercomService) {
        this.studentRepository = studentRepository;
        this.weeklyMomentumRepository = weeklyMomentumRepository;
        this.drillSubmissionRepository = drillSubmissionRepository;
        this.geminiGateway = geminiGateway;
        this.intercomService = intercomService;
    }

    @Override
    public void checkDisengagement(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        // Get student's latest momentum
        Optional<WeeklyMomentum> latestMomentumOpt = weeklyMomentumRepository
                .findFirstByStudentIdOrderByWeekStartDesc(studentId);

        if (latestMomentumOpt.isEmpty()) {
            log.info("[DisengagementCheck] No weekly momentum record found for student: {}", student.getEmail());
            return;
        }

        WeeklyMomentum latest = latestMomentumOpt.get();
        MomentumState state = latest.getState();

        if (state == MomentumState.BLOCKED || state == MomentumState.OFFLINE) {
            log.info("[DisengagementCheck] Student {} is in disengaged state: {}", student.getEmail(), state);

            // Determine last active concept
            Optional<DrillSubmission> latestSubmissionOpt = drillSubmissionRepository
                    .findFirstByStudentIdOrderBySubmittedAtDesc(studentId);

            String lastActiveConcept = latestSubmissionOpt
                    .map(sub -> sub.getDrill().getConcept().getName())
                    .orElse("Initial Scaffolding Drills");

            // Generate personalized reach-out message via DisengagementCoach (AI-01)
            try {
                String reachOutMessage = geminiGateway.generateDisengagementReachOut(
                        student.getName(),
                        lastActiveConcept,
                        List.of(state.name())
                );

                // Send via Intercom
                intercomService.sendReachOutMessage(student.getEmail(), reachOutMessage);

            } catch (Exception e) {
                log.error("[DisengagementCheck] Error calling DisengagementCoach prompt. Gracefully skipping reach-out for student: {}", student.getEmail(), e);
            }
        } else {
            log.debug("[DisengagementCheck] Student {} is active (state={}). Skipping disengagement reach-out.", student.getEmail(), state);
        }
    }
}
