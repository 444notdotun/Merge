package com.merge.backend.curriculum.repository;

import com.merge.backend.curriculum.domain.ResourceCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceCompletionRepository extends JpaRepository<ResourceCompletion, Long> {

    boolean existsByStudentIdAndResourceId(Long studentId, Long resourceId);
}
