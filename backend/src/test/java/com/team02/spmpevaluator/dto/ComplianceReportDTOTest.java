package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ComplianceReportDTO.
 */
@DisplayName("ComplianceReportDTO Tests")
class ComplianceReportDTOTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            ComplianceReportDTO dto = new ComplianceReportDTO();

            assertNotNull(dto);
            assertNull(dto.getDocumentId());
            assertNull(dto.getDocumentName());
            assertNull(dto.getOverallScore());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            LocalDateTime now = LocalDateTime.now();
            List<SectionAnalysisDTO> sections = new ArrayList<>();

            ComplianceReportDTO dto = new ComplianceReportDTO(
                    1L, "document.pdf", 85.5, 90.0, 80.0,
                    10, 12, true, "Summary text", sections, now, 95.0, "Good work");

            assertEquals(1L, dto.getDocumentId());
            assertEquals("document.pdf", dto.getDocumentName());
            assertEquals(85.5, dto.getOverallScore());
            assertEquals(90.0, dto.getStructureScore());
            assertEquals(80.0, dto.getCompletenessScore());
            assertEquals(10, dto.getSectionsFound());
            assertEquals(12, dto.getTotalSectionsRequired());
            assertTrue(dto.isCompliant());
            assertEquals("Summary text", dto.getSummary());
            assertEquals(sections, dto.getSectionAnalyses());
            assertEquals(now, dto.getEvaluatedAt());
            assertEquals(95.0, dto.getProfessorOverride());
            assertEquals("Good work", dto.getProfessorNotes());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get documentId")
        void setAndGetDocumentId() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setDocumentId(100L);
            assertEquals(100L, dto.getDocumentId());
        }

        @Test
        @DisplayName("Should set and get documentName")
        void setAndGetDocumentName() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setDocumentName("test.pdf");
            assertEquals("test.pdf", dto.getDocumentName());
        }

        @Test
        @DisplayName("Should set and get overallScore")
        void setAndGetOverallScore() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setOverallScore(92.5);
            assertEquals(92.5, dto.getOverallScore());
        }

        @Test
        @DisplayName("Should set and get structureScore")
        void setAndGetStructureScore() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setStructureScore(88.0);
            assertEquals(88.0, dto.getStructureScore());
        }

        @Test
        @DisplayName("Should set and get completenessScore")
        void setAndGetCompletenessScore() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setCompletenessScore(95.0);
            assertEquals(95.0, dto.getCompletenessScore());
        }

        @Test
        @DisplayName("Should set and get sectionsFound")
        void setAndGetSectionsFound() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setSectionsFound(8);
            assertEquals(8, dto.getSectionsFound());
        }

        @Test
        @DisplayName("Should set and get totalSectionsRequired")
        void setAndGetTotalSectionsRequired() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setTotalSectionsRequired(12);
            assertEquals(12, dto.getTotalSectionsRequired());
        }

        @Test
        @DisplayName("Should set and get compliant")
        void setAndGetCompliant() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setCompliant(true);
            assertTrue(dto.isCompliant());

            dto.setCompliant(false);
            assertFalse(dto.isCompliant());
        }

        @Test
        @DisplayName("Should set and get summary")
        void setAndGetSummary() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setSummary("Test summary");
            assertEquals("Test summary", dto.getSummary());
        }

        @Test
        @DisplayName("Should set and get sectionAnalyses")
        void setAndGetSectionAnalyses() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            List<SectionAnalysisDTO> sections = new ArrayList<>();
            sections.add(new SectionAnalysisDTO());
            dto.setSectionAnalyses(sections);
            assertEquals(1, dto.getSectionAnalyses().size());
        }

        @Test
        @DisplayName("Should set and get evaluatedAt")
        void setAndGetEvaluatedAt() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            LocalDateTime now = LocalDateTime.now();
            dto.setEvaluatedAt(now);
            assertEquals(now, dto.getEvaluatedAt());
        }

        @Test
        @DisplayName("Should set and get professorOverride")
        void setAndGetProfessorOverride() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setProfessorOverride(98.0);
            assertEquals(98.0, dto.getProfessorOverride());
        }

        @Test
        @DisplayName("Should set and get professorNotes")
        void setAndGetProfessorNotes() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setProfessorNotes("Excellent work");
            assertEquals("Excellent work", dto.getProfessorNotes());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            LocalDateTime now = LocalDateTime.now();
            ComplianceReportDTO dto1 = new ComplianceReportDTO(
                    1L, "doc.pdf", 85.0, 90.0, 80.0, 10, 12,
                    true, "Summary", null, now, null, null);
            ComplianceReportDTO dto2 = new ComplianceReportDTO(
                    1L, "doc.pdf", 85.0, 90.0, 80.0, 10, 12,
                    true, "Summary", null, now, null, null);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different values")
        void equals_DifferentValues_ReturnsFalse() {
            ComplianceReportDTO dto1 = new ComplianceReportDTO();
            dto1.setDocumentId(1L);
            ComplianceReportDTO dto2 = new ComplianceReportDTO();
            dto2.setDocumentId(2L);

            assertNotEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with all fields")
        void toString_ContainsAllFields() {
            ComplianceReportDTO dto = new ComplianceReportDTO();
            dto.setDocumentId(1L);
            dto.setDocumentName("test.pdf");
            dto.setOverallScore(85.0);

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("documentId=1"));
            assertTrue(result.contains("documentName=test.pdf"));
            assertTrue(result.contains("overallScore=85.0"));
        }
    }
}
