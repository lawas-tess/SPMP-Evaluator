package com.team02.spmpevaluator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Snapshot of compliance scores to track re-evaluations and overrides.
 */
@Entity
@Table(name = "compliance_score_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private SPMPDocument document;

    @Column(nullable = false)
    private Double overallScore;

    @Column(nullable = false)
    private Double structureScore;

    @Column(nullable = false)
    private Double completenessScore;

    @Column(name = "sections_found")
    private Integer sectionsFound;

    @Column(name = "total_sections_required")
    private Integer totalSectionsRequired;

    @Column(name = "is_compliant")
    private boolean compliant;

    @Column(name = "professor_override")
    private Double professorOverride;

    @Column(name = "professor_notes", columnDefinition = "LONGTEXT")
    private String professorNotes;

    @Column(columnDefinition = "LONGTEXT")
    private String summary;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "recorded_by_user_id")
    private Long recordedByUserId;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(name = "source", length = 32)
    private String source; // AI_EVALUATION, RE_EVALUATION, OVERRIDE

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }
}
