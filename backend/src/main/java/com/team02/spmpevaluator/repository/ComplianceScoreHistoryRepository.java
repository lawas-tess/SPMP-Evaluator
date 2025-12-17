package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.ComplianceScoreHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceScoreHistoryRepository extends JpaRepository<ComplianceScoreHistory, Long> {
    List<ComplianceScoreHistory> findByDocumentIdOrderByRecordedAtDesc(Long documentId);

    int countByDocumentId(Long documentId);
    
    void deleteByDocumentId(Long documentId);
}
