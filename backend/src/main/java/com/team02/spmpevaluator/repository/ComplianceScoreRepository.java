package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.entity.SPMPDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplianceScoreRepository extends JpaRepository<ComplianceScore, Long> {
    Optional<ComplianceScore> findByDocument(SPMPDocument document);
    Optional<ComplianceScore> findByDocumentId(Long documentId);
    
    @Query("SELECT cs FROM ComplianceScore cs LEFT JOIN FETCH cs.sectionAnalyses WHERE cs.document.id = :documentId")
    Optional<ComplianceScore> findByDocumentIdWithSectionAnalyses(@Param("documentId") Long documentId);
}
