package com.merge.backend.ai.gateway;

import com.merge.backend.assessment.dto.ComprehensionQuestion;
import com.merge.backend.assessment.dto.ComprehensionQuestionsRequest;
import com.merge.backend.assessment.dto.ComprehensionScoreRequest;
import com.merge.backend.assessment.dto.GenerateDrillsRequest;
import com.merge.backend.assessment.dto.GeneratedDrill;
import com.merge.backend.curriculum.dto.ConceptExplanationRequest;
import com.merge.backend.personalisation.dto.PersonalisationAiResult;
import com.merge.backend.personalisation.dto.SessionAnalysisPayload;

import java.util.List;

/**
 * AI-01: Gemini API gateway.
 * Implemented by the ai module; consumed by feature modules that need AI analysis.
 */
public interface GeminiGateway {

    /**
     * Sends session metrics to Gemini and returns a structured personalisation analysis.
     * Evaluates hint patterns, comprehension scores, pass/fail history, and question types
     * to classify weak/strength concepts, scaffolding level, and coding style signals.
     */
    PersonalisationAiResult analyseSessionForPersonalisation(SessionAnalysisPayload payload);

    /**
     * CurriculumWriter prompt: generates a 500-800 word personalised written explanation
     * of a concept, tailored to the student's scaffolding level, thinking style,
     * learning approach, and prior exposure.
     *
     * @return the generated explanation text (plain prose, no Markdown headers)
     */
    String generateConceptExplanation(ConceptExplanationRequest request);

    /**
     * AI-02 — DrillWriter prompt: generates Drill 1 and Drill 2 for a concept,
     * calibrated to the student's personalisation profile.
     * Drill 1 is simpler (guided scaffold); Drill 2 raises the difficulty.
     * Always returns exactly 2 elements ordered by drillNumber.
     */
    List<GeneratedDrill> generateDrills(GenerateDrillsRequest request);

    /**
     * AI-04 — ComprehensionWriter prompt: generates questions grounded in the student's
     * specific implementation — their actual variable names, function choices, and
     * architectural decisions. Questions must be unanswerable without reading this exact code.
     *
     * Always called with fresh context so questions differ across submission attempts.
     * Returns a list of questions; caller computes serverDeadline as
     * triggeredAt + (result.size() × 10 seconds).
     */
    List<ComprehensionQuestion> generateComprehensionQuestions(ComprehensionQuestionsRequest request);

    /**
     * AI-05 — ComprehensionScorer prompt: evaluates the student's answers against
     * their specific code implementation. Verifies that answers reference concrete details
     * of the submitted code (variable names, chosen algorithms, data structures).
     * Generic answers that could apply to any implementation are scored as failed.
     *
     * @return true if the student demonstrated genuine understanding; false otherwise
     */
    boolean scoreComprehensionAnswers(ComprehensionScoreRequest request);
}
