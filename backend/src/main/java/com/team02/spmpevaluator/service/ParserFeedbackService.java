package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.ParserConfiguration;
import com.team02.spmpevaluator.entity.ParserFeedback;
import com.team02.spmpevaluator.repository.ParserFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing parser feedback and AI-based document analysis.
 * This service will eventually integrate with an AI parser module for
 * IEEE 1058 compliance analysis.
 */
@Service
@RequiredArgsConstructor
public class ParserFeedbackService {

    private final ParserFeedbackRepository parserFeedbackRepository;

    /**
     * Create a new parser feedback entry (for future AI integration)
     */
    @Transactional
    public ParserFeedback createFeedback(SPMPDocument document, ParserConfiguration config) {
        ParserFeedback feedback = new ParserFeedback();
        feedback.setDocument(document);
        feedback.setParserConfiguration(config);
        feedback.setStatus(ParserFeedback.FeedbackStatus.PENDING);
        feedback.setAnalyzedAt(LocalDateTime.now());
        feedback.setParserVersion("1.0.0-MOCK");
        
        return parserFeedbackRepository.save(feedback);
    }

    /**
     * Generate mock feedback for demonstration purposes
     * TODO: Replace with actual AI parser integration
     */
    @Transactional
    public ParserFeedback generateMockFeedback(SPMPDocument document, ParserConfiguration config) {
        ParserFeedback feedback = new ParserFeedback();
        feedback.setDocument(document);
        feedback.setParserConfiguration(config);
        feedback.setComplianceScore(75.0);
        feedback.setDetectedClauses(getMockDetectedClauses());
        feedback.setMissingClauses(getMockMissingClauses());
        feedback.setRecommendations(getMockRecommendations());
        feedback.setAnalysisReport("Mock analysis report: Document shows moderate compliance with IEEE 1058 standard.");
        feedback.setStatus(ParserFeedback.FeedbackStatus.COMPLETED);
        feedback.setAnalyzedAt(LocalDateTime.now());
        feedback.setParserVersion("1.0.0-MOCK");
        
        return parserFeedbackRepository.save(feedback);
    }

    /**
     * Get feedback for a specific document
     */
    public Optional<ParserFeedback> getFeedbackByDocument(SPMPDocument document) {
        return parserFeedbackRepository.findByDocument(document);
    }

    /**
     * Get all feedback for a document by ID
     */
    public List<ParserFeedback> getFeedbackByDocumentId(Long documentId) {
        return parserFeedbackRepository.findByDocumentId(documentId);
    }

    /**
     * Get feedback by ID
     */
    public Optional<ParserFeedback> getFeedbackById(Long id) {
        return parserFeedbackRepository.findById(id);
    }

    /**
     * Get all feedback for documents uploaded by a specific user
     */
    public List<ParserFeedback> getFeedbackByUserId(Long userId) {
        return parserFeedbackRepository.findByDocumentUploadedByIdOrderByAnalyzedAtDesc(userId);
    }

    /**
     * Update feedback status
     */
    @Transactional
    public ParserFeedback updateStatus(Long feedbackId, ParserFeedback.FeedbackStatus status, String errorMessage) {
        ParserFeedback feedback = parserFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Parser feedback not found with id: " + feedbackId));
        
        feedback.setStatus(status);
        if (errorMessage != null) {
            feedback.setErrorMessage(errorMessage);
        }
        
        return parserFeedbackRepository.save(feedback);
    }

    /**
     * Delete feedback
     */
    @Transactional
    public void deleteFeedback(Long id) {
        parserFeedbackRepository.deleteById(id);
    }

    // Mock data methods - to be replaced with AI parser integration

    private String getMockDetectedClauses() {
        return """
                [
                    {"clauseId": "1", "clauseName": "Overview", "score": 90, "found": true, "location": "Page 1"},
                    {"clauseId": "1.1", "clauseName": "Project Overview", "score": 85, "found": true, "location": "Page 1"},
                    {"clauseId": "2", "clauseName": "References", "score": 70, "found": true, "location": "Page 2"},
                    {"clauseId": "4", "clauseName": "Project Organization", "score": 80, "found": true, "location": "Page 3"},
                    {"clauseId": "5", "clauseName": "Managerial Process Plans", "score": 75, "found": true, "location": "Page 4"},
                    {"clauseId": "5.2", "clauseName": "Work Plan", "score": 85, "found": true, "location": "Page 5"}
                ]
                """;
    }

    private String getMockMissingClauses() {
        return """
                [
                    {"clauseId": "3", "clauseName": "Definitions", "severity": "medium", "reason": "No definitions or glossary section found"},
                    {"clauseId": "5.3", "clauseName": "Control Plan", "severity": "high", "reason": "Missing control and monitoring procedures"},
                    {"clauseId": "6", "clauseName": "Technical Process Plans", "severity": "high", "reason": "No technical methodology or development processes documented"},
                    {"clauseId": "7", "clauseName": "Supporting Process Plans", "severity": "medium", "reason": "Quality assurance and configuration management plans not found"}
                ]
                """;
    }

    private String getMockRecommendations() {
        return """
                [
                    {"priority": "high", "recommendation": "Add a comprehensive Technical Process Plans section (Clause 6) describing development methodology, testing approach, and deployment procedures.", "clauseRef": "6"},
                    {"priority": "high", "recommendation": "Include Control Plan (Clause 5.3) with details on project monitoring, change control, and progress tracking mechanisms.", "clauseRef": "5.3"},
                    {"priority": "medium", "recommendation": "Add Supporting Process Plans (Clause 7) covering quality assurance, configuration management, and documentation procedures.", "clauseRef": "7"},
                    {"priority": "medium", "recommendation": "Include a Definitions section (Clause 3) to clarify technical terms and project-specific terminology.", "clauseRef": "3"},
                    {"priority": "low", "recommendation": "Expand the References section with more relevant IEEE standards and project documentation.", "clauseRef": "2"}
                ]
                """;
    }
}
