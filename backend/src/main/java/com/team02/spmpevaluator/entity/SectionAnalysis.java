package com.team02.spmpevaluator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "section_analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SectionAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compliance_score_id", nullable = false)
    @JsonIgnore
    private ComplianceScore complianceScore;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IEEE1058Section sectionName;

    @Column(nullable = false)
    private boolean present; // True if section found in document

    @Column(nullable = false)
    private Double sectionScore; // Individual section score

    @Column(columnDefinition = "LONGTEXT")
    private String findings; // Detailed findings for this section

    @Column(columnDefinition = "LONGTEXT")
    private String recommendations; // Recommendations for improvement

    private Integer pageNumber; // Page where section starts (if found)

    @Column
    private Double coverage; // Percentage keyword coverage for this section

    @Column(length = 24)
    private String severity; // HIGH, MEDIUM, INFO based on coverage/presence

    @Column(columnDefinition = "LONGTEXT")
    private String evidenceSnippet; // First matched snippet for context

    @Column(columnDefinition = "LONGTEXT")
    private String missingSubclauses; // Comma-separated list of missing subclauses

    @Column
    private Integer sectionWeight; // Applied weight used during scoring

    public enum IEEE1058Section {
        OVERVIEW("Project Overview"),
        DOCUMENTATION_PLAN("Documentation Plan"),
        MASTER_SCHEDULE("Master Schedule"),
        ORGANIZATION("Project Organization"),
        STANDARDS_PRACTICES("Standards, Practices, and Conventions"),
        RISK_MANAGEMENT("Risk Management"),
        STAFF_ORGANIZATION("Staff Organization"),
        BUDGET_RESOURCE("Budget and Resource Planning"),
        REVIEWS_AUDITS("Reviews and Audits"),
        PROBLEM_RESOLUTION("Problem Resolution"),
        CHANGE_MANAGEMENT("Change Management"),
        GLOSSARY_APPENDIX("Glossary and Appendices");

        private final String displayName;

        IEEE1058Section(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
