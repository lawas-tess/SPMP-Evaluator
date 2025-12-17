package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.dto.ComplianceReportDTO;
import com.team02.spmpevaluator.entity.*;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ComplianceEvaluationService.
 * Tests IEEE 1058 compliance evaluation, scoring, and reporting.
 */
@ExtendWith(MockitoExtension.class)
class ComplianceEvaluationServiceTest {

    @Mock
    private ComplianceScoreRepository complianceScoreRepository;

    @InjectMocks
    private ComplianceEvaluationService complianceEvaluationService;

    private SPMPDocument testDocument;
    private User testStudent;
    private ComplianceScore testComplianceScore;

    @BeforeEach
    void setUp() {
        // Setup test student
        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setUsername("student1");
        testStudent.setEmail("student@example.com");
        testStudent.setRole(Role.STUDENT);
        testStudent.setEnabled(true);

        // Setup test document
        testDocument = new SPMPDocument();
        testDocument.setId(1L);
        testDocument.setFileName("test_spmp.pdf");
        testDocument.setFileUrl("/uploads/documents/test_spmp.pdf");
        testDocument.setFileSize(50000L);
        testDocument.setFileType("application/pdf");
        testDocument.setUploadedBy(testStudent);
        testDocument.setUploadedAt(LocalDateTime.now());
        testDocument.setEvaluated(false);

        // Setup test compliance score
        testComplianceScore = new ComplianceScore();
        testComplianceScore.setId(1L);
        testComplianceScore.setDocument(testDocument);
        testComplianceScore.setOverallScore(85.0);
        testComplianceScore.setStructureScore(90.0);
        testComplianceScore.setCompletenessScore(80.0);
        testComplianceScore.setSectionsFound(10);
        testComplianceScore.setTotalSectionsRequired(12);
        testComplianceScore.setCompliant(true);
        testComplianceScore.setSummary("Document meets IEEE 1058 compliance threshold");
        testComplianceScore.setEvaluatedAt(LocalDateTime.now());
        testComplianceScore.setSectionAnalyses(new ArrayList<>());
    }

    @Nested
    @DisplayName("Evaluate Document Tests")
    class EvaluateDocumentTests {

        @Test
        @DisplayName("Should evaluate document with minimal content")
        void evaluateDocument_MinimalContent() {
            // Arrange
            String minimalContent = "Project Overview. This is the overview section.";
            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.empty());
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenAnswer(invocation -> {
                ComplianceScore score = invocation.getArgument(0);
                score.setId(1L);
                return score;
            });

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, minimalContent);

