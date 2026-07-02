package com.merge.backend.curriculum.repository;

import com.merge.backend.curriculum.domain.ConceptContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConceptContentRepository extends JpaRepository<ConceptContent, Long> {

    Optional<ConceptContent> findByStudentIdAndConceptId(Long studentId, Long conceptId);

    void deleteByStudentIdAndConceptId(Long studentId, Long conceptId);
}
