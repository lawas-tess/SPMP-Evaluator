package com.team02.spmpevaluator.dto;

import com.team02.spmpevaluator.entity.SectionAnalysis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceReportDTO {
    private Long documentId;
    private String documentName;
    private Double overallScore;
    private Double structureScore;
    private Double completenessScore;
    private Integer sectionsFound;
    private Integer totalSectionsRequired;
    private boolean compliant;
    private String summary;
    private List<SectionAnalysisDTO> sectionAnalyses;
    private LocalDateTime evaluatedAt;
    private Double professorOverride;
    private String professorNotes;
}
