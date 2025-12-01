package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.SPMPDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SPMPDocumentRepository extends JpaRepository<SPMPDocument, Long> {
    List<SPMPDocument> findByUploadedBy_Id(Long userId);
    List<SPMPDocument> findByUploadedBy_IdAndEvaluated(Long userId, boolean evaluated);
    List<SPMPDocument> findByEvaluated(boolean evaluated);
}
