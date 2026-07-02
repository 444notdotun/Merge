package com.merge.backend.assessment.repository;

import com.merge.backend.assessment.domain.BuildComprehensionCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildComprehensionCheckRepository extends JpaRepository<BuildComprehensionCheck, Long> {

    Optional<BuildComprehensionCheck> findByBuildSubmissionId(Long buildSubmissionId);
}
