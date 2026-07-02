package com.merge.backend.assessment.service;

import com.merge.backend.ai.gateway.GeminiGateway;
import com.merge.backend.assessment.domain.ComprehensionCheck;
import com.merge.backend.assessment.domain.ComprehensionCheckStatus;
import com.merge.backend.assessment.domain.DrillSubmission;
import com.merge.backend.assessment.dto.ComprehensionQuestion;
import com.merge.backend.assessment.dto.ComprehensionQuestionsRequest;
import com.merge.backend.assessment.repository.ComprehensionCheckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ComprehensionCheckService {

    private static final int SECONDS_PER_QUESTION = 10;

    private final GeminiGateway geminiGateway;
    private final ComprehensionCheckRepository comprehensionCheckRepository;

    public ComprehensionCheckService(GeminiGateway geminiGateway,
                                     ComprehensionCheckRepository comprehensionCheckRepository) {
        this.geminiGateway = geminiGateway;
        this.comprehensionCheckRepository = comprehensionCheckRepository;
    }

    /**
     * Triggered immediately when Judge0 returns status 3 (Accepted) for a drill submission.
     *
     * Calls AI-04 to generate questions grounded in the student's specific code —
     * their actual variable names, function choices, and architectural decisions.
     * Questions are unanswerable without understanding this exact implementation.
     *
     * Each invocation produces fresh questions, so re-attempts always get different questions.
     *
     * serverDeadline = triggeredAt + (numQuestions × 10 seconds)
     */
    @Transactional
    public ComprehensionCheck triggerFor(DrillSubmission submission) {
        ComprehensionQuestionsRequest aiRequest = new ComprehensionQuestionsRequest(
                submission.getCode(),
                submission.getTestSuite(),
                submission.getDrill().getConcept().getName(),
                submission.getDrill().getProblemStatement()
        );

        List<ComprehensionQuestion> generated = geminiGateway.generateComprehensionQuestions(aiRequest);
        List<String> questionTexts = generated.stream()
                .map(ComprehensionQuestion::questionText)
                .toList();

        Instant now = Instant.now();
        Instant deadline = now.plusSeconds((long) questionTexts.size() * SECONDS_PER_QUESTION);

        ComprehensionCheck check = new ComprehensionCheck();
        check.setStudent(submission.getStudent());
        check.setDrill(submission.getDrill());
        check.setDrillSubmission(submission);
        check.setQuestions(questionTexts);
        check.setTriggeredAt(now);
        check.setServerDeadline(deadline);
        check.setStatus(ComprehensionCheckStatus.PENDING);

        return comprehensionCheckRepository.save(check);
    }

}
