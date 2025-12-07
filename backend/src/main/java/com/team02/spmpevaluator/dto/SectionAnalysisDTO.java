package com.team02.spmpevaluator.dto;

import com.team02.spmpevaluator.entity.SectionAnalysis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionAnalysisDTO {
    private Long id;
    private String sectionName;
    private boolean present;
    private Double sectionScore;
    private String findings;
    private String recommendations;
    private Integer pageNumber;
    private Double coverage;
    private String severity;
    private String evidenceSnippet;
    private List<String> missingSubclauses;
    private Integer sectionWeight;
}
