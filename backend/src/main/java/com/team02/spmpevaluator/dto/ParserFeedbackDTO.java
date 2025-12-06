package com.team02.spmpevaluator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Parser Feedback responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParserFeedbackDTO {
    private Long id;
    private Long documentId;
    private String documentName;
    private Long parserConfigId;
    private String parserConfigName;
    private Double complianceScore;
    private String detectedClauses;
    private String missingClauses;
    private String recommendations;
    private String analysisReport;
    private String analyzedAt;
    private String parserVersion;
    private String status;
    private String errorMessage;
}
