package com.devarena.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devarena.project.dto.response.ProjectSummaryResponseDto;
import com.devarena.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT new com.devarena.project.dto.response.ProjectSummaryResponseDto(p.id, p.title, p.topic, p.owner.username) "
    + "FROM Project p WHERE p.owner.id = :ownerId")
    List<ProjectSummaryResponseDto> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT new com.devarena.project.dto.response" +
    ".ProjectSummaryResponseDto(p.id, p.title, p.topic, p.owner.username) "
    + "FROM Project p WHERE p.topic = :topic")
    List<ProjectSummaryResponseDto> findByTopic(@Param("topic") String topic, Pageable pageable);

    @Query("SELECT new com.devarena.project.dto.response.ProjectSummaryResponseDto(p.id, p.title, p.topic, p.owner.username) "
    + "FROM Project p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ProjectSummaryResponseDto> searchByTitleContaining(@Param("keyword") String keyword);

    @Query("SELECT p FROM Project p WHERE p.id = :projectId")
    @EntityGraph(attributePaths = {"owner"})
    Optional<Project> findProjectWithOwner(@Param("projectId") Long projectId);

}
