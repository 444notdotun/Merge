package com.merge.backend.assessment.repository;

import com.merge.backend.assessment.domain.ComprehensionCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComprehensionCheckRepository extends JpaRepository<ComprehensionCheck, Long> {

    Optional<ComprehensionCheck> findByDrillSubmissionId(Long drillSubmissionId);
}
