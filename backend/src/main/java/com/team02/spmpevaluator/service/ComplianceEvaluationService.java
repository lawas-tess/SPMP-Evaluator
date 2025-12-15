package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.dto.ComplianceReportDTO;
import com.team02.spmpevaluator.dto.SectionAnalysisDTO;
import com.team02.spmpevaluator.entity.*;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import com.team02.spmpevaluator.util.IEEE1058StandardConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.*;

/**
 * Service for evaluating SPMP documents against IEEE 1058 standard.
 * Performs compliance checking, section detection, and scoring.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ComplianceEvaluationService {

    private final ComplianceScoreRepository complianceScoreRepository;

    private static final Map<SectionAnalysis.IEEE1058Section, Integer> SECTION_WEIGHTS = Map.ofEntries(
            Map.entry(SectionAnalysis.IEEE1058Section.OVERVIEW, 10),
            Map.entry(SectionAnalysis.IEEE1058Section.DOCUMENTATION_PLAN, 8),
            Map.entry(SectionAnalysis.IEEE1058Section.MASTER_SCHEDULE, 10),
            Map.entry(SectionAnalysis.IEEE1058Section.ORGANIZATION, 12),
            Map.entry(SectionAnalysis.IEEE1058Section.STANDARDS_PRACTICES, 10),
            Map.entry(SectionAnalysis.IEEE1058Section.RISK_MANAGEMENT, 10),
            Map.entry(SectionAnalysis.IEEE1058Section.STAFF_ORGANIZATION, 8),
            Map.entry(SectionAnalysis.IEEE1058Section.BUDGET_RESOURCE, 10),
            Map.entry(SectionAnalysis.IEEE1058Section.REVIEWS_AUDITS, 7),
            Map.entry(SectionAnalysis.IEEE1058Section.PROBLEM_RESOLUTION, 5),
            Map.entry(SectionAnalysis.IEEE1058Section.CHANGE_MANAGEMENT, 5),
            Map.entry(SectionAnalysis.IEEE1058Section.GLOSSARY_APPENDIX, 5)
    );

    private record SubclauseDefinition(String id, String title, Set<String> keywords, int weight) {}

    private static final Map<SectionAnalysis.IEEE1058Section, List<SubclauseDefinition>> SUBCLAUSE_DEFINITIONS =
            buildSubclauseDefinitions();

    /**
     * Evaluates a document's compliance with IEEE 1058 standard.
     * Returns a detailed compliance report with section analysis and scoring.
     * Uses WEIGHTED SCORING to ensure IEEE 1058 compliance integrity.
     */
    public ComplianceScore evaluateDocument(SPMPDocument document, String documentContent) {
        String normalizedContent = documentContent.toLowerCase();

        // Analyze each IEEE 1058 section
        List<SectionAnalysis> sectionAnalyses = new ArrayList<>();
        int sectionsFound = 0;

        for (SectionAnalysis.IEEE1058Section section : SectionAnalysis.IEEE1058Section.values()) {
            SectionAnalysis analysis = analyzeSectionPresence(section, normalizedContent, documentContent);
            sectionAnalyses.add(analysis);
            if (analysis.isPresent()) {
                sectionsFound++;
            }
        }

        // CRITICAL FIX: Calculate weighted overall score from section scores
        // Each section contributes its score * weight to the final score
        double overallScore = calculateWeightedOverallScore(sectionAnalyses);
        
        // Calculate diagnostic scores (for backward compatibility, not used in final score)
        double completenessScore = calculateCompletenessScore(sectionsFound);
        double structureScore = calculateStructureScore(documentContent);

        // Reuse existing compliance score to support re-evaluation
        ComplianceScore complianceScore = complianceScoreRepository.findByDocument(document)
                .orElseGet(() -> {
                    ComplianceScore newScore = new ComplianceScore();
                    newScore.setSectionAnalyses(new ArrayList<>());
                    return newScore;
                });

        // Clear previous section analyses (orphanRemoval will delete them)
        complianceScore.getSectionAnalyses().clear();

        complianceScore.setDocument(document);
        complianceScore.setOverallScore(overallScore);
        complianceScore.setStructureScore(structureScore);
        complianceScore.setCompletenessScore(completenessScore);
        complianceScore.setSectionsFound(sectionsFound);
        complianceScore.setTotalSectionsRequired(SectionAnalysis.IEEE1058Section.values().length);
        complianceScore.setCompliant(overallScore >= (IEEE1058StandardConstants.MINIMUM_COMPLIANCE_THRESHOLD * 100));
        complianceScore.setSummary(generateSummary(overallScore, sectionsFound, documentContent.length()));
        complianceScore.setEvaluatedAt(LocalDateTime.now());

        // Add new section analyses to the collection
        for (SectionAnalysis analysis : sectionAnalyses) {
            analysis.setComplianceScore(complianceScore);
            complianceScore.getSectionAnalyses().add(analysis);
        }

        // Save compliance score (cascade will save section analyses)
        complianceScore = complianceScoreRepository.save(complianceScore);
        
        return complianceScore;
    }

    /**
     * Analyzes whether a specific IEEE 1058 section is present in the document.
     * CRITICAL FIX: Now requires BOTH keywords AND sufficient content length to prevent false positives.
     */
    private SectionAnalysis analyzeSectionPresence(SectionAnalysis.IEEE1058Section section, String normalizedContent, String originalContent) {
        SectionAnalysis analysis = new SectionAnalysis();
        analysis.setSectionName(section);

        Set<String> keywords = getKeywordsForSection(section);
        int matchedKeywords = (int) keywords.stream()
                .filter(kw -> normalizedContent.contains(kw.toLowerCase()))
                .count();

        // CRITICAL FIX: Check if section has dedicated heading/structure
        boolean hasSectionHeading = detectSectionHeading(section, originalContent);
        
        // CRITICAL FIX: Extract section content for analysis
        String sectionContent = extractSectionContent(section, originalContent);
        int sectionContentLength = sectionContent.length();

        double primaryCoverage = keywords.isEmpty() ? 0.0 : (matchedKeywords / (double) keywords.size()) * 100.0;

        SubclauseResult subclauseResult = evaluateSubclauses(section, normalizedContent, originalContent);
        
        // BALANCED FIX: Section is present if it has reasonable keyword coverage OR structural evidence
        // This prevents false negatives for well-written SPMPs that use different heading formats
        // Requires EITHER:
        //   - Good keyword match (40%+ coverage)
        //   - Section heading detected with some keywords
        //   - Subclause evidence with keywords
        boolean hasGoodKeywordCoverage = primaryCoverage >= 40.0;
        boolean hasStructuralEvidence = hasSectionHeading && matchedKeywords >= 1;
        boolean hasSubclauseEvidence = !subclauseResult.missingSubclauses().isEmpty() && matchedKeywords >= 1;
        
        boolean sectionPresent = hasGoodKeywordCoverage || hasStructuralEvidence || hasSubclauseEvidence;

        double combinedCoverage = SUBCLAUSE_DEFINITIONS.getOrDefault(section, Collections.emptyList()).isEmpty()
                ? primaryCoverage
                : (primaryCoverage * 0.5) + (subclauseResult.coveragePct() * 0.5);

        // CRITICAL FIX: Apply length-based penalty to coverage
        double lengthPenalty = calculateLengthPenalty(sectionContentLength, getMinimumSectionLength(section));
        combinedCoverage = combinedCoverage * lengthPenalty;

        // CRITICAL FIX: Section score now properly penalizes missing/incomplete sections
        double sectionScore = computeSectionScore(combinedCoverage, sectionPresent, matchedKeywords, keywords.size());

        analysis.setPresent(sectionPresent);
        analysis.setSectionScore(sectionScore);
        analysis.setCoverage(combinedCoverage);
        analysis.setSeverity(resolveSeverity(sectionPresent, combinedCoverage, subclauseResult.missingSubclauses()));
        analysis.setEvidenceSnippet(Optional.ofNullable(subclauseResult.evidenceSnippet())
                .orElseGet(() -> extractEvidenceSnippet(originalContent, keywords)));
        analysis.setMissingSubclauses(String.join(", ", subclauseResult.missingSubclauses()));
        analysis.setSectionWeight(SECTION_WEIGHTS.getOrDefault(section, 0));

        String findings = buildFindings(section, sectionPresent, matchedKeywords, keywords.size(), combinedCoverage,
                subclauseResult);
        String recommendations = buildRecommendations(section, sectionPresent, combinedCoverage, subclauseResult);

        analysis.setFindings(findings);
        analysis.setRecommendations(recommendations);

        return analysis;
    }

    /**
     * BALANCED: Computes section score with realistic grading for professor-approved SPMPs.
     * Missing sections receive 0%. Present sections receive proportional scores based on coverage.
     * Scoring curve adjusted to match academic grading standards.
     */
    private double computeSectionScore(double combinedCoverage, boolean sectionPresent, int matchedKeywords, int totalKeywords) {
        // If section is completely missing, return 0
        if (!sectionPresent) {
            return 0.0;
        }
        
        // Calculate keyword completeness ratio
        double keywordRatio = totalKeywords > 0 ? (matchedKeywords / (double) totalKeywords) : 0.0;
        
        // Award points based on coverage - more generous curve for quality content
        double score;
        if (combinedCoverage >= 75.0) {
            // Excellent: High coverage = 85-100% score
            score = 70.0 + (combinedCoverage * 0.35);
        } else if (combinedCoverage >= 60.0) {
            // Good: Solid coverage = 75-90% score  
            score = 60.0 + (combinedCoverage * 0.40);
        } else if (combinedCoverage >= 40.0) {
            // Fair: Adequate coverage = 60-80% score
            score = 45.0 + (combinedCoverage * 0.50);
        } else if (combinedCoverage >= 25.0) {
            // Passing: Minimal coverage = 50-65% score
            score = 35.0 + (combinedCoverage * 0.60);
        } else {
            // Poor: Very low coverage = 0-40% score
            score = combinedCoverage * 1.2;
        }
        
        // Small bonus for high keyword matching
        if (keywordRatio >= 0.7) {
            score += 5.0;
        } else if (keywordRatio >= 0.5) {
            score += 3.0;
        }
        
        return Math.min(score, 100.0);
    }

    /**
     * CRITICAL FIX: Calculates weighted overall score based on IEEE 1058 section weights.
     * This is the TRUE quality score, not an unweighted average.
     * 
     * Formula: Σ(section_score × section_weight) / 100
     * 
     * Example:
     * - Section A: score=98%, weight=10 → contribution = 98 * 0.10 = 9.8
     * - Section B: score=0%, weight=12 → contribution = 0 * 0.12 = 0.0
     * - Total weighted score = sum of all contributions
     * 
     * This ensures high-weight sections (Organization=12%, Risk=10%) impact the score
     * significantly more than low-weight sections (Glossary=5%, Problem Resolution=5%).
     */
    private double calculateWeightedOverallScore(List<SectionAnalysis> sectionAnalyses) {
        double totalWeightedScore = 0.0;
        int totalWeight = 0;
        
        for (SectionAnalysis analysis : sectionAnalyses) {
            int sectionWeight = analysis.getSectionWeight();
            double sectionScore = analysis.getSectionScore();
            
            // Each section contributes: (score / 100) * weight
            // Example: 98% score with 10 weight = 0.98 * 10 = 9.8 points
            totalWeightedScore += (sectionScore / 100.0) * sectionWeight;
            totalWeight += sectionWeight;
        }
        
        // Validate weights sum to 100 (fail-safe check)
        if (totalWeight != 100) {
            throw new IllegalStateException(
                String.format("Section weights must sum to 100, but got %d. Check SECTION_WEIGHTS configuration.", totalWeight)
            );
        }
        
        // Return as percentage (0-100)
        return totalWeightedScore;
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
     * CRITICAL FIX: Calculates completeness score with weighted penalties.
     * Missing critical sections (high weight) cause larger score reductions.
     */
    private double calculateCompletenessScore(int sectionsFound) {
        int totalSections = SectionAnalysis.IEEE1058Section.values().length;
        
        // Base completeness ratio
        double baseScore = (sectionsFound / (double) totalSections) * 100.0;
        
        // Apply penalties for incomplete documents
        if (sectionsFound < totalSections * 0.5) {
            // Less than 50% complete - severe penalty
            baseScore = baseScore * 0.6;
        } else if (sectionsFound < totalSections * 0.75) {
            // Less than 75% complete - moderate penalty
            baseScore = baseScore * 0.8;
        }
        
        return baseScore;
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
        summary.append("Overall Compliance Score: ").append(Math.round(overallScore)).append("%\n");
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
     * Overloaded method that accepts document info to avoid lazy loading issues.
     */
    public ComplianceReportDTO convertToDTO(ComplianceScore complianceScore, Long documentId, String documentName) {
        ComplianceReportDTO dto = new ComplianceReportDTO();
        dto.setDocumentId(documentId);
        dto.setDocumentName(documentName);
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
                sectionDTO.setCoverage(analysis.getCoverage());
                sectionDTO.setSeverity(analysis.getSeverity());
                sectionDTO.setEvidenceSnippet(analysis.getEvidenceSnippet());
                sectionDTO.setMissingSubclauses(parseMissingSubclauses(analysis.getMissingSubclauses()));
                sectionDTO.setSectionWeight(analysis.getSectionWeight());
                sectionDTOs.add(sectionDTO);
            }
        }
        dto.setSectionAnalyses(sectionDTOs);

        return dto;
    }

    private List<String> parseMissingSubclauses(String missing) {
        if (missing == null || missing.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(missing.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private SubclauseResult evaluateSubclauses(SectionAnalysis.IEEE1058Section section, String normalizedContent, String originalContent) {
        List<SubclauseDefinition> definitions = SUBCLAUSE_DEFINITIONS.getOrDefault(section, Collections.emptyList());
        if (definitions.isEmpty()) {
            return new SubclauseResult(0.0, Collections.emptyList(), null);
        }

        double totalCoverage = 0.0;
        List<String> missing = new ArrayList<>();
        String evidenceSnippet = null;

        for (SubclauseDefinition def : definitions) {
            int matchedKeywords = (int) def.keywords().stream()
                    .filter(kw -> normalizedContent.contains(kw.toLowerCase()))
                    .count();
            double coveragePct = def.keywords().isEmpty() ? 0.0 : (matchedKeywords / (double) def.keywords().size()) * 100.0;
            totalCoverage += coveragePct;

            if (matchedKeywords == 0) {
                missing.add(def.id() + " " + def.title());
            } else if (evidenceSnippet == null) {
                evidenceSnippet = extractEvidenceSnippet(originalContent, def.keywords());
            }
        }

        double avgCoverage = totalCoverage / definitions.size();
        return new SubclauseResult(avgCoverage, missing, evidenceSnippet);
    }

    private String buildFindings(SectionAnalysis.IEEE1058Section section,
                                 boolean present,
                                 int matchedKeywords,
                                 int keywordPool,
                                 double combinedCoverage,
                                 SubclauseResult subclauseResult) {
        if (!present) {
            return "Section '" + section.getDisplayName() + "' not detected in the document.";
        }

        StringBuilder findings = new StringBuilder();
        findings.append("Section '").append(section.getDisplayName()).append("' detected. ");
        findings.append(String.format("Coverage: %.0f%% of keywords matched (%d/%d). ", combinedCoverage, matchedKeywords, keywordPool));

        if (!subclauseResult.missingSubclauses().isEmpty()) {
            findings.append("Missing subclauses: ");
            findings.append(String.join(", ", subclauseResult.missingSubclauses()));
            findings.append(".");
        } else {
            findings.append("All mapped subclauses detected.");
        }
        return findings.toString();
    }

    private String buildRecommendations(SectionAnalysis.IEEE1058Section section,
                                        boolean present,
                                        double coverage,
                                        SubclauseResult subclauseResult) {
        if (!present) {
            return "Add a dedicated '" + section.getDisplayName() + "' section. Include the key subclauses: " +
                    String.join(", ", listSubclauseTitles(section)) + ".";
        }

        if (!subclauseResult.missingSubclauses().isEmpty()) {
            return "Address missing subclauses: " + String.join(", ", subclauseResult.missingSubclauses()) +
                    ". Provide concrete details aligned with IEEE 1058 expectations.";
        }

        if (coverage < 75) {
            return "Strengthen this section with quantitative details, ownership, and acceptance criteria. " +
                    sectionSpecificTip(section);
        }

        return "Section is present. Consider tightening clarity, cross-references, and evidence of execution.";
    }

    private String resolveSeverity(boolean present, double coverage, List<String> missingSubclauses) {
        if (!present) {
            return "HIGH";
        }
        if (!missingSubclauses.isEmpty()) {
            return "HIGH";
        }
        if (coverage < 60) {
            return "MEDIUM";
        }
        if (coverage < 80) {
            return "LOW";
        }
        return "INFO";
    }

    private List<String> listSubclauseTitles(SectionAnalysis.IEEE1058Section section) {
        return SUBCLAUSE_DEFINITIONS.getOrDefault(section, Collections.emptyList()).stream()
                .map(def -> def.id() + " " + def.title())
                .toList();
    }

    private String extractEvidenceSnippet(String originalContent, Set<String> keywords) {
        if (originalContent == null || originalContent.isBlank() || keywords.isEmpty()) {
            return null;
        }
        for (String line : originalContent.split("\n")) {
            String lower = line.toLowerCase();
            if (keywords.stream().anyMatch(lower::contains)) {
                String trimmed = line.trim();
                return trimmed.length() > 240 ? trimmed.substring(0, 240) + "..." : trimmed;
            }
        }
        return null;
    }

    private String sectionSpecificTip(SectionAnalysis.IEEE1058Section section) {
        return switch (section) {
            case RISK_MANAGEMENT -> "Include risk register with probability/impact, owners, and mitigation actions.";
            case MASTER_SCHEDULE -> "Show milestones, dependencies, and critical path dates.";
            case ORGANIZATION -> "Add RACI or responsibility matrix with escalation paths.";
            case DOCUMENTATION_PLAN -> "List required deliverables, formats, and review/approval cycles.";
            case STANDARDS_PRACTICES -> "Cite the exact standards, coding conventions, and QA gates applied.";
            case BUDGET_RESOURCE -> "Provide estimates, allocations, and burn-rate assumptions.";
            case REVIEWS_AUDITS -> "Describe review cadence, participants, and entry/exit criteria.";
            case PROBLEM_RESOLUTION -> "Explain escalation paths, SLAs, and defect management workflow.";
            case CHANGE_MANAGEMENT -> "Document change control board, request workflow, and impact analysis.";
            case STAFF_ORGANIZATION -> "List roles, skills, onboarding plans, and backups.";
            case GLOSSARY_APPENDIX -> "Add key terms, abbreviations, and referenced documents.";
            case OVERVIEW -> "Clarify objectives, scope, and success criteria.";
        };
    }

    private static Map<SectionAnalysis.IEEE1058Section, List<SubclauseDefinition>> buildSubclauseDefinitions() {
        Map<SectionAnalysis.IEEE1058Section, List<SubclauseDefinition>> map = new EnumMap<>(SectionAnalysis.IEEE1058Section.class);

        map.put(SectionAnalysis.IEEE1058Section.OVERVIEW, List.of(
                new SubclauseDefinition("1.1", "Purpose & Scope", Set.of("purpose", "scope", "objective"), 5),
                new SubclauseDefinition("1.2", "Assumptions & Constraints", Set.of("assumption", "constraint", "dependency"), 5)
        ));

        map.put(SectionAnalysis.IEEE1058Section.DOCUMENTATION_PLAN, List.of(
                new SubclauseDefinition("2.1", "Deliverables", Set.of("deliverable", "artifact", "submission"), 4),
                new SubclauseDefinition("2.2", "Standards & Templates", Set.of("template", "standard", "format"), 4),
                new SubclauseDefinition("2.3", "Review & Approval", Set.of("review", "approval", "sign-off"), 4)
        ));

        map.put(SectionAnalysis.IEEE1058Section.MASTER_SCHEDULE, List.of(
                new SubclauseDefinition("3.1", "Milestones", Set.of("milestone", "phase", "checkpoint"), 5),
                new SubclauseDefinition("3.2", "Dependencies", Set.of("dependency", "precedence", "critical path"), 5),
                new SubclauseDefinition("3.3", "Resource Loading", Set.of("resource loading", "allocation", "capacity"), 5)
        ));

        map.put(SectionAnalysis.IEEE1058Section.ORGANIZATION, List.of(
                new SubclauseDefinition("4.1", "Structure", Set.of("organization", "org chart", "structure"), 4),
                new SubclauseDefinition("4.2", "Roles & Responsibilities", Set.of("role", "responsibility", "raci"), 4),
                new SubclauseDefinition("4.3", "Communication", Set.of("communication", "escalation", "meeting"), 3)
        ));

        map.put(SectionAnalysis.IEEE1058Section.STANDARDS_PRACTICES, List.of(
                new SubclauseDefinition("5.1", "Engineering Standards", Set.of("standard", "coding", "design"), 4),
                new SubclauseDefinition("5.2", "Process & QA", Set.of("process", "qa", "review"), 4),
                new SubclauseDefinition("5.3", "Tools", Set.of("tool", "automation", "platform"), 2)
        ));

        map.put(SectionAnalysis.IEEE1058Section.RISK_MANAGEMENT, List.of(
                new SubclauseDefinition("6.1", "Risk Register", Set.of("risk", "register", "probability"), 4),
                new SubclauseDefinition("6.2", "Mitigation", Set.of("mitigation", "response", "contingency"), 4),
                new SubclauseDefinition("6.3", "Monitoring", Set.of("monitor", "threshold", "trigger"), 2)
        ));

        map.put(SectionAnalysis.IEEE1058Section.STAFF_ORGANIZATION, List.of(
                new SubclauseDefinition("7.1", "Team Composition", Set.of("team", "staffing", "resource"), 3),
                new SubclauseDefinition("7.2", "Roles & Skills", Set.of("skill", "role", "assignment"), 3),
                new SubclauseDefinition("7.3", "Training", Set.of("training", "onboarding", "upskilling"), 2)
        ));

        map.put(SectionAnalysis.IEEE1058Section.BUDGET_RESOURCE, List.of(
                new SubclauseDefinition("8.1", "Estimates", Set.of("estimate", "budget", "cost"), 4),
                new SubclauseDefinition("8.2", "Allocation", Set.of("allocation", "fund", "resource"), 3),
                new SubclauseDefinition("8.3", "Tracking", Set.of("burn", "variance", "tracking"), 3)
        ));

        map.put(SectionAnalysis.IEEE1058Section.REVIEWS_AUDITS, List.of(
                new SubclauseDefinition("9.1", "Review Cadence", Set.of("review", "inspection", "audit"), 4),
                new SubclauseDefinition("9.2", "Entry/Exit Criteria", Set.of("entry", "exit", "criteria"), 3),
                new SubclauseDefinition("9.3", "Findings Handling", Set.of("finding", "defect", "action"), 3)
        ));

        map.put(SectionAnalysis.IEEE1058Section.PROBLEM_RESOLUTION, List.of(
                new SubclauseDefinition("10.1", "Escalation", Set.of("escalation", "tier", "support"), 3),
                new SubclauseDefinition("10.2", "SLA & Response", Set.of("sla", "response", "turnaround"), 3),
                new SubclauseDefinition("10.3", "Tracking", Set.of("ticket", "issue", "tracking"), 2)
        ));

        map.put(SectionAnalysis.IEEE1058Section.CHANGE_MANAGEMENT, List.of(
                new SubclauseDefinition("11.1", "Change Control Board", Set.of("ccb", "board", "governance"), 3),
                new SubclauseDefinition("11.2", "Workflow", Set.of("change request", "workflow", "approval"), 4),
                new SubclauseDefinition("11.3", "Impact Analysis", Set.of("impact", "analysis", "assessment"), 3)
        ));

        map.put(SectionAnalysis.IEEE1058Section.GLOSSARY_APPENDIX, List.of(
                new SubclauseDefinition("12.1", "Glossary", Set.of("glossary", "definition", "term"), 4),
                new SubclauseDefinition("12.2", "References", Set.of("reference", "bibliography", "citation"), 3),
                new SubclauseDefinition("12.3", "Appendices", Set.of("appendix", "appendices", "annex"), 3)
        ));

        return map;
    }

    /**
     * IMPROVED: Detects if a section has a proper heading/structure in the document.
     * Now recognizes multiple heading formats used in real SPMP documents.
     */
    private boolean detectSectionHeading(SectionAnalysis.IEEE1058Section section, String content) {
        String sectionTitle = section.getDisplayName().toLowerCase();
        String[] titleWords = sectionTitle.split("\\s+");
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            String trimmedLine = line.trim().toLowerCase();
            
            // Skip very long lines (likely not headings)
            if (trimmedLine.length() > 200) {
                continue;
            }
            
            // Check for exact section title
            if (trimmedLine.contains(sectionTitle)) {
                return true;
            }
            
            // Check for partial title match (e.g., "risk management" in "5.3.7 Risk management plan")
            int matchedWords = 0;
            for (String word : titleWords) {
                if (trimmedLine.contains(word)) {
                    matchedWords++;
                }
            }
            if (matchedWords >= Math.max(1, titleWords.length - 1)) {
                return true;
            }
            
            // Check for numbered headings (e.g., "1. Overview", "5.3.7 Risk management")
            if (trimmedLine.matches("^\\d+(\\.\\d+)*\\s+.*")) {
                for (String word : titleWords) {
                    if (trimmedLine.contains(word)) {
                        return true;
                    }
                }
            }
            
            // Check for headings with primary section keywords
            Set<String> keywords = getKeywordsForSection(section);
            for (String keyword : keywords) {
                if (keyword.length() >= 4 && trimmedLine.contains(keyword.toLowerCase()) && trimmedLine.length() < 150) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * IMPROVED: Extracts content belonging to a specific section.
     * More flexible to handle various document formats and nested sections.
     */
    private String extractSectionContent(SectionAnalysis.IEEE1058Section section, String content) {
        String sectionTitle = section.getDisplayName().toLowerCase();
        String[] titleWords = sectionTitle.split("\\s+");
        String[] lines = content.split("\n");
        StringBuilder sectionContent = new StringBuilder();
        boolean inSection = false;
        int consecutiveEmptyLines = 0;
        int contentLinesFound = 0;
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            String lowerLine = trimmedLine.toLowerCase();
            
            // Start capturing when section heading found (more flexible matching)
            if (!inSection) {
                boolean matchesTitle = lowerLine.contains(sectionTitle);
                
                // Check if line contains most title words
                int matchedWords = 0;
                for (String word : titleWords) {
                    if (lowerLine.contains(word)) {
                        matchedWords++;
                    }
                }
                boolean matchesPartialTitle = matchedWords >= Math.max(1, titleWords.length - 1);
                
                if (matchesTitle || matchesPartialTitle || matchesAnySectionKeyword(section, lowerLine)) {
                    inSection = true;
                    continue;
                }
            }
            
            // Capture content while in section
            if (inSection) {
                // Stop if we hit a major section heading (but be less aggressive)
                if (looksLikeSectionHeading(lowerLine) && !matchesAnySectionKeyword(section, lowerLine)) {
                    // Allow some subsection headings, but stop at major section changes
                    if (trimmedLine.matches("^\\d+\\.?\\s+[A-Z].*") || trimmedLine.matches("^[A-Z][A-Z\\s]{10,}$")) {
                        consecutiveEmptyLines++;
                        if (consecutiveEmptyLines > 1 || contentLinesFound > 10) {
                            break;
                        }
                    }
                }
                
                if (trimmedLine.isEmpty()) {
                    consecutiveEmptyLines++;
                    // Stop after many empty lines (end of section)
                    if (consecutiveEmptyLines > 3 && contentLinesFound > 5) {
                        break;
                    }
                } else {
                    consecutiveEmptyLines = 0;
                    contentLinesFound++;
                }
                
                sectionContent.append(line).append("\n");
            }
        }
        
        // If we didn't find content by heading, search by keywords
        if (sectionContent.length() < 100) {
            Set<String> keywords = getKeywordsForSection(section);
            StringBuilder fallbackContent = new StringBuilder();
            
            for (String line : lines) {
                String lowerLine = line.toLowerCase();
                for (String keyword : keywords) {
                    if (lowerLine.contains(keyword.toLowerCase())) {
                        fallbackContent.append(line).append("\n");
                        break;
                    }
                }
            }
            
            if (fallbackContent.length() > sectionContent.length()) {
                return fallbackContent.toString();
            }
        }
        
        return sectionContent.toString();
    }

    /**
     * Helper: Checks if a line looks like a section heading.
     */
    private boolean looksLikeSectionHeading(String line) {
        return line.matches("^\\d+(\\.\\d*)?\\s+.*") ||  // Numbered heading
               line.matches("^[A-Z][A-Z\\s]+$") ||        // All caps heading
               line.length() < 60 && line.matches("^[A-Z].*");  // Short capitalized line
    }

    /**
     * Helper: Checks if line matches any keyword for the section.
     */
    private boolean matchesAnySectionKeyword(SectionAnalysis.IEEE1058Section section, String line) {
        Set<String> keywords = getKeywordsForSection(section);
        return keywords.stream().anyMatch(kw -> line.contains(kw.toLowerCase()));
    }

    /**
     * BALANCED: Returns minimum required content length for each section.
     * Adjusted to realistic expectations for professor-approved SPMPs.
     */
    private int getMinimumSectionLength(SectionAnalysis.IEEE1058Section section) {
        return switch (section) {
            case OVERVIEW -> 250;  // Should have introduction and scope
            case DOCUMENTATION_PLAN -> 200;  // List of deliverables and standards
            case MASTER_SCHEDULE -> 300;  // Timeline with milestones
            case ORGANIZATION -> 250;  // Org chart and structure
            case STANDARDS_PRACTICES -> 200;  // Engineering standards
            case RISK_MANAGEMENT -> 300;  // Risk register and mitigation
            case STAFF_ORGANIZATION -> 200;  // Team roles and skills
            case BUDGET_RESOURCE -> 250;  // Budget estimates and allocation
            case REVIEWS_AUDITS -> 200;  // Review process and criteria
            case PROBLEM_RESOLUTION -> 150;  // Issue handling process
            case CHANGE_MANAGEMENT -> 200;  // Change control procedures
            case GLOSSARY_APPENDIX -> 100;  // Terms and references
        };
    }

    /**
     * CRITICAL FIX: Calculates penalty based on content length.
     * Sections below minimum length receive reduced scores.
     */
    private double calculateLengthPenalty(int actualLength, int minimumLength) {
        if (actualLength <= 0) {
            return 0.0;  // No content = 0% of score
        }
        
        if (actualLength >= minimumLength) {
            return 1.0;  // Meets minimum = full score
        }
        
        // Proportional penalty for partial content
        double ratio = actualLength / (double) minimumLength;
        
        // Apply progressive penalty curve
        if (ratio >= 0.75) {
            return 0.9;  // 75-99% length = 90% of score
        } else if (ratio >= 0.5) {
            return 0.7;  // 50-74% length = 70% of score
        } else if (ratio >= 0.25) {
            return 0.4;  // 25-49% length = 40% of score
        } else {
            return 0.1;  // <25% length = 10% of score
        }
    }

    private record SubclauseResult(double coveragePct, List<String> missingSubclauses, String evidenceSnippet) {}
}
