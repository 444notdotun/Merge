package com.merge.backend.feedback.dto;

import java.util.List;

public record CleanCodeFeedbackResult(
        int overallScore,
        List<String> namingIssues,
        List<String> functionSizeIssues,
        List<String> redundancyIssues,
        List<String> solidIssues,
        boolean seniorReviewFlagged
) {}
