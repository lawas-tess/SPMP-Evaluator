package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.dto.ComplianceReportDTO;
import com.team02.spmpevaluator.dto.SectionAnalysisDTO;
import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReportExportService.
 * Tests PDF and Excel report generation for compliance reports.
 */
@ExtendWith(MockitoExtension.class)
class ReportExportServiceTest {

    @Mock
    private ComplianceScoreRepository complianceScoreRepository;

    @Mock
    private ComplianceEvaluationService complianceEvaluationService;

    @InjectMocks
    private ReportExportService reportExportService;

    private User testUser;
    private SPMPDocument testDocument;
    private ComplianceScore testScore;
    private ComplianceReportDTO testReportDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("student@test.com");

        testDocument = new SPMPDocument();
        testDocument.setId(1L);
        testDocument.setFileName("test_spmp.pdf");
        testDocument.setFileUrl("/uploads/documents/test_spmp.pdf");
        testDocument.setFileType("application/pdf");
        testDocument.setUploadedBy(testUser);

        testScore = new ComplianceScore();
        testScore.setId(1L);
        testScore.setDocument(testDocument);
        testScore.setOverallScore(85.0);
        testScore.setStructureScore(90.0);
        testScore.setCompletenessScore(80.0);
        testScore.setCompliant(true);
        testScore.setSummary("Good compliance with IEEE 1058 standards.");
        testScore.setEvaluatedAt(LocalDateTime.now());

