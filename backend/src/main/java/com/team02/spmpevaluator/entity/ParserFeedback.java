package com.team02.spmpevaluator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing AI parser feedback for uploaded SPMP documents.
 * Stores compliance analysis, missing clauses, and recommendations.
 */
@Entity
@Table(name = "parser_feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParserFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private SPMPDocument document;

    @ManyToOne
    @JoinColumn(name = "parser_config_id")
    private ParserConfiguration parserConfiguration;

    /**
     * Overall compliance score (0-100)
     */
    @Column(nullable = false)
    private Double complianceScore;

    /**
     * Detected IEEE 1058 clauses with their scores
     * Format: [{"clauseId": "1.1", "clauseName": "Purpose", "score": 85, "found": true, "location": "page 2"}]
     */
    @Column(columnDefinition = "TEXT")
    private String detectedClauses;

    /**
     * Missing or incomplete clauses
     * Format: [{"clauseId": "2.3", "clauseName": "Risk Management", "severity": "high"}]
     */
    @Column(columnDefinition = "TEXT")
    private String missingClauses;

    /**
     * AI-generated recommendations for improvement
     * Format: [{"priority": "high", "recommendation": "Add risk management section...", "clauseRef": "2.3"}]
     */
    @Column(columnDefinition = "TEXT")
    private String recommendations;

    /**
     * Detailed analysis report
     */
    @Column(columnDefinition = "TEXT")
    private String analysisReport;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    /**
     * Parser version or AI model used
     */
    private String parserVersion;

    /**
     * Processing status: PENDING, IN_PROGRESS, COMPLETED, FAILED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status = FeedbackStatus.PENDING;

    @Column(length = 1000)
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        if (analyzedAt == null) {
            analyzedAt = LocalDateTime.now();
        }
    }

    public enum FeedbackStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}
