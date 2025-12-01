package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.dto.ComplianceReportDTO;
import com.team02.spmpevaluator.dto.SectionAnalysisDTO;
import com.team02.spmpevaluator.entity.*;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import com.team02.spmpevaluator.repository.SectionAnalysisRepository;
import com.team02.spmpevaluator.util.DocumentParser;
import com.team02.spmpevaluator.util.IEEE1058StandardConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for evaluating SPMP documents against IEEE 1058 standard.
 * Performs compliance checking, section detection, and scoring.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ComplianceEvaluationService {

    private final DocumentParser documentParser;
    private final ComplianceScoreRepository complianceScoreRepository;
    private final SectionAnalysisRepository sectionAnalysisRepository;

    /**
     * Evaluates a document's compliance with IEEE 1058 standard.
     * Returns a detailed compliance report with section analysis and scoring.
     */
    public ComplianceScore evaluateDocument(SPMPDocument document, String documentContent) {
        String normalizedContent = documentContent.toLowerCase();

        // Analyze each IEEE 1058 section
        List<SectionAnalysis> sectionAnalyses = new ArrayList<>();
        int sectionsFound = 0;

        for (SectionAnalysis.IEEE1058Section section : SectionAnalysis.IEEE1058Section.values()) {
            SectionAnalysis analysis = analyzeSectionPresence(section, normalizedContent);
            sectionAnalyses.add(analysis);
            if (analysis.isPresent()) {
                sectionsFound++;
            }
        }

        // Calculate scores
        double completenessScore = calculateCompletenessScore(sectionsFound);
        double structureScore = calculateStructureScore(documentContent);
        double overallScore = (structureScore * IEEE1058StandardConstants.STRUCTURE_WEIGHT) +
                             (completenessScore * IEEE1058StandardConstants.COMPLETENESS_WEIGHT);

        // Create compliance score entity
        ComplianceScore complianceScore = new ComplianceScore();
        complianceScore.setDocument(document);
        complianceScore.setOverallScore(overallScore);
        complianceScore.setStructureScore(structureScore);
        complianceScore.setCompletenessScore(completenessScore);
        complianceScore.setSectionsFound(sectionsFound);
        complianceScore.setTotalSectionsRequired(SectionAnalysis.IEEE1058Section.values().length);
        complianceScore.setCompliant(overallScore >= (IEEE1058StandardConstants.MINIMUM_COMPLIANCE_THRESHOLD * 100));
        complianceScore.setSummary(generateSummary(overallScore, sectionsFound, documentContent.length()));
        complianceScore.setEvaluatedAt(LocalDateTime.now());

        // Save compliance score first
        complianceScore = complianceScoreRepository.save(complianceScore);

        // Save section analyses with reference to compliance score
        for (SectionAnalysis analysis : sectionAnalyses) {
            analysis.setComplianceScore(complianceScore);
            sectionAnalysisRepository.save(analysis);
        }

        complianceScore.setSectionAnalyses(sectionAnalyses);
        return complianceScore;
    }

    /**
     * Analyzes whether a specific IEEE 1058 section is present in the document.
     */
    private SectionAnalysis analyzeSectionPresence(SectionAnalysis.IEEE1058Section section, String content) {
        SectionAnalysis analysis = new SectionAnalysis();
        analysis.setSectionName(section);

        Set<String> keywords = getKeywordsForSection(section);
        boolean sectionPresent = documentParser.containsKeywords(content, keywords);

        analysis.setPresent(sectionPresent);
        analysis.setSectionScore(sectionPresent ? 100.0 : 0.0);

        if (sectionPresent) {
            analysis.setFindings("Section '" + section.getDisplayName() + "' detected in document.");
            analysis.setRecommendations("Continue ensuring comprehensive coverage of this section.");
        } else {
            analysis.setFindings("Section '" + section.getDisplayName() + "' not found in document.");
            analysis.setRecommendations("Add the '" + section.getDisplayName() +
                    "' section to comply with IEEE 1058 standard.");
        }

        return analysis;
    }

    /**
     * Maps section enum to its associated keywords.
     */
    private Set<String> getKeywordsForSection(SectionAnalysis.IEEE1058Section section) {
        return switch (section) {
            case OVERVIEW -> IEEE1058StandardConstants.OVERVIEW_KEYWORDS;
            case DOCUMENTATION_PLAN -> IEEE1058StandardConstants.DOCUMENTATION_PLAN_KEYWORDS;
            case MASTER_SCHEDULE -> IEEE1058StandardConstants.MASTER_SCHEDULE_KEYWORDS;
            case ORGANIZATION -> IEEE1058StandardConstants.ORGANIZATION_KEYWORDS;
            case STANDARDS_PRACTICES -> IEEE1058StandardConstants.STANDARDS_PRACTICES_KEYWORDS;
            case RISK_MANAGEMENT -> IEEE1058StandardConstants.RISK_MANAGEMENT_KEYWORDS;
            case STAFF_ORGANIZATION -> IEEE1058StandardConstants.STAFF_ORGANIZATION_KEYWORDS;
            case BUDGET_RESOURCE -> IEEE1058StandardConstants.BUDGET_RESOURCE_KEYWORDS;
            case REVIEWS_AUDITS -> IEEE1058StandardConstants.REVIEWS_AUDITS_KEYWORDS;
            case PROBLEM_RESOLUTION -> IEEE1058StandardConstants.PROBLEM_RESOLUTION_KEYWORDS;
            case CHANGE_MANAGEMENT -> IEEE1058StandardConstants.CHANGE_MANAGEMENT_KEYWORDS;
            case GLOSSARY_APPENDIX -> IEEE1058StandardConstants.GLOSSARY_APPENDIX_KEYWORDS;
        };
    }

    /**
     * Calculates completeness score based on sections found.
     */
    private double calculateCompletenessScore(int sectionsFound) {
        int totalSections = SectionAnalysis.IEEE1058Section.values().length;
        return (sectionsFound / (double) totalSections) * 100.0;
    }

    /**
     * Calculates structure score based on document characteristics.
     * Checks for proper formatting, length, and organization.
     */
    private double calculateStructureScore(String content) {
        double score = 0.0;
        int maxScore = 100;

        // Check minimum length (5000+ characters recommended)
        if (content.length() >= 5000) {
            score += 20;
        } else if (content.length() >= 3000) {
            score += 10;
        }

        // Check for headings/sections (usually capitalized lines)
        long headingCount = Arrays.stream(content.split("\n"))
                .filter(line -> line.matches("^[A-Z].*"))
                .count();
        if (headingCount >= 10) {
            score += 30;
        } else if (headingCount >= 5) {
            score += 15;
        }

        // Check for numbered lists or structure
        long bulletPoints = Arrays.stream(content.split("\n"))
                .filter(line -> line.matches("^\\s*[•\\-\\*].*") || line.matches("^\\s*\\d+\\..*"))
                .count();
        if (bulletPoints >= 20) {
            score += 30;
        } else if (bulletPoints >= 10) {
            score += 15;
        }

        // Check for proper table of contents indicators
        String lowerContent = content.toLowerCase();
        if (lowerContent.contains("table of contents") ||
            lowerContent.contains("contents") ||
            lowerContent.contains("index")) {
            score += 20;
        }

        return Math.min(score, maxScore);
    }

    /**
     * Generates a summary of the evaluation results.
     */
    private String generateSummary(double overallScore, int sectionsFound, int contentLength) {
        StringBuilder summary = new StringBuilder();
        summary.append("Compliance Evaluation Summary:\n");
        summary.append("Overall Compliance Score: ").append(String.format("%.2f%%", overallScore)).append("\n");
        summary.append("Sections Found: ").append(sectionsFound).append("/")
                .append(SectionAnalysis.IEEE1058Section.values().length).append("\n");
        summary.append("Document Length: ").append(contentLength).append(" characters\n");

        if (overallScore >= 90) {
            summary.append("Status: Excellent - Document is well-aligned with IEEE 1058 standard.\n");
        } else if (overallScore >= 80) {
            summary.append("Status: Good - Document meets IEEE 1058 requirements with minor improvements needed.\n");
        } else if (overallScore >= 70) {
            summary.append("Status: Acceptable - Document has significant compliance issues that need addressing.\n");
        } else {
            summary.append("Status: Poor - Document does not meet IEEE 1058 requirements. Major revisions needed.\n");
        }

        return summary.toString();
    }

    /**
     * Converts ComplianceScore entity to DTO for API responses.
     */
    public ComplianceReportDTO convertToDTO(ComplianceScore complianceScore) {
        ComplianceReportDTO dto = new ComplianceReportDTO();
        dto.setDocumentId(complianceScore.getDocument().getId());
        dto.setDocumentName(complianceScore.getDocument().getFileName());
        dto.setOverallScore(complianceScore.getOverallScore());
        dto.setStructureScore(complianceScore.getStructureScore());
        dto.setCompletenessScore(complianceScore.getCompletenessScore());
        dto.setSectionsFound(complianceScore.getSectionsFound());
        dto.setTotalSectionsRequired(complianceScore.getTotalSectionsRequired());
        dto.setCompliant(complianceScore.isCompliant());
        dto.setSummary(complianceScore.getSummary());
        dto.setEvaluatedAt(complianceScore.getEvaluatedAt());
        dto.setProfessorOverride(complianceScore.getProfessorOverride());
        dto.setProfessorNotes(complianceScore.getProfessorNotes());

        // Convert section analyses
        List<SectionAnalysisDTO> sectionDTOs = new ArrayList<>();
        if (complianceScore.getSectionAnalyses() != null) {
            for (SectionAnalysis analysis : complianceScore.getSectionAnalyses()) {
                SectionAnalysisDTO sectionDTO = new SectionAnalysisDTO();
                sectionDTO.setId(analysis.getId());
                sectionDTO.setSectionName(analysis.getSectionName().getDisplayName());
                sectionDTO.setPresent(analysis.isPresent());
                sectionDTO.setSectionScore(analysis.getSectionScore());
                sectionDTO.setFindings(analysis.getFindings());
                sectionDTO.setRecommendations(analysis.getRecommendations());
                sectionDTO.setPageNumber(analysis.getPageNumber());
                sectionDTOs.add(sectionDTO);
            }
        }
        dto.setSectionAnalyses(sectionDTOs);

        return dto;
    }
}
