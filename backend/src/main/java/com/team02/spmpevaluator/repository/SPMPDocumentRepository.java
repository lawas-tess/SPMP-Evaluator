package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.SPMPDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SPMP Document entities.
 * Handles database operations for uploaded documents.
 */
@Repository
public interface SPMPDocumentRepository extends JpaRepository<SPMPDocument, Long> {

    /**
     * Find all documents uploaded by a specific user.
     */
    List<SPMPDocument> findByUploadedBy_Id(Long userId);
    
    /**
     * Delete all documents uploaded by a specific user.
     */
    void deleteByUploadedById(Long userId);

    /**
     * Find documents by evaluation status.
     */
    List<SPMPDocument> findByEvaluated(boolean evaluated);

    /**
     * Find documents by user and evaluation status.
     */
    List<SPMPDocument> findByUploadedBy_IdAndEvaluated(Long userId, boolean evaluated);

    /**
     * Find document by ID with uploadedBy relationship eagerly loaded.
     * Prevents lazy loading issues when accessing user data.
     */
    @Query("SELECT d FROM SPMPDocument d LEFT JOIN FETCH d.uploadedBy WHERE d.id = :id")
    Optional<SPMPDocument> findByIdWithUploadedBy(@Param("id") Long id);

    /**
     * Find all documents with uploadedBy relationship eagerly loaded.
     */
    @Query("SELECT DISTINCT d FROM SPMPDocument d LEFT JOIN FETCH d.uploadedBy")
    List<SPMPDocument> findAllWithUploadedBy();

    /**
     * Find documents by filename pattern.
     */
    List<SPMPDocument> findByFileNameContainingIgnoreCase(String filename);

    /**
     * Count documents by user.
     */
    long countByUploadedBy_Id(Long userId);

    /**
     * Count evaluated documents by user.
     */
    long countByUploadedBy_IdAndEvaluated(Long userId, boolean evaluated);
}
