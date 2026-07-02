package com.merge.backend.curriculum.repository;

import com.merge.backend.curriculum.domain.ConceptUnlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptUnlockRepository extends JpaRepository<ConceptUnlock, Long> {

    boolean existsByStudentIdAndConceptId(Long studentId, Long conceptId);

    boolean existsByStudentIdAndConceptStageName(Long studentId, String stageName);
}
