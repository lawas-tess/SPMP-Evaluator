package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ComplianceScoreHistoryDTO.
 */
@DisplayName("ComplianceScoreHistoryDTO Tests")
class ComplianceScoreHistoryDTOTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();

            assertNotNull(dto);
            assertNull(dto.getId());
            assertNull(dto.getOverallScore());
            assertNull(dto.getSource());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setId(100L);
            assertEquals(100L, dto.getId());
        }

        @Test
        @DisplayName("Should set and get overallScore")
        void setAndGetOverallScore() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setOverallScore(85.5);
            assertEquals(85.5, dto.getOverallScore());
        }

        @Test
        @DisplayName("Should set and get structureScore")
        void setAndGetStructureScore() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setStructureScore(90.0);
            assertEquals(90.0, dto.getStructureScore());
        }

        @Test
        @DisplayName("Should set and get completenessScore")
        void setAndGetCompletenessScore() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setCompletenessScore(80.0);
            assertEquals(80.0, dto.getCompletenessScore());
        }

        @Test
        @DisplayName("Should set and get sectionsFound")
        void setAndGetSectionsFound() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setSectionsFound(10);
            assertEquals(10, dto.getSectionsFound());
        }

        @Test
        @DisplayName("Should set and get totalSectionsRequired")
        void setAndGetTotalSectionsRequired() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setTotalSectionsRequired(12);
            assertEquals(12, dto.getTotalSectionsRequired());
        }

        @Test
        @DisplayName("Should set and get compliant")
        void setAndGetCompliant() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setCompliant(true);
            assertTrue(dto.isCompliant());

            dto.setCompliant(false);
            assertFalse(dto.isCompliant());
        }

        @Test
        @DisplayName("Should set and get professorOverride")
        void setAndGetProfessorOverride() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setProfessorOverride(95.0);
            assertEquals(95.0, dto.getProfessorOverride());
        }

        @Test
        @DisplayName("Should set and get professorNotes")
        void setAndGetProfessorNotes() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setProfessorNotes("Good improvement");
            assertEquals("Good improvement", dto.getProfessorNotes());
        }

        @Test
        @DisplayName("Should set and get summary")
        void setAndGetSummary() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setSummary("Historical summary");
            assertEquals("Historical summary", dto.getSummary());
        }

        @Test
        @DisplayName("Should set and get evaluatedAt")
        void setAndGetEvaluatedAt() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            LocalDateTime now = LocalDateTime.now();
            dto.setEvaluatedAt(now);
            assertEquals(now, dto.getEvaluatedAt());
        }

        @Test
        @DisplayName("Should set and get recordedAt")
        void setAndGetRecordedAt() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            LocalDateTime now = LocalDateTime.now();
            dto.setRecordedAt(now);
            assertEquals(now, dto.getRecordedAt());
        }

        @Test
        @DisplayName("Should set and get versionNumber")
        void setAndGetVersionNumber() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setVersionNumber(3);
            assertEquals(3, dto.getVersionNumber());
        }

        @Test
        @DisplayName("Should set and get source")
        void setAndGetSource() {
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setSource("AUTOMATED");
            assertEquals("AUTOMATED", dto.getSource());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            ComplianceScoreHistoryDTO dto1 = new ComplianceScoreHistoryDTO();
            dto1.setId(1L);
            dto1.setOverallScore(85.0);
            dto1.setVersionNumber(1);

            ComplianceScoreHistoryDTO dto2 = new ComplianceScoreHistoryDTO();
            dto2.setId(1L);
            dto2.setOverallScore(85.0);
            dto2.setVersionNumber(1);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different values")
        void equals_DifferentValues_ReturnsFalse() {
            ComplianceScoreHistoryDTO dto1 = new ComplianceScoreHistoryDTO();
            dto1.setId(1L);

            ComplianceScoreHistoryDTO dto2 = new ComplianceScoreHistoryDTO();
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
            ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
            dto.setId(1L);
            dto.setOverallScore(85.0);
            dto.setVersionNumber(2);
            dto.setSource("MANUAL");

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("overallScore=85.0"));
            assertTrue(result.contains("versionNumber=2"));
            assertTrue(result.contains("source=MANUAL"));
        }
    }
}
