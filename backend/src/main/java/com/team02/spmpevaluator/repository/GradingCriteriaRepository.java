package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.GradingCriteria;
import com.team02.spmpevaluator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for GradingCriteria entity.
 * UC 2.7 - Database operations for grading criteria management.
 */
@Repository
public interface GradingCriteriaRepository extends JpaRepository<GradingCriteria, Long> {

    /**
     * Find all grading criteria created by a specific professor.
     */
    List<GradingCriteria> findByCreatedByOrderByUpdatedAtDesc(User createdBy);

    /**
     * Find the active grading criteria for a professor.
     */
    Optional<GradingCriteria> findByCreatedByAndIsActiveTrue(User createdBy);

    /**
     * Find all grading criteria by professor ID.
     */
    @Query("SELECT gc FROM GradingCriteria gc WHERE gc.createdBy.id = :professorId ORDER BY gc.updatedAt DESC")
    List<GradingCriteria> findByProfessorId(@Param("professorId") Long professorId);

    /**
     * Find active criteria by professor ID.
     */
    @Query("SELECT gc FROM GradingCriteria gc WHERE gc.createdBy.id = :professorId AND gc.isActive = true")
    Optional<GradingCriteria> findActiveCriteriaByProfessorId(@Param("professorId") Long professorId);

    /**
     * Deactivate all criteria for a professor (used before setting a new active one).
     */
    @Modifying
    @Query("UPDATE GradingCriteria gc SET gc.isActive = false WHERE gc.createdBy.id = :professorId")
    void deactivateAllCriteriaForProfessor(@Param("professorId") Long professorId);

    /**
     * Check if a grading criteria with the same name exists for the professor.
     */
    boolean existsByNameAndCreatedBy(String name, User createdBy);

    /**
     * Find by name and professor.
     */
    Optional<GradingCriteria> findByNameAndCreatedBy(String name, User createdBy);
}
