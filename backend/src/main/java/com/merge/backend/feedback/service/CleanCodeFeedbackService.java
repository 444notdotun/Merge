package com.merge.backend.feedback.service;

import com.merge.backend.feedback.domain.CleanCodeFeedback;

public interface CleanCodeFeedbackService {
    CleanCodeFeedback generateFeedback(Long submissionId, int attemptCount, int maxAttempts);
}