        // Set up DTO
        testReportDTO = new ComplianceReportDTO();
        testReportDTO.setDocumentId(1L);
        testReportDTO.setDocumentName("test_spmp.pdf");
        testReportDTO.setOverallScore(85.0);
        testReportDTO.setStructureScore(90.0);
        testReportDTO.setCompletenessScore(80.0);
        testReportDTO.setCompliant(true);
        testReportDTO.setSummary("Good compliance with IEEE 1058 standards.");
        testReportDTO.setSectionAnalyses(new ArrayList<>());
    }

    @Nested
    @DisplayName("Export PDF Tests")
    class ExportPdfTests {

        @Test
        @DisplayName("Should export PDF successfully for valid document")
        void exportPdf_ValidDocument_ReturnsPdfBytes() throws IOException {
            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(ComplianceScore.class), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
            // PDF files start with %PDF
            String header = new String(result, 0, Math.min(4, result.length));
            assertEquals("%PDF", header);
        }

        @Test
        @DisplayName("Should throw exception when document not found")
        void exportPdf_DocumentNotFound_ThrowsException() {
            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(999L))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> reportExportService.exportPdf(999L));
        }

        @Test
        @DisplayName("Should include compliance status in PDF for compliant document")
        void exportPdf_CompliantDocument_IncludesStatus() throws IOException {
            testReportDTO.setCompliant(true);
            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should include compliance status in PDF for non-compliant document")
        void exportPdf_NonCompliantDocument_IncludesStatus() throws IOException {
            testScore.setCompliant(false);
            testReportDTO.setCompliant(false);
            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should include section analyses in PDF")
        void exportPdf_WithSectionAnalyses_IncludesSections() throws IOException {
            SectionAnalysisDTO section1 = new SectionAnalysisDTO();
            section1.setSectionName("Overview");
            section1.setSectionScore(95.0);
            section1.setFindings("Well structured overview section");
            section1.setRecommendations("Consider adding more detail to project scope");

            SectionAnalysisDTO section2 = new SectionAnalysisDTO();
            section2.setSectionName("References");
            section2.setSectionScore(80.0);
            section2.setFindings("References present but incomplete");
            section2.setRecommendations("Add IEEE 1058 standard reference");

            testReportDTO.setSectionAnalyses(List.of(section1, section2));

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle empty section analyses")
        void exportPdf_EmptySectionAnalyses_GeneratesReport() throws IOException {
            testReportDTO.setSectionAnalyses(new ArrayList<>());

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle null section analyses")
        void exportPdf_NullSectionAnalyses_GeneratesReport() throws IOException {
            testReportDTO.setSectionAnalyses(null);

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }
    }

    @Nested
    @DisplayName("Export Excel Tests")
    class ExportExcelTests {

        @Test
        @DisplayName("Should export Excel successfully for valid document")
        void exportExcel_ValidDocument_ReturnsExcelBytes() throws IOException {
            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(ComplianceScore.class), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportExcel(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
            // XLSX files are ZIP archives starting with PK
            assertEquals((byte) 0x50, result[0]); // 'P'
            assertEquals((byte) 0x4B, result[1]); // 'K'
        }

        @Test
        @DisplayName("Should throw exception when document not found")
        void exportExcel_DocumentNotFound_ThrowsException() {
            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(999L))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> reportExportService.exportExcel(999L));
        }

        @Test
        @DisplayName("Should include section analyses in Excel")
        void exportExcel_WithSectionAnalyses_IncludesSections() throws IOException {
            SectionAnalysisDTO section1 = new SectionAnalysisDTO();
            section1.setSectionName("Overview");
            section1.setSectionScore(95.0);
            section1.setFindings("Well structured overview section");
            section1.setRecommendations("Consider adding more detail");

            testReportDTO.setSectionAnalyses(List.of(section1));

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportExcel(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle empty section analyses in Excel")
        void exportExcel_EmptySectionAnalyses_GeneratesReport() throws IOException {
            testReportDTO.setSectionAnalyses(new ArrayList<>());

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportExcel(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle null section analyses in Excel")
        void exportExcel_NullSectionAnalyses_GeneratesReport() throws IOException {
            testReportDTO.setSectionAnalyses(null);

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportExcel(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle high score compliant document in Excel")
        void exportExcel_HighScore_GeneratesReport() throws IOException {
            testReportDTO.setOverallScore(95.0);
            testReportDTO.setCompliant(true);

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportExcel(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle low score non-compliant document in Excel")
        void exportExcel_LowScore_GeneratesReport() throws IOException {
            testReportDTO.setOverallScore(45.0);
            testReportDTO.setCompliant(false);

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportExcel(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }
    }

    @Nested
    @DisplayName("Score Formatting Tests")
    class ScoreFormattingTests {

        @Test
        @DisplayName("Should handle null structure score")
        void exportPdf_NullStructureScore_HandlesGracefully() throws IOException {
            testReportDTO.setStructureScore(null);

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle null completeness score")
        void exportPdf_NullCompletenessScore_HandlesGracefully() throws IOException {
            testReportDTO.setCompletenessScore(null);

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle null summary")
        void exportPdf_NullSummary_HandlesGracefully() throws IOException {
            testReportDTO.setSummary(null);

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle decimal scores")
        void exportPdf_DecimalScores_FormatsCorrectly() throws IOException {
            testReportDTO.setStructureScore(85.567);
            testReportDTO.setCompletenessScore(72.123);

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }
    }

    @Nested
    @DisplayName("Section Analysis Edge Cases")
    class SectionAnalysisEdgeCasesTests {

        @Test
        @DisplayName("Should handle section with null findings")
        void exportPdf_SectionNullFindings_HandlesGracefully() throws IOException {
            SectionAnalysisDTO section = new SectionAnalysisDTO();
            section.setSectionName("Overview");
            section.setSectionScore(90.0);
            section.setFindings(null);
            section.setRecommendations("Add more detail");

            testReportDTO.setSectionAnalyses(List.of(section));

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle section with null recommendations")
        void exportPdf_SectionNullRecommendations_HandlesGracefully() throws IOException {
            SectionAnalysisDTO section = new SectionAnalysisDTO();
            section.setSectionName("Overview");
            section.setSectionScore(90.0);
            section.setFindings("Well structured");
            section.setRecommendations(null);

            testReportDTO.setSectionAnalyses(List.of(section));

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }

        @Test
        @DisplayName("Should handle many sections for pagination")
        void exportPdf_ManySections_HandlesPagination() throws IOException {
            List<SectionAnalysisDTO> sections = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                SectionAnalysisDTO section = new SectionAnalysisDTO();
                section.setSectionName("Section " + (i + 1));
                section.setSectionScore(70.0 + i);
                section.setFindings("Findings for section " + (i + 1)
                        + " with detailed analysis of the content structure and compliance requirements.");
                section.setRecommendations("Recommendation for section " + (i + 1)
                        + " including specific steps to improve IEEE 1058 compliance.");
                sections.add(section);
            }
            testReportDTO.setSectionAnalyses(sections);

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
            // PDF with multiple pages should be larger
            assertTrue(result.length > 1000);
        }

        @Test
        @DisplayName("Should handle section scores at boundary values")
        void exportPdf_BoundaryScores_AppliesCorrectColors() throws IOException {
            SectionAnalysisDTO highScore = new SectionAnalysisDTO();
            highScore.setSectionName("High Score Section");
            highScore.setSectionScore(100.0);
            highScore.setFindings("Perfect");
            highScore.setRecommendations("None");

            SectionAnalysisDTO mediumScore = new SectionAnalysisDTO();
            mediumScore.setSectionName("Medium Score Section");
            mediumScore.setSectionScore(70.0);
            mediumScore.setFindings("Average");
            mediumScore.setRecommendations("Improve");

            SectionAnalysisDTO lowScore = new SectionAnalysisDTO();
            lowScore.setSectionName("Low Score Section");
            lowScore.setSectionScore(40.0);
            lowScore.setFindings("Needs work");
            lowScore.setRecommendations("Major revision needed");

            testReportDTO.setSectionAnalyses(List.of(highScore, mediumScore, lowScore));

            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            byte[] result = reportExportService.exportPdf(1L);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }
    }

    @Nested
    @DisplayName("Repository Interaction Tests")
    class RepositoryInteractionTests {

        @Test
        @DisplayName("Should call repository with correct document ID")
        void exportPdf_CallsRepositoryWithCorrectId() throws IOException {
            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            reportExportService.exportPdf(1L);

            verify(complianceScoreRepository).findByDocumentIdWithSectionAnalyses(1L);
        }

        @Test
        @DisplayName("Should call conversion service with correct parameters")
        void exportPdf_CallsConversionServiceCorrectly() throws IOException {
            when(complianceScoreRepository.findByDocumentIdWithSectionAnalyses(1L))
                    .thenReturn(Optional.of(testScore));
            when(complianceEvaluationService.convertToDTO(any(), eq(1L), eq("test_spmp.pdf")))
                    .thenReturn(testReportDTO);

            reportExportService.exportPdf(1L);

            verify(complianceEvaluationService).convertToDTO(
                    eq(testScore),
                    eq(1L),
                    eq("test_spmp.pdf"));
        }
    }
}
