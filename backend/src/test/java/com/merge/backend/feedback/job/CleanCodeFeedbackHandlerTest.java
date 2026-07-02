package com.merge.backend.feedback.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merge.backend.feedback.dto.CleanCodeFeedbackPayload;
import com.merge.backend.feedback.service.CleanCodeFeedbackService;
import com.merge.backend.infrastructure.queue.JobPayload;
import com.merge.backend.infrastructure.queue.JobType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CleanCodeFeedbackHandlerTest {

    private CleanCodeFeedbackHandler handler;

    @Mock
    private CleanCodeFeedbackService cleanCodeFeedbackService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new CleanCodeFeedbackHandler(cleanCodeFeedbackService, objectMapper);
    }

    @Test
    public void testJobType() {
        assertEquals(JobType.CLEAN_CODE_FEEDBACK, handler.jobType());
    }

    @Test
    public void testHandle() throws Exception {
        CleanCodeFeedbackPayload payload = new CleanCodeFeedbackPayload();
        payload.setSubmissionId(42L);
        String json = objectMapper.writeValueAsString(payload);

        JobPayload jobPayload = mock(JobPayload.class);
        when(jobPayload.getPayloadJson()).thenReturn(json);
        when(jobPayload.getAttemptCount()).thenReturn(1);

        handler.handle(jobPayload);

        verify(cleanCodeFeedbackService, times(1)).generateFeedback(42L, 1, 2);
    }
}
