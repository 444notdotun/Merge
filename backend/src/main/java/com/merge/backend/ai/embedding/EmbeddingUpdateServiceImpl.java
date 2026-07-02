package com.merge.backend.ai.embedding;

import com.merge.backend.ai.gateway.GeminiGateway;
import com.merge.backend.personalisation.domain.PersonalisationProfile;
import com.merge.backend.personalisation.repository.PersonalisationProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.StringJoiner;

/**
 * AI-06: Generates and stores a 1536-dimensional embedding for a student's
 * personalisation profile. Called after every profile write (session end via
 * PersonalisationUpdateService, and approved Drill submission via ComprehensionSubmitService).
 *
 * The stored vector enables PersonalisationContextRetriever to find similar learners
 * via cosine distance (embedding <=> query_vector) for RAG-style prompt context.
 *
 * Gemini failures are caught internally so a transient API error never rolls back
 * the outer drill pass or session update.
 */
@Service
@Transactional
public class EmbeddingUpdateServiceImpl implements EmbeddingUpdateService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingUpdateServiceImpl.class);

    private final PersonalisationProfileRepository profileRepository;
    private final GeminiGateway geminiGateway;

    public EmbeddingUpdateServiceImpl(PersonalisationProfileRepository profileRepository,
                                      GeminiGateway geminiGateway) {
        this.profileRepository = profileRepository;
        this.geminiGateway = geminiGateway;
    }

    @Override
    public void triggerPersonalisationEmbeddingUpdate(Long studentId) {
        log.info("[EmbeddingUpdate] Triggering embedding update for studentId={}", studentId);

        PersonalisationProfile profile = profileRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Personalisation profile not found for student: " + studentId));

        String text = buildEmbeddingText(profile);
        if (text.isBlank()) {
            log.debug("[EmbeddingUpdate] Profile text is empty for student={} — skipping", studentId);
            return;
        }

        List<Float> vector;
        try {
            vector = geminiGateway.generateEmbedding(text);
        } catch (Exception e) {
            log.warn("[EmbeddingUpdate] Gemini embedding call failed for student={}: {}",
                    studentId, e.getMessage());
            return;
        }

        profile.setEmbedding(toVectorLiteral(vector));
        profileRepository.save(profile);

        log.info("[EmbeddingUpdate] Vector embedding updated for studentId={}", studentId);
    }

    /**
     * Serialises the student's current profile into labelled plain text for embedding.
     * Each profile dimension is a separate line so the model receives structured signal.
     * Null/empty fields are omitted — a sparse profile generates a shorter but still
     * valid text (Gemini handles variable-length inputs).
     */
    private String buildEmbeddingText(PersonalisationProfile profile) {
        StringBuilder sb = new StringBuilder();

        if (profile.getWeakConcepts() != null && !profile.getWeakConcepts().isEmpty()) {
            sb.append("weak concepts: ").append(String.join(", ", profile.getWeakConcepts())).append("\n");
        }
        if (profile.getStrengthConcepts() != null && !profile.getStrengthConcepts().isEmpty()) {
            sb.append("strong concepts: ").append(String.join(", ", profile.getStrengthConcepts())).append("\n");
        }
        if (profile.getScaffoldingLevel() != null) {
            sb.append("scaffolding level: ").append(profile.getScaffoldingLevel().name()).append("\n");
        }
        if (profile.getThinkingStyle() != null) {
            sb.append("thinking style: ").append(profile.getThinkingStyle().name()).append("\n");
        }
        if (profile.getLearningApproach() != null) {
            sb.append("learning approach: ").append(profile.getLearningApproach().name()).append("\n");
        }
        if (profile.getHintUsagePattern() != null && !profile.getHintUsagePattern().isEmpty()) {
            sb.append("hint usage: ").append(profile.getHintUsagePattern()).append("\n");
        }
        if (profile.getCodingStylePatterns() != null && !profile.getCodingStylePatterns().isEmpty()) {
            sb.append("coding patterns: ").append(profile.getCodingStylePatterns()).append("\n");
        }

        return sb.toString().trim();
    }

    /**
     * Converts a float list to pgvector literal format: "[f1,f2,...,fn]".
     * pgvector parses this string when the column type is vector(N).
     */
    private static String toVectorLiteral(List<Float> vector) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (Float f : vector) {
            joiner.add(String.valueOf(f));
        }
        return joiner.toString();
    }
}
