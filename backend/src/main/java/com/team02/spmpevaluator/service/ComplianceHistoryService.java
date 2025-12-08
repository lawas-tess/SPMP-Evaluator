package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.entity.ComplianceScoreHistory;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.repository.ComplianceScoreHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplianceHistoryService {

    private final ComplianceScoreHistoryRepository historyRepository;

    /**
     * Archive the current compliance score before re-evaluation or override.
     */
    public void archiveScore(ComplianceScore currentScore, String source, Long recordedByUserId) {
        if (currentScore == null) {
            return;
        }

        SPMPDocument document = currentScore.getDocument();
        ComplianceScoreHistory history = new ComplianceScoreHistory();
        history.setDocument(document);
        history.setOverallScore(currentScore.getOverallScore());
        history.setStructureScore(currentScore.getStructureScore());
        history.setCompletenessScore(currentScore.getCompletenessScore());
        history.setSectionsFound(currentScore.getSectionsFound());
        history.setTotalSectionsRequired(currentScore.getTotalSectionsRequired());
        history.setCompliant(currentScore.isCompliant());
        history.setProfessorOverride(currentScore.getProfessorOverride());
        history.setProfessorNotes(currentScore.getProfessorNotes());
        history.setSummary(currentScore.getSummary());
        history.setEvaluatedAt(currentScore.getEvaluatedAt());
        history.setRecordedByUserId(recordedByUserId);
        history.setSource(source);
        history.setVersionNumber(historyRepository.countByDocumentId(document.getId()) + 1);
        history.setRecordedAt(LocalDateTime.now());

        historyRepository.save(history);
    }

    public List<ComplianceScoreHistory> getHistoryForDocument(Long documentId) {
        return historyRepository.findByDocumentIdOrderByRecordedAtDesc(documentId);
    }
}
