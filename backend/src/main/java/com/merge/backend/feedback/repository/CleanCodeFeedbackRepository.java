package com.merge.backend.feedback.repository;

import com.merge.backend.feedback.domain.CleanCodeFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CleanCodeFeedbackRepository extends JpaRepository<CleanCodeFeedback, Long> {
    Optional<CleanCodeFeedback> findByDrillSubmissionId(Long drillSubmissionId);
}