            // Assert
            assertNotNull(result);
            assertEquals(testDocument, result.getDocument());
            assertNotNull(result.getOverallScore());
            assertNotNull(result.getSummary());
            assertNotNull(result.getSectionAnalyses());
            verify(complianceScoreRepository).save(any(ComplianceScore.class));
        }

        @Test
        @DisplayName("Should evaluate document with comprehensive content")
        void evaluateDocument_ComprehensiveContent() {
            // Arrange - Content with multiple IEEE 1058 keywords
            String comprehensiveContent = """
                    1. Overview
                    This Software Project Management Plan provides project management framework.

                    2. References
                    IEEE 1058 Standard for Software Project Management Plans.

                    3. Definitions
                    Key terminology and abbreviations used in this document.

                    4. Project Organization
                    The project team consists of developers, testers, and managers.
                    Work breakdown structure and responsibilities defined.

                    5. Managerial Process
                    Project planning, estimation, risk management approach.
                    Schedule, milestones, and deliverables tracked.

                    6. Technical Process
                    Development methodology using agile approach.
                    Configuration management and quality assurance.

                    7. Work Packages
                    Detailed work breakdown and task assignments.
                    Budget allocation and resource planning.

                    8. Schedule
                    Master schedule with Gantt chart representation.
                    Critical path analysis and dependencies.

                    9. Risk Management
                    Risk identification, assessment, and mitigation strategies.

                    10. Glossary
                    Terms, acronyms, and definitions appendix.
                    """;

            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.empty());
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenAnswer(invocation -> {
                ComplianceScore score = invocation.getArgument(0);
                score.setId(1L);
                return score;
            });

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, comprehensiveContent);

            // Assert
            assertNotNull(result);
            assertTrue(result.getSectionsFound() > 0, "Should find at least some sections");
            assertNotNull(result.getSectionAnalyses());
            assertEquals(12, result.getTotalSectionsRequired()); // IEEE 1058 has 12 sections
        }

        @Test
        @DisplayName("Should re-evaluate existing document")
        void evaluateDocument_ReEvaluate() {
            // Arrange
            String content = "Updated project overview with new milestones and schedule.";
            ComplianceScore existingScore = new ComplianceScore();
            existingScore.setId(1L);
            existingScore.setDocument(testDocument);
            existingScore.setSectionAnalyses(new ArrayList<>());
            existingScore.setOverallScore(50.0);

            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.of(existingScore));
            when(complianceScoreRepository.save(any(ComplianceScore.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, content);

            // Assert
            assertNotNull(result);
            assertEquals(existingScore.getId(), result.getId()); // Should reuse existing ID
            verify(complianceScoreRepository).save(any(ComplianceScore.class));
        }

        @Test
        @DisplayName("Should handle empty document content")
        void evaluateDocument_EmptyContent() {
            // Arrange
            String emptyContent = "";
            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.empty());
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenAnswer(invocation -> {
                ComplianceScore score = invocation.getArgument(0);
                score.setId(1L);
                return score;
            });

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, emptyContent);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.getSectionsFound()); // No sections found in empty content
            assertFalse(result.isCompliant()); // Should not be compliant with empty content
        }

        @Test
        @DisplayName("Should set evaluated timestamp")
        void evaluateDocument_SetsTimestamp() {
            // Arrange
            String content = "Project Overview and Schedule section.";
            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.empty());
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenAnswer(invocation -> {
                ComplianceScore score = invocation.getArgument(0);
                score.setId(1L);
                return score;
            });

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, content);

            // Assert
            assertNotNull(result.getEvaluatedAt());
        }

        @Test
        @DisplayName("Should associate section analyses with compliance score")
        void evaluateDocument_AssociatesSectionAnalyses() {
            // Arrange
            String content = "Overview section with project scope and objectives.";
            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.empty());
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenAnswer(invocation -> {
                ComplianceScore score = invocation.getArgument(0);
                score.setId(1L);
                return score;
            });

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, content);

            // Assert
            assertNotNull(result.getSectionAnalyses());
            assertFalse(result.getSectionAnalyses().isEmpty());
            // All section analyses should be linked to the compliance score
            for (SectionAnalysis analysis : result.getSectionAnalyses()) {
                assertEquals(result, analysis.getComplianceScore());
            }
        }

        @Test
        @DisplayName("Should calculate proper compliance status based on threshold")
        void evaluateDocument_ComplianceThreshold() {
            // Arrange - Minimal content likely results in low score
            String lowScoreContent = "Brief document.";
            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.empty());
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenAnswer(invocation -> {
                ComplianceScore score = invocation.getArgument(0);
                score.setId(1L);
                return score;
            });

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, lowScoreContent);

            // Assert
            assertNotNull(result);
            // Compliance is based on 80% threshold
            if (result.getOverallScore() >= 80.0) {
                assertTrue(result.isCompliant());
            } else {
                assertFalse(result.isCompliant());
            }
        }
    }

    @Nested
    @DisplayName("Convert to DTO Tests")
    class ConvertToDTOTests {

        @Test
        @DisplayName("Should convert compliance score to DTO")
        void convertToDTO_Success() {
            // Arrange
            SectionAnalysis sectionAnalysis = new SectionAnalysis();
            sectionAnalysis.setId(1L);
            sectionAnalysis.setSectionName(SectionAnalysis.IEEE1058Section.OVERVIEW);
            sectionAnalysis.setPresent(true);
            sectionAnalysis.setSectionScore(85.0);
            sectionAnalysis.setFindings("Section present with good coverage");
            sectionAnalysis.setRecommendations("Consider adding more detail");
            sectionAnalysis.setCoverage(75.0);
            sectionAnalysis.setSeverity("LOW");
            sectionAnalysis.setEvidenceSnippet("Project overview describes...");
            sectionAnalysis.setMissingSubclauses("");
            sectionAnalysis.setSectionWeight(10);

            testComplianceScore.getSectionAnalyses().add(sectionAnalysis);

            // Act
            ComplianceReportDTO dto = complianceEvaluationService.convertToDTO(
                    testComplianceScore, 1L, "test_document.pdf");

            // Assert
            assertNotNull(dto);
            assertEquals(1L, dto.getDocumentId());
            assertEquals("test_document.pdf", dto.getDocumentName());
            assertEquals(85.0, dto.getOverallScore());
            assertEquals(90.0, dto.getStructureScore());
            assertEquals(80.0, dto.getCompletenessScore());
            assertEquals(10, dto.getSectionsFound());
            assertEquals(12, dto.getTotalSectionsRequired());
            assertTrue(dto.isCompliant());
            assertNotNull(dto.getSummary());
        }

        @Test
        @DisplayName("Should convert section analyses to DTOs")
        void convertToDTO_WithSectionAnalyses() {
            // Arrange
            SectionAnalysis section1 = new SectionAnalysis();
            section1.setId(1L);
            section1.setSectionName(SectionAnalysis.IEEE1058Section.OVERVIEW);
            section1.setPresent(true);
            section1.setSectionScore(90.0);
            section1.setCoverage(85.0);
            section1.setSeverity("LOW");
            section1.setMissingSubclauses("");
            section1.setSectionWeight(10);

            SectionAnalysis section2 = new SectionAnalysis();
            section2.setId(2L);
            section2.setSectionName(SectionAnalysis.IEEE1058Section.ORGANIZATION);
            section2.setPresent(false);
            section2.setSectionScore(0.0);
            section2.setCoverage(0.0);
            section2.setSeverity("HIGH");
            section2.setMissingSubclauses("4.1 Project Organization, 4.2 Team Structure");
            section2.setSectionWeight(12);

            testComplianceScore.getSectionAnalyses().addAll(Arrays.asList(section1, section2));

            // Act
            ComplianceReportDTO dto = complianceEvaluationService.convertToDTO(
                    testComplianceScore, 1L, "test.pdf");

            // Assert
            assertNotNull(dto.getSectionAnalyses());
            assertEquals(2, dto.getSectionAnalyses().size());

            // Verify first section DTO
            var sectionDto1 = dto.getSectionAnalyses().get(0);
            assertEquals("Project Overview", sectionDto1.getSectionName());
            assertTrue(sectionDto1.isPresent());
            assertEquals(90.0, sectionDto1.getSectionScore());

            // Verify second section DTO
            var sectionDto2 = dto.getSectionAnalyses().get(1);
            assertEquals("Project Organization", sectionDto2.getSectionName());
            assertFalse(sectionDto2.isPresent());
        }

        @Test
        @DisplayName("Should handle null section analyses")
        void convertToDTO_NullSectionAnalyses() {
            // Arrange
            testComplianceScore.setSectionAnalyses(null);

            // Act
            ComplianceReportDTO dto = complianceEvaluationService.convertToDTO(
                    testComplianceScore, 1L, "test.pdf");

            // Assert
            assertNotNull(dto);
            assertNotNull(dto.getSectionAnalyses());
            assertTrue(dto.getSectionAnalyses().isEmpty());
        }

        @Test
        @DisplayName("Should handle empty section analyses")
        void convertToDTO_EmptySectionAnalyses() {
            // Arrange
            testComplianceScore.setSectionAnalyses(new ArrayList<>());

            // Act
            ComplianceReportDTO dto = complianceEvaluationService.convertToDTO(
                    testComplianceScore, 1L, "test.pdf");

            // Assert
            assertNotNull(dto);
            assertTrue(dto.getSectionAnalyses().isEmpty());
        }

        @Test
        @DisplayName("Should include professor override if present")
        void convertToDTO_WithProfessorOverride() {
            // Arrange
            testComplianceScore.setProfessorOverride(95.0);
            testComplianceScore.setProfessorNotes("Excellent work, well-structured SPMP");

            // Act
            ComplianceReportDTO dto = complianceEvaluationService.convertToDTO(
                    testComplianceScore, 1L, "test.pdf");

            // Assert
            assertEquals(95.0, dto.getProfessorOverride());
            assertEquals("Excellent work, well-structured SPMP", dto.getProfessorNotes());
        }

        @Test
        @DisplayName("Should parse missing subclauses correctly")
        void convertToDTO_ParseMissingSubclauses() {
            // Arrange
            SectionAnalysis section = new SectionAnalysis();
            section.setId(1L);
            section.setSectionName(SectionAnalysis.IEEE1058Section.RISK_MANAGEMENT);
            section.setPresent(true);
            section.setSectionScore(60.0);
            section.setMissingSubclauses("5.2.1 Risk Identification, 5.2.2 Risk Assessment, 5.2.3 Mitigation");
            section.setSectionWeight(10);

            testComplianceScore.getSectionAnalyses().add(section);

            // Act
            ComplianceReportDTO dto = complianceEvaluationService.convertToDTO(
                    testComplianceScore, 1L, "test.pdf");

            // Assert
            var sectionDto = dto.getSectionAnalyses().get(0);
            assertNotNull(sectionDto.getMissingSubclauses());
            assertEquals(3, sectionDto.getMissingSubclauses().size());
            assertTrue(sectionDto.getMissingSubclauses().contains("5.2.1 Risk Identification"));
        }

        @Test
        @DisplayName("Should handle blank missing subclauses")
        void convertToDTO_BlankMissingSubclauses() {
            // Arrange
            SectionAnalysis section = new SectionAnalysis();
            section.setId(1L);
            section.setSectionName(SectionAnalysis.IEEE1058Section.OVERVIEW);
            section.setPresent(true);
            section.setSectionScore(100.0);
            section.setMissingSubclauses("   ");
            section.setSectionWeight(10);

            testComplianceScore.getSectionAnalyses().add(section);

            // Act
            ComplianceReportDTO dto = complianceEvaluationService.convertToDTO(
                    testComplianceScore, 1L, "test.pdf");

            // Assert
            var sectionDto = dto.getSectionAnalyses().get(0);
            assertNotNull(sectionDto.getMissingSubclauses());
            assertTrue(sectionDto.getMissingSubclauses().isEmpty());
        }
    }

    @Nested
    @DisplayName("Get All Evaluations Tests")
    class GetAllEvaluationsTests {

        @Test
        @DisplayName("Should return all evaluations")
        void getAllEvaluations_Success() {
            // Arrange
            ComplianceScore score1 = new ComplianceScore();
            score1.setId(1L);
            score1.setOverallScore(85.0);

            ComplianceScore score2 = new ComplianceScore();
            score2.setId(2L);
            score2.setOverallScore(75.0);

            List<ComplianceScore> scores = Arrays.asList(score1, score2);
            when(complianceScoreRepository.findAll()).thenReturn(scores);

            // Act
            List<ComplianceScore> result = complianceEvaluationService.getAllEvaluations();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(complianceScoreRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no evaluations exist")
        void getAllEvaluations_Empty() {
            // Arrange
            when(complianceScoreRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<ComplianceScore> result = complianceEvaluationService.getAllEvaluations();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("IEEE 1058 Section Coverage Tests")
    class IEEE1058SectionCoverageTests {

        @Test
        @DisplayName("Should detect Overview section")
        void detectOverviewSection() {
            // Arrange
            String content = """
                    1. Overview
                    1.1 Project Summary
                    This document describes the Software Project Management Plan for the SPMP Evaluator system.
                    The purpose and scope of this project is to provide automated compliance checking.
                    """;
            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.empty());
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenAnswer(invocation -> {
                ComplianceScore score = invocation.getArgument(0);
                score.setId(1L);
                return score;
            });

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, content);

            // Assert
            assertNotNull(result.getSectionAnalyses());
            SectionAnalysis overviewSection = result.getSectionAnalyses().stream()
                    .filter(s -> s.getSectionName() == SectionAnalysis.IEEE1058Section.OVERVIEW)
                    .findFirst()
                    .orElse(null);
            assertNotNull(overviewSection, "Overview section analysis should exist");
        }

        @Test
        @DisplayName("Should detect Risk Management section")
        void detectRiskManagementSection() {
            // Arrange
            String content = """
                    Risk Management Plan
                    Risk identification and assessment procedures.
                    Risk mitigation strategies and contingency plans.
                    Risk monitoring and reporting activities.
                    """;
            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.empty());
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenAnswer(invocation -> {
                ComplianceScore score = invocation.getArgument(0);
                score.setId(1L);
                return score;
            });

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, content);

            // Assert
            assertNotNull(result.getSectionAnalyses());
            SectionAnalysis riskSection = result.getSectionAnalyses().stream()
                    .filter(s -> s.getSectionName() == SectionAnalysis.IEEE1058Section.RISK_MANAGEMENT)
                    .findFirst()
                    .orElse(null);
            assertNotNull(riskSection, "Risk Management section analysis should exist");
        }

        @Test
        @DisplayName("Should detect Master Schedule section")
        void detectMasterScheduleSection() {
            // Arrange
            String content = """
                    Master Schedule
                    Project timeline with milestones and deliverables.
                    Gantt chart showing work packages and dependencies.
                    Critical path analysis and schedule variance tracking.
                    """;
            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.empty());
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenAnswer(invocation -> {
                ComplianceScore score = invocation.getArgument(0);
                score.setId(1L);
                return score;
            });

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, content);

            // Assert
            SectionAnalysis scheduleSection = result.getSectionAnalyses().stream()
                    .filter(s -> s.getSectionName() == SectionAnalysis.IEEE1058Section.MASTER_SCHEDULE)
                    .findFirst()
                    .orElse(null);
            assertNotNull(scheduleSection, "Master Schedule section analysis should exist");
        }

        @Test
        @DisplayName("Should evaluate all 12 IEEE 1058 sections")
        void evaluateAllSections() {
            // Arrange
            String content = "Test document with some content";
            when(complianceScoreRepository.findByDocument(testDocument)).thenReturn(Optional.empty());
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenAnswer(invocation -> {
                ComplianceScore score = invocation.getArgument(0);
                score.setId(1L);
                return score;
            });

            // Act
            ComplianceScore result = complianceEvaluationService.evaluateDocument(testDocument, content);

            // Assert
            assertEquals(12, result.getSectionAnalyses().size(), "Should analyze all 12 IEEE 1058 sections");
            assertEquals(12, result.getTotalSectionsRequired());
        }
    }
}
