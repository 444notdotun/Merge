package com.merge.backend.scout.service;

import com.merge.backend.scout.dto.Layer1QuestionsResponse;
import com.merge.backend.scout.dto.Layer1SubmitRequest;
import com.merge.backend.scout.dto.Layer1SubmitResponse;
import com.merge.backend.scout.dto.Layer2ProblemsResponse;
import com.merge.backend.scout.dto.Layer2SubmitRequest;
import com.merge.backend.scout.dto.Layer2SubmitResponse;

public interface ScoutService {
    Layer1QuestionsResponse getLayer1Questions();
    Layer1SubmitResponse submitLayer1(String studentEmail, Layer1SubmitRequest request);
    Layer2ProblemsResponse getLayer2Problems();
    Layer2SubmitResponse submitLayer2(String studentEmail, Layer2SubmitRequest request);
}
