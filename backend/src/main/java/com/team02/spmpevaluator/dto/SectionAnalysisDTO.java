package com.team02.spmpevaluator.dto;

import com.team02.spmpevaluator.entity.SectionAnalysis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
