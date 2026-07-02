package com.merge.backend.engagement.repository;

import com.merge.backend.engagement.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

    java.util.Optional<Session> findByIdAndStudentId(String id, Long studentId);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.student.id = :studentId " +
           "AND s.startedAt >= :from AND s.startedAt < :to")
    int countSessionsBetween(@Param("studentId") Long studentId,
                             @Param("from") Instant from,
                             @Param("to") Instant to);
}
