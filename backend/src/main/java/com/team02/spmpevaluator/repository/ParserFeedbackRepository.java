package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.ParserFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParserFeedbackRepository extends JpaRepository<ParserFeedback, Long> {
    
    Optional<ParserFeedback> findByDocument(SPMPDocument document);
    
    List<ParserFeedback> findByDocumentId(Long documentId);
    
    List<ParserFeedback> findByStatus(ParserFeedback.FeedbackStatus status);
    
    List<ParserFeedback> findByDocumentUploadedByIdOrderByAnalyzedAtDesc(Long userId);
}
