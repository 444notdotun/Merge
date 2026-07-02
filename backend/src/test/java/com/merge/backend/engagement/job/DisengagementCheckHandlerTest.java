package com.merge.backend.engagement.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merge.backend.engagement.dto.DisengagementCheckPayload;
import com.merge.backend.engagement.service.DisengagementService;
import com.merge.backend.infrastructure.queue.JobPayload;
import com.merge.backend.infrastructure.queue.JobType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DisengagementCheckHandlerTest {

    private DisengagementCheckHandler handler;

    @Mock
    private DisengagementService disengagementService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DisengagementCheckHandler(disengagementService, objectMapper);
    }

    @Test
    public void testJobType() {
        assertEquals(JobType.DISENGAGEMENT_CHECK, handler.jobType());
    }

    @Test
    public void testHandle() throws Exception {
        DisengagementCheckPayload payload = new DisengagementCheckPayload();
        payload.setStudentId(101L);
        String json = objectMapper.writeValueAsString(payload);

        JobPayload jobPayload = mock(JobPayload.class);
        when(jobPayload.getPayloadJson()).thenReturn(json);

        handler.handle(jobPayload);

        verify(disengagementService, times(1)).checkDisengagement(101L);
    }
}
