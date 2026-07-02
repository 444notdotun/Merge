package com.merge.backend.identity.repository;

import com.merge.backend.identity.domain.Student;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUniversityEmail(String universityEmail);

    /**
     * Fetches the student with a database-level pessimistic write lock
     * (SELECT … FOR UPDATE). Use inside a transaction when total_xp
     * must be read and updated atomically.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Student s WHERE s.id = :id")
    Optional<Student> findByIdForUpdate(@Param("id") Long id);
}
