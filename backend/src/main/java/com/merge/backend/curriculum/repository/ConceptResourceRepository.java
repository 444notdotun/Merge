package com.merge.backend.curriculum.repository;

import com.merge.backend.curriculum.domain.ConceptResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConceptResourceRepository extends JpaRepository<ConceptResource, Long> {

    List<ConceptResource> findByConceptIdOrderByTypeAscTitleAsc(Long conceptId);
}
