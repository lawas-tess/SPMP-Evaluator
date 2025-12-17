package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SectionAnalysisDTO.
 */
@DisplayName("SectionAnalysisDTO Tests")
class SectionAnalysisDTOTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();

            assertNotNull(dto);
            assertNull(dto.getId());
            assertNull(dto.getSectionName());
            assertNull(dto.getSectionScore());
            assertNull(dto.getMissingSubclauses());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            List<String> missingSubclauses = Arrays.asList("1.1", "1.2");
            SectionAnalysisDTO dto = new SectionAnalysisDTO(
                    1L, "Overview", true, 85.5, "Good coverage",
                    "Add more details", 5, 90.0, "LOW",
                    "Sample text...", missingSubclauses, 10);

            assertEquals(1L, dto.getId());
            assertEquals("Overview", dto.getSectionName());
            assertTrue(dto.isPresent());
            assertEquals(85.5, dto.getSectionScore());
            assertEquals("Good coverage", dto.getFindings());
            assertEquals("Add more details", dto.getRecommendations());
            assertEquals(5, dto.getPageNumber());
            assertEquals(90.0, dto.getCoverage());
            assertEquals("LOW", dto.getSeverity());
            assertEquals("Sample text...", dto.getEvidenceSnippet());
            assertEquals(missingSubclauses, dto.getMissingSubclauses());
            assertEquals(10, dto.getSectionWeight());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setId(100L);
            assertEquals(100L, dto.getId());
        }

        @Test
        @DisplayName("Should set and get sectionName")
        void setAndGetSectionName() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setSectionName("Managerial Process");
            assertEquals("Managerial Process", dto.getSectionName());
        }

        @Test
        @DisplayName("Should set and get present")
        void setAndGetPresent() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setPresent(true);
            assertTrue(dto.isPresent());

            dto.setPresent(false);
            assertFalse(dto.isPresent());
        }

        @Test
        @DisplayName("Should set and get sectionScore")
        void setAndGetSectionScore() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setSectionScore(92.5);
            assertEquals(92.5, dto.getSectionScore());
        }

        @Test
        @DisplayName("Should set and get findings")
        void setAndGetFindings() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setFindings("Section is well structured");
            assertEquals("Section is well structured", dto.getFindings());
        }

        @Test
        @DisplayName("Should set and get recommendations")
        void setAndGetRecommendations() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setRecommendations("Add risk assessment details");
            assertEquals("Add risk assessment details", dto.getRecommendations());
        }

        @Test
        @DisplayName("Should set and get pageNumber")
        void setAndGetPageNumber() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setPageNumber(15);
            assertEquals(15, dto.getPageNumber());
        }

        @Test
        @DisplayName("Should set and get coverage")
        void setAndGetCoverage() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setCoverage(75.5);
            assertEquals(75.5, dto.getCoverage());
        }

        @Test
        @DisplayName("Should set and get severity")
        void setAndGetSeverity() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setSeverity("HIGH");
            assertEquals("HIGH", dto.getSeverity());
        }

        @Test
        @DisplayName("Should set and get evidenceSnippet")
        void setAndGetEvidenceSnippet() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setEvidenceSnippet("The project schedule outlines...");
            assertEquals("The project schedule outlines...", dto.getEvidenceSnippet());
        }

        @Test
        @DisplayName("Should set and get missingSubclauses")
        void setAndGetMissingSubclauses() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            List<String> subclauses = Arrays.asList("5.1.1", "5.1.2", "5.2");
            dto.setMissingSubclauses(subclauses);
            assertEquals(subclauses, dto.getMissingSubclauses());
        }

        @Test
        @DisplayName("Should set and get sectionWeight")
        void setAndGetSectionWeight() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setSectionWeight(20);
            assertEquals(20, dto.getSectionWeight());
        }
    }

    @Nested
    @DisplayName("Severity Values Tests")
    class SeverityValuesTests {

        @Test
        @DisplayName("Should handle LOW severity")
        void severityLow() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setSeverity("LOW");
            assertEquals("LOW", dto.getSeverity());
        }

        @Test
        @DisplayName("Should handle MEDIUM severity")
        void severityMedium() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setSeverity("MEDIUM");
            assertEquals("MEDIUM", dto.getSeverity());
        }

        @Test
        @DisplayName("Should handle HIGH severity")
        void severityHigh() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setSeverity("HIGH");
            assertEquals("HIGH", dto.getSeverity());
        }

        @Test
        @DisplayName("Should handle CRITICAL severity")
        void severityCritical() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setSeverity("CRITICAL");
            assertEquals("CRITICAL", dto.getSeverity());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty missing subclauses list")
        void emptyMissingSubclauses() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setMissingSubclauses(Collections.emptyList());
            assertTrue(dto.getMissingSubclauses().isEmpty());
        }

        @Test
        @DisplayName("Should handle zero score")
        void zeroScore() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setSectionScore(0.0);
            assertEquals(0.0, dto.getSectionScore());
        }

        @Test
        @DisplayName("Should handle perfect score")
        void perfectScore() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setSectionScore(100.0);
            assertEquals(100.0, dto.getSectionScore());
        }

        @Test
        @DisplayName("Should handle zero coverage")
        void zeroCoverage() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setCoverage(0.0);
            assertEquals(0.0, dto.getCoverage());
        }

        @Test
        @DisplayName("Should handle full coverage")
        void fullCoverage() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setCoverage(100.0);
            assertEquals(100.0, dto.getCoverage());
        }

        @Test
        @DisplayName("Should handle null values")
        void nullValues() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO(
                    null, null, false, null, null, null,
                    null, null, null, null, null, null);
            assertNull(dto.getId());
            assertNull(dto.getSectionName());
            assertNull(dto.getSectionScore());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            List<String> missing = Arrays.asList("1.1");
            SectionAnalysisDTO dto1 = new SectionAnalysisDTO(
                    1L, "Overview", true, 85.0, "findings", "recs",
                    5, 90.0, "LOW", "snippet", missing, 10);
            SectionAnalysisDTO dto2 = new SectionAnalysisDTO(
                    1L, "Overview", true, 85.0, "findings", "recs",
                    5, 90.0, "LOW", "snippet", missing, 10);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different values")
        void equals_DifferentValues_ReturnsFalse() {
            SectionAnalysisDTO dto1 = new SectionAnalysisDTO();
            dto1.setId(1L);
            SectionAnalysisDTO dto2 = new SectionAnalysisDTO();
            dto2.setId(2L);

            assertNotEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with all fields")
        void toString_ContainsAllFields() {
            SectionAnalysisDTO dto = new SectionAnalysisDTO();
            dto.setId(1L);
            dto.setSectionName("Overview");
            dto.setSectionScore(85.0);
            dto.setPresent(true);

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("sectionName=Overview"));
            assertTrue(result.contains("sectionScore=85.0"));
            assertTrue(result.contains("present=true"));
        }
    }
}
