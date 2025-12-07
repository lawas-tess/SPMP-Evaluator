package com.team02.spmpevaluator.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComplianceScoreHistoryDTO {
    private Long id;
    private Double overallScore;
    private Double structureScore;
    private Double completenessScore;
    private Integer sectionsFound;
    private Integer totalSectionsRequired;
    private boolean compliant;
    private Double professorOverride;
    private String professorNotes;
    private String summary;
    private LocalDateTime evaluatedAt;
    private LocalDateTime recordedAt;
    private Integer versionNumber;
    private String source;
}
