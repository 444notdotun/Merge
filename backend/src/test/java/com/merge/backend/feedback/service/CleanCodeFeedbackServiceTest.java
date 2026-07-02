package com.merge.backend.feedback.service;

import com.merge.backend.ai.context.PersonalisationContext;
import com.merge.backend.ai.context.PersonalisationContextEntry;
import com.merge.backend.ai.context.PersonalisationContextRetriever;
import com.merge.backend.ai.gateway.GeminiGateway;
import com.merge.backend.assessment.domain.DrillSubmission;
import com.merge.backend.assessment.repository.DrillSubmissionRepository;
import com.merge.backend.feedback.domain.CleanCodeFeedback;
import com.merge.backend.feedback.dto.CleanCodeFeedbackResult;
import com.merge.backend.feedback.repository.CleanCodeFeedbackRepository;
import com.merge.backend.identity.domain.Student;
import com.merge.backend.personalisation.domain.PersonalisationProfile;
import com.merge.backend.personalisation.repository.PersonalisationProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CleanCodeFeedbackServiceTest {

    private CleanCodeFeedbackService cleanCodeFeedbackService;

    @Mock
    private DrillSubmissionRepository drillSubmissionRepository;

    @Mock
    private CleanCodeFeedbackRepository cleanCodeFeedbackRepository;

    @Mock
    private GeminiGateway geminiGateway;

    @Mock
    private PersonalisationProfileRepository profileRepository;

    @Mock
    private PersonalisationContextRetriever contextRetriever;

    private Student testStudent;
    private DrillSubmission testSubmission;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cleanCodeFeedbackService = new CleanCodeFeedbackServiceImpl(
                drillSubmissionRepository,
                cleanCodeFeedbackRepository,
                geminiGateway,
                profileRepository,
                contextRetriever
        );

        testStudent = new Student(1L, "David Park", "david@test.com", "123", "david@u.edu", "hash", "CADET", 100, null, null, null);
        testSubmission = new DrillSubmission();
        testSubmission.setId(10L);
        testSubmission.setStudent(testStudent);
        testSubmission.setCode("function add(a, b) { return a + b; }");

        when(drillSubmissionRepository.findById(10L)).thenReturn(Optional.of(testSubmission));
        when(cleanCodeFeedbackRepository.findByDrillSubmissionId(10L)).thenReturn(Optional.empty());
    }

    @Test
    public void testGenerateFeedback_Cadet() {
        testStudent.setCurrentStage("CADET");

        CleanCodeFeedbackResult resultDto = new CleanCodeFeedbackResult(
                90,
                List.of("L1: Variable 'a' should be descriptive."),
                List.of(),
                List.of(),
                List.of(),
                false
        );
        when(geminiGateway.generateCleanCodeFeedback(anyString(), eq("CADET"))).thenReturn(resultDto);

        when(cleanCodeFeedbackRepository.save(any(CleanCodeFeedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CleanCodeFeedback feedback = cleanCodeFeedbackService.generateFeedback(10L, 0, 2);

        assertNotNull(feedback);
        assertEquals(90, feedback.getOverallScore());
        assertEquals(1, feedback.getNamingIssues().size());
        assertTrue(feedback.getFunctionSizeIssues().isEmpty());
        assertTrue(feedback.getRedundancyIssues().isEmpty());
        assertTrue(feedback.getSolidIssues().isEmpty());
        assertFalse(feedback.isSeniorReviewFlagged());

        verify(cleanCodeFeedbackRepository, times(1)).save(any(CleanCodeFeedback.class));
    }

    @Test
    public void testGenerateFeedback_Engineer() {
        testStudent.setCurrentStage("ENGINEER");

        CleanCodeFeedbackResult resultDto = new CleanCodeFeedbackResult(
                80,
                List.of("L1: Variable 'a' should be descriptive."),
                List.of("L5: Method too large."),
                List.of("L8: Redundant loop."),
                List.of(),
                false
        );
        when(geminiGateway.generateCleanCodeFeedback(anyString(), eq("ENGINEER"))).thenReturn(resultDto);

        when(cleanCodeFeedbackRepository.save(any(CleanCodeFeedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CleanCodeFeedback feedback = cleanCodeFeedbackService.generateFeedback(10L, 0, 2);

        assertNotNull(feedback);
        assertEquals(80, feedback.getOverallScore());
        assertEquals(1, feedback.getNamingIssues().size());
        assertEquals(1, feedback.getFunctionSizeIssues().size());
        assertEquals(1, feedback.getRedundancyIssues().size());
        assertTrue(feedback.getSolidIssues().isEmpty());

        verify(cleanCodeFeedbackRepository, times(1)).save(any(CleanCodeFeedback.class));
    }

    @Test
    public void testGenerateFeedback_RetryAttemptRethrows() {
        when(geminiGateway.generateCleanCodeFeedback(anyString(), anyString()))
                .thenThrow(new RuntimeException("Gemini Unavailable"));

        // First attempt (0 of 2) should rethrow the exception to trigger retry
        assertThrows(RuntimeException.class, () ->
                cleanCodeFeedbackService.generateFeedback(10L, 0, 2)
        );

        verify(cleanCodeFeedbackRepository, never()).save(any(CleanCodeFeedback.class));
    }

    @Test
    public void testGenerateFeedback_FinalAttemptTriggersFallback() {
        when(geminiGateway.generateCleanCodeFeedback(anyString(), anyString()))
                .thenThrow(new RuntimeException("Gemini Unavailable"));

        PersonalisationProfile profile = new PersonalisationProfile();
        profile.setEmbedding("[0.1,0.2]");
        when(profileRepository.findByStudentId(1L)).thenReturn(Optional.of(profile));

        // Mock pgvector similar students returning student 2
        PersonalisationContextEntry entry = new PersonalisationContextEntry(2L, 2L, List.of(), Map.of(), Map.of());
        PersonalisationContext context = new PersonalisationContext(List.of(entry), "similar");
        when(contextRetriever.retrieve("[0.1,0.2]")).thenReturn(context);

        // Mock similar student 2's previous feedback
        CleanCodeFeedback fallbackFeedback = new CleanCodeFeedback();
        fallbackFeedback.setOverallScore(85);
        fallbackFeedback.setNamingIssues(List.of("L5: descriptive fallback"));
        fallbackFeedback.setFunctionSizeIssues(List.of());
        fallbackFeedback.setRedundancyIssues(List.of());
        fallbackFeedback.setSolidIssues(List.of());
        fallbackFeedback.setSeniorReviewFlagged(false);

        Student similarStudent = new Student(2L, "Similar Student", "sim@test.com", "222", "sim@u.edu", "hash", "CADET", 100, null, null, null);
        DrillSubmission sub2 = new DrillSubmission();
        sub2.setStudent(similarStudent);
        fallbackFeedback.setDrillSubmission(sub2);

        when(cleanCodeFeedbackRepository.findAll()).thenReturn(List.of(fallbackFeedback));
        when(cleanCodeFeedbackRepository.save(any(CleanCodeFeedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Last attempt (1 of 2) should execute fallback and not throw
        CleanCodeFeedback feedback = cleanCodeFeedbackService.generateFeedback(10L, 1, 2);

        assertNotNull(feedback);
        assertEquals(85, feedback.getOverallScore());
        assertEquals("L5: descriptive fallback", feedback.getNamingIssues().get(0));
        verify(cleanCodeFeedbackRepository, times(1)).save(any(CleanCodeFeedback.class));
    }
}
