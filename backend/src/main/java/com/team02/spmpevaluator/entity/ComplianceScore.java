package com.team02.spmpevaluator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "compliance_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ComplianceScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    @JsonIgnore
    private SPMPDocument document;

    @Column(nullable = false)
    private Double overallScore; // 0-100

    @Column(nullable = false)
    private Double structureScore; // Score for document structure compliance

    @Column(nullable = false)
    private Double completenessScore; // Score for required sections presence

    @Column(nullable = false)
    private Integer totalSectionsRequired = 11; // IEEE 1058 sections

    @Column(nullable = false)
    private Integer sectionsFound = 0;

    @Column(name = "is_compliant")
    private boolean compliant; // True if passes 80% threshold

    @Column(columnDefinition = "LONGTEXT")
    private String summary;

    @OneToMany(mappedBy = "complianceScore", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"complianceScore"})
    private List<SectionAnalysis> sectionAnalyses;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

    @Column(name = "professor_override")
    private Double professorOverride; // Professor's manual score override

    @Column(name = "professor_notes", columnDefinition = "LONGTEXT")
    private String professorNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_user_id")
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private User reviewedBy; // Professor who reviewed

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @PrePersist
    protected void onCreate() {
        evaluatedAt = LocalDateTime.now();
    }
}
