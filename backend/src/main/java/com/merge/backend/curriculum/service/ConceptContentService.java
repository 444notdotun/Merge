package com.merge.backend.curriculum.service;

import com.merge.backend.ai.gateway.GeminiGateway;
import com.merge.backend.curriculum.domain.Concept;
import com.merge.backend.curriculum.domain.ConceptContent;
import com.merge.backend.curriculum.dto.ConceptExplanationRequest;
import com.merge.backend.curriculum.repository.ConceptContentRepository;
import com.merge.backend.curriculum.repository.ConceptRepository;
import com.merge.backend.identity.domain.Student;
import com.merge.backend.identity.repository.StudentRepository;
import com.merge.backend.personalisation.domain.PersonalisationProfile;
import com.merge.backend.personalisation.repository.PersonalisationProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class ConceptContentService {

    private final ConceptRepository conceptRepository;
    private final ConceptContentRepository contentRepository;
    private final StudentRepository studentRepository;
    private final PersonalisationProfileRepository profileRepository;
    private final GeminiGateway geminiGateway;

    public ConceptContentService(ConceptRepository conceptRepository,
                                 ConceptContentRepository contentRepository,
                                 StudentRepository studentRepository,
                                 PersonalisationProfileRepository profileRepository,
                                 GeminiGateway geminiGateway) {
        this.conceptRepository = conceptRepository;
        this.contentRepository = contentRepository;
        this.studentRepository = studentRepository;
        this.profileRepository = profileRepository;
        this.geminiGateway = geminiGateway;
    }

    /**
     * Returns personalised concept content for the authenticated student.
     * Serves from cache if present and not stale (personalisation_version == profile.version).
     * Otherwise calls the CurriculumWriter prompt via AI-01, stores the result, and returns it.
     */
    @Transactional
    public ConceptContent getOrGenerate(Long conceptId, String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentEmail));

        Concept concept = conceptRepository.findById(conceptId)
                .orElseThrow(() -> new ConceptNotFoundException("Concept not found: " + conceptId));

        PersonalisationProfile profile = profileRepository.findByStudentId(student.getId())
                .orElse(null);

        int currentVersion = profile != null ? profile.getVersion() : 1;

        Optional<ConceptContent> cached = contentRepository
                .findByStudentIdAndConceptId(student.getId(), conceptId);

        if (cached.isPresent() && cached.get().getPersonalisationVersion() == currentVersion) {
            return cached.get();
        }

        String explanation = geminiGateway.generateConceptExplanation(
                buildRequest(concept, profile));

        ConceptContent content = cached.orElseGet(ConceptContent::new);
        content.setStudent(student);
        content.setConcept(concept);
        content.setExplanationText(explanation);
        content.setGeneratedAt(Instant.now());
        content.setPersonalisationVersion(currentVersion);

        return contentRepository.save(content);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private ConceptExplanationRequest buildRequest(Concept concept, PersonalisationProfile profile) {
        return new ConceptExplanationRequest(
                concept.getName(),
                concept.getSfiaSkill(),
                concept.getFailureScenario(),
                profile != null && profile.getScaffoldingLevel() != null
                        ? profile.getScaffoldingLevel().name() : "MEDIUM",
                profile != null && profile.getThinkingStyle() != null
                        ? profile.getThinkingStyle().name() : "SYSTEMATIC",
                profile != null && profile.getLearningApproach() != null
                        ? profile.getLearningApproach().name() : "EXAMPLES_FIRST",
                profile != null && profile.getPriorExposure() != null
                        ? profile.getPriorExposure().name() : "NONE"
        );
    }
}
