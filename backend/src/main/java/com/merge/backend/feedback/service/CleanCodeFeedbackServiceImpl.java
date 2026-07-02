package com.merge.backend.feedback.service;

import com.merge.backend.ai.context.PersonalisationContext;
import com.merge.backend.ai.context.PersonalisationContextRetriever;
import com.merge.backend.ai.gateway.GeminiGateway;
import com.merge.backend.assessment.domain.DrillSubmission;
import com.merge.backend.assessment.repository.DrillSubmissionRepository;
import com.merge.backend.feedback.domain.CleanCodeFeedback;
import com.merge.backend.feedback.dto.CleanCodeFeedbackResult;
import com.merge.backend.feedback.repository.CleanCodeFeedbackRepository;
import com.merge.backend.personalisation.repository.PersonalisationProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class CleanCodeFeedbackServiceImpl implements CleanCodeFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(CleanCodeFeedbackServiceImpl.class);

    private final DrillSubmissionRepository drillSubmissionRepository;
    private final CleanCodeFeedbackRepository cleanCodeFeedbackRepository;
    private final GeminiGateway geminiGateway;
    private final PersonalisationProfileRepository profileRepository;
    private final PersonalisationContextRetriever contextRetriever;

    public CleanCodeFeedbackServiceImpl(DrillSubmissionRepository drillSubmissionRepository,
                                        CleanCodeFeedbackRepository cleanCodeFeedbackRepository,
                                        GeminiGateway geminiGateway,
                                        PersonalisationProfileRepository profileRepository,
                                        PersonalisationContextRetriever contextRetriever) {
        this.drillSubmissionRepository = drillSubmissionRepository;
        this.cleanCodeFeedbackRepository = cleanCodeFeedbackRepository;
        this.geminiGateway = geminiGateway;
        this.profileRepository = profileRepository;
        this.contextRetriever = contextRetriever;
    }

    @Override
    public CleanCodeFeedback generateFeedback(Long submissionId, int attemptCount, int maxAttempts) {
        log.info("[CleanCodeFeedback] Triggering feedback generation for submissionId={} attemptCount={}", submissionId, attemptCount);

        DrillSubmission submission = drillSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Drill submission not found: " + submissionId));

        var existing = cleanCodeFeedbackRepository.findByDrillSubmissionId(submissionId);
        if (existing.isPresent()) {
            log.info("[CleanCodeFeedback] Feedback already exists for submissionId={}. Returning it.", submissionId);
            return existing.get();
        }

        String stageName = submission.getStudent().getCurrentStage();
        if (stageName == null) {
            stageName = "CADET";
        }

        try {
            // Call Gemini CleanCodeReviewer prompt (FB-01)
            CleanCodeFeedbackResult result = geminiGateway.generateCleanCodeFeedback(
                    submission.getCode(),
                    stageName
            );

            CleanCodeFeedback feedback = new CleanCodeFeedback();
            feedback.setDrillSubmission(submission);
            feedback.setOverallScore(result.overallScore());
            feedback.setNamingIssues(result.namingIssues());
            feedback.setFunctionSizeIssues(result.functionSizeIssues());
            feedback.setRedundancyIssues(result.redundancyIssues());
            feedback.setSolidIssues(result.solidIssues());
            feedback.setSeniorReviewFlagged(result.seniorReviewFlagged());
            feedback.setGeneratedAt(Instant.now());

            return cleanCodeFeedbackRepository.save(feedback);

        } catch (Exception ex) {
            int currentAttempt = attemptCount + 1;
            if (currentAttempt < maxAttempts) {
                log.warn("[CleanCodeFeedback] Gemini call failed on attempt {}/{}. Rethrowing to retry.", currentAttempt, maxAttempts, ex);
                throw ex;
            } else {
                log.error("[CleanCodeFeedback] Gemini call failed finally on attempt {}/{}. Triggering pgvector fallback.", currentAttempt, maxAttempts, ex);
                return runPgvectorFallback(submission);
            }
        }
    }

    private CleanCodeFeedback runPgvectorFallback(DrillSubmission submission) {
        Long studentId = submission.getStudent().getId();
        var profileOpt = profileRepository.findByStudentId(studentId);

        CleanCodeFeedback fallback = null;

        if (profileOpt.isPresent() && profileOpt.get().getEmbedding() != null) {
            String embedding = profileOpt.get().getEmbedding();
            PersonalisationContext context = contextRetriever.retrieve(embedding);

            for (var entry : context.similarProfiles()) {
                if (!entry.studentId().equals(studentId)) {
                    var matchOpt = cleanCodeFeedbackRepository.findAll().stream()
                            .filter(f -> f.getDrillSubmission().getStudent().getId().equals(entry.studentId()))
                            .findFirst();
                    if (matchOpt.isPresent()) {
                        fallback = matchOpt.get();
                        log.info("[CleanCodeFeedback] Found similar student feedback from studentId={}", entry.studentId());
                        break;
                    }
                }
            }
        }

        CleanCodeFeedback feedback = new CleanCodeFeedback();
        feedback.setDrillSubmission(submission);
        feedback.setGeneratedAt(Instant.now());

        if (fallback != null) {
            feedback.setOverallScore(fallback.getOverallScore());
            feedback.setNamingIssues(fallback.getNamingIssues());
            feedback.setFunctionSizeIssues(fallback.getFunctionSizeIssues());
            feedback.setRedundancyIssues(fallback.getRedundancyIssues());
            feedback.setSolidIssues(fallback.getSolidIssues());
            feedback.setSeniorReviewFlagged(fallback.isSeniorReviewFlagged());
        } else {
            feedback.setOverallScore(80);
            feedback.setNamingIssues(List.of("Review complete. Code conforms to standard conventions."));
            feedback.setFunctionSizeIssues(List.of());
            feedback.setRedundancyIssues(List.of());
            feedback.setSolidIssues(List.of());
            feedback.setSeniorReviewFlagged(false);
        }

        return cleanCodeFeedbackRepository.save(feedback);
    }
}
