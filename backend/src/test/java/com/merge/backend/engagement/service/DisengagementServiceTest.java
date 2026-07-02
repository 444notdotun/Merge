package com.merge.backend.engagement.service;

import com.merge.backend.ai.gateway.GeminiGateway;
import com.merge.backend.assessment.domain.Drill;
import com.merge.backend.assessment.domain.DrillSubmission;
import com.merge.backend.assessment.repository.DrillSubmissionRepository;
import com.merge.backend.curriculum.domain.Concept;
import com.merge.backend.engagement.domain.MomentumState;
import com.merge.backend.engagement.domain.WeeklyMomentum;
import com.merge.backend.engagement.repository.WeeklyMomentumRepository;
import com.merge.backend.identity.domain.Student;
import com.merge.backend.identity.repository.StudentRepository;
import com.merge.backend.integration.service.IntercomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DisengagementServiceTest {

    private DisengagementService disengagementService;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private WeeklyMomentumRepository weeklyMomentumRepository;

    @Mock
    private DrillSubmissionRepository drillSubmissionRepository;

    @Mock
    private GeminiGateway geminiGateway;

    @Mock
    private IntercomService intercomService;

    private Student testStudent;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        disengagementService = new DisengagementServiceImpl(
                studentRepository,
                weeklyMomentumRepository,
                drillSubmissionRepository,
                geminiGateway,
                intercomService
        );

        testStudent = new Student(1L, "David Park", "david@test.com", "123", "david@u.edu", "hash", "CADET", 100, null, null, null);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
    }

    @Test
    public void testCheckDisengagement_ActiveStateSkipsReachOut() {
        WeeklyMomentum momentum = new WeeklyMomentum(1L, testStudent, LocalDate.now(), MomentumState.DEPLOYING, 5, 0.95, false, Instant.now());
        when(weeklyMomentumRepository.findFirstByStudentIdOrderByWeekStartDesc(1L)).thenReturn(Optional.of(momentum));

        disengagementService.checkDisengagement(1L);

        verify(geminiGateway, never()).generateDisengagementReachOut(anyString(), anyString(), anyList());
        verify(intercomService, never()).sendReachOutMessage(anyString(), anyString());
    }

    @Test
    public void testCheckDisengagement_BlockedTriggersReachOut() {
        WeeklyMomentum momentum = new WeeklyMomentum(1L, testStudent, LocalDate.now(), MomentumState.BLOCKED, 0, 0.0, false, Instant.now());
        when(weeklyMomentumRepository.findFirstByStudentIdOrderByWeekStartDesc(1L)).thenReturn(Optional.of(momentum));

        // Setup concept and drill submission
        Concept concept = new Concept();
        concept.setName("Variables");
        Drill drill = new Drill();
        drill.setConcept(concept);
        DrillSubmission submission = new DrillSubmission();
        submission.setDrill(drill);
        when(drillSubmissionRepository.findFirstByStudentIdOrderBySubmittedAtDesc(1L)).thenReturn(Optional.of(submission));

        when(geminiGateway.generateDisengagementReachOut(eq("David Park"), eq("Variables"), anyList()))
                .thenReturn("Hey David, let's look at Variables.");

        disengagementService.checkDisengagement(1L);

        verify(geminiGateway, times(1)).generateDisengagementReachOut(eq("David Park"), eq("Variables"), anyList());
        verify(intercomService, times(1)).sendReachOutMessage(eq("david@test.com"), eq("Hey David, let's look at Variables."));
    }

    @Test
    public void testCheckDisengagement_OfflineNoSubmissionsDefaultConcept() {
        WeeklyMomentum momentum = new WeeklyMomentum(1L, testStudent, LocalDate.now(), MomentumState.OFFLINE, 0, 0.0, false, Instant.now());
        when(weeklyMomentumRepository.findFirstByStudentIdOrderByWeekStartDesc(1L)).thenReturn(Optional.of(momentum));

        // No submissions
        when(drillSubmissionRepository.findFirstByStudentIdOrderBySubmittedAtDesc(1L)).thenReturn(Optional.empty());

        when(geminiGateway.generateDisengagementReachOut(eq("David Park"), eq("Initial Scaffolding Drills"), anyList()))
                .thenReturn("Hey David, let's start.");

        disengagementService.checkDisengagement(1L);

        verify(geminiGateway, times(1)).generateDisengagementReachOut(eq("David Park"), eq("Initial Scaffolding Drills"), anyList());
        verify(intercomService, times(1)).sendReachOutMessage(eq("david@test.com"), eq("Hey David, let's start."));
    }

    @Test
    public void testCheckDisengagement_AiFailureGracefulSkip() {
        WeeklyMomentum momentum = new WeeklyMomentum(1L, testStudent, LocalDate.now(), MomentumState.BLOCKED, 0, 0.0, false, Instant.now());
        when(weeklyMomentumRepository.findFirstByStudentIdOrderByWeekStartDesc(1L)).thenReturn(Optional.of(momentum));
        when(drillSubmissionRepository.findFirstByStudentIdOrderBySubmittedAtDesc(1L)).thenReturn(Optional.empty());

        when(geminiGateway.generateDisengagementReachOut(anyString(), anyString(), anyList()))
                .thenThrow(new RuntimeException("API limit reached"));

        // Assert that calling checkDisengagement does not crash
        assertDoesNotThrow(() -> disengagementService.checkDisengagement(1L));

        verify(intercomService, never()).sendReachOutMessage(anyString(), anyString());
    }
}
