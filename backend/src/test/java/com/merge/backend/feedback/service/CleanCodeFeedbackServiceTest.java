package com.merge.backend.feedback.service;

import com.merge.backend.ai.gateway.GeminiGateway;
import com.merge.backend.assessment.domain.DrillSubmission;
import com.merge.backend.assessment.repository.DrillSubmissionRepository;
import com.merge.backend.feedback.domain.CleanCodeFeedback;
import com.merge.backend.feedback.dto.CleanCodeFeedbackResult;
import com.merge.backend.feedback.repository.CleanCodeFeedbackRepository;
import com.merge.backend.identity.domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
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

    private Student testStudent;
    private DrillSubmission testSubmission;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cleanCodeFeedbackService = new CleanCodeFeedbackServiceImpl(
                drillSubmissionRepository,
                cleanCodeFeedbackRepository,
                geminiGateway
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

        CleanCodeFeedback feedback = cleanCodeFeedbackService.generateFeedback(10L);

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

        CleanCodeFeedback feedback = cleanCodeFeedbackService.generateFeedback(10L);

        assertNotNull(feedback);
        assertEquals(80, feedback.getOverallScore());
        assertEquals(1, feedback.getNamingIssues().size());
        assertEquals(1, feedback.getFunctionSizeIssues().size());
        assertEquals(1, feedback.getRedundancyIssues().size());
        assertTrue(feedback.getSolidIssues().isEmpty());

        verify(cleanCodeFeedbackRepository, times(1)).save(any(CleanCodeFeedback.class));
    }
}
