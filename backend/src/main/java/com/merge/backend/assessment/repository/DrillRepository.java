package com.merge.backend.assessment.repository;

import com.merge.backend.assessment.domain.Drill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillRepository extends JpaRepository<Drill, Long> {

    List<Drill> findByConceptId(Long conceptId);
}
