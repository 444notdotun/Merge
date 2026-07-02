package com.merge.backend.feedback.service;

import com.merge.backend.ai.gateway.GeminiGateway;
import com.merge.backend.assessment.domain.DrillSubmission;
import com.merge.backend.assessment.repository.DrillSubmissionRepository;
import com.merge.backend.feedback.domain.CleanCodeFeedback;
import com.merge.backend.feedback.dto.CleanCodeFeedbackResult;
import com.merge.backend.feedback.repository.CleanCodeFeedbackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class CleanCodeFeedbackServiceImpl implements CleanCodeFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(CleanCodeFeedbackServiceImpl.class);

    private final DrillSubmissionRepository drillSubmissionRepository;
    private final CleanCodeFeedbackRepository cleanCodeFeedbackRepository;
    private final GeminiGateway geminiGateway;

    public CleanCodeFeedbackServiceImpl(DrillSubmissionRepository drillSubmissionRepository,
                                        CleanCodeFeedbackRepository cleanCodeFeedbackRepository,
                                        GeminiGateway geminiGateway) {
        this.drillSubmissionRepository = drillSubmissionRepository;
        this.cleanCodeFeedbackRepository = cleanCodeFeedbackRepository;
        this.geminiGateway = geminiGateway;
    }

    @Override
    public CleanCodeFeedback generateFeedback(Long submissionId) {
        log.info("[CleanCodeFeedback] Triggering feedback generation for submissionId={}", submissionId);

        DrillSubmission submission = drillSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Drill submission not found: " + submissionId));

        // Check if feedback already exists to avoid redundant calls
        var existing = cleanCodeFeedbackRepository.findByDrillSubmissionId(submissionId);
        if (existing.isPresent()) {
            log.info("[CleanCodeFeedback] Feedback already exists for submissionId={}. Returning it.", submissionId);
            return existing.get();
        }

        String stageName = submission.getStudent().getCurrentStage();
        if (stageName == null) {
            stageName = "CADET";
        }

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

        CleanCodeFeedback saved = cleanCodeFeedbackRepository.save(feedback);
        log.info("[CleanCodeFeedback] Structured feedback saved with id={}", saved.getId());
        return saved;
    }
}
