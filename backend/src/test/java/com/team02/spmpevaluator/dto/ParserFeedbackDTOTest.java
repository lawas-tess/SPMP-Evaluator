package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ParserFeedbackDTO.
 */
@DisplayName("ParserFeedbackDTO Tests")
class ParserFeedbackDTOTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();

            assertNotNull(dto);
            assertNull(dto.getId());
            assertNull(dto.getDocumentId());
            assertNull(dto.getComplianceScore());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO(
                    1L, 2L, "document.pdf", 3L, "Default Config",
                    85.5, "overview,schedule", "risk,budget",
                    "Add risk section", "Full analysis report",
                    "2025-12-18T10:00:00", "1.0.0", "COMPLETED", null);

            assertEquals(1L, dto.getId());
            assertEquals(2L, dto.getDocumentId());
            assertEquals("document.pdf", dto.getDocumentName());
            assertEquals(3L, dto.getParserConfigId());
            assertEquals("Default Config", dto.getParserConfigName());
            assertEquals(85.5, dto.getComplianceScore());
            assertEquals("overview,schedule", dto.getDetectedClauses());
            assertEquals("risk,budget", dto.getMissingClauses());
            assertEquals("Add risk section", dto.getRecommendations());
            assertEquals("Full analysis report", dto.getAnalysisReport());
            assertEquals("2025-12-18T10:00:00", dto.getAnalyzedAt());
            assertEquals("1.0.0", dto.getParserVersion());
            assertEquals("COMPLETED", dto.getStatus());
            assertNull(dto.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setId(100L);
            assertEquals(100L, dto.getId());
        }

        @Test
        @DisplayName("Should set and get documentId")
        void setAndGetDocumentId() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setDocumentId(200L);
            assertEquals(200L, dto.getDocumentId());
        }

        @Test
        @DisplayName("Should set and get documentName")
        void setAndGetDocumentName() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setDocumentName("test.pdf");
            assertEquals("test.pdf", dto.getDocumentName());
        }

        @Test
        @DisplayName("Should set and get parserConfigId")
        void setAndGetParserConfigId() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setParserConfigId(50L);
            assertEquals(50L, dto.getParserConfigId());
        }

        @Test
        @DisplayName("Should set and get parserConfigName")
        void setAndGetParserConfigName() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setParserConfigName("Custom Parser");
            assertEquals("Custom Parser", dto.getParserConfigName());
        }

        @Test
        @DisplayName("Should set and get complianceScore")
        void setAndGetComplianceScore() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setComplianceScore(92.5);
            assertEquals(92.5, dto.getComplianceScore());
        }

        @Test
        @DisplayName("Should set and get detectedClauses")
        void setAndGetDetectedClauses() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setDetectedClauses("clause1,clause2,clause3");
            assertEquals("clause1,clause2,clause3", dto.getDetectedClauses());
        }

        @Test
        @DisplayName("Should set and get missingClauses")
        void setAndGetMissingClauses() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setMissingClauses("clause4,clause5");
            assertEquals("clause4,clause5", dto.getMissingClauses());
        }

        @Test
        @DisplayName("Should set and get recommendations")
        void setAndGetRecommendations() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setRecommendations("Add missing sections");
            assertEquals("Add missing sections", dto.getRecommendations());
        }

        @Test
        @DisplayName("Should set and get analysisReport")
        void setAndGetAnalysisReport() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setAnalysisReport("Detailed analysis...");
            assertEquals("Detailed analysis...", dto.getAnalysisReport());
        }

        @Test
        @DisplayName("Should set and get analyzedAt")
        void setAndGetAnalyzedAt() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setAnalyzedAt("2025-12-18T15:30:00");
            assertEquals("2025-12-18T15:30:00", dto.getAnalyzedAt());
        }

        @Test
        @DisplayName("Should set and get parserVersion")
        void setAndGetParserVersion() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setParserVersion("2.0.0");
            assertEquals("2.0.0", dto.getParserVersion());
        }

        @Test
        @DisplayName("Should set and get status")
        void setAndGetStatus() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setStatus("PENDING");
            assertEquals("PENDING", dto.getStatus());
        }

        @Test
        @DisplayName("Should set and get errorMessage")
        void setAndGetErrorMessage() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setErrorMessage("Parsing failed");
            assertEquals("Parsing failed", dto.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("Status Values Tests")
    class StatusValuesTests {

        @Test
        @DisplayName("Should handle PENDING status")
        void statusPending() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setStatus("PENDING");
            assertEquals("PENDING", dto.getStatus());
        }

        @Test
        @DisplayName("Should handle IN_PROGRESS status")
        void statusInProgress() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setStatus("IN_PROGRESS");
            assertEquals("IN_PROGRESS", dto.getStatus());
        }

        @Test
        @DisplayName("Should handle COMPLETED status")
        void statusCompleted() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setStatus("COMPLETED");
            assertEquals("COMPLETED", dto.getStatus());
        }

        @Test
        @DisplayName("Should handle FAILED status")
        void statusFailed() {
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setStatus("FAILED");
            dto.setErrorMessage("Document format not supported");
            assertEquals("FAILED", dto.getStatus());
            assertEquals("Document format not supported", dto.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            ParserFeedbackDTO dto1 = new ParserFeedbackDTO(
                    1L, 2L, "doc.pdf", 3L, "Config", 85.0,
                    "clauses", "missing", "recs", "report",
                    "2025-01-01", "1.0", "COMPLETED", null);
            ParserFeedbackDTO dto2 = new ParserFeedbackDTO(
                    1L, 2L, "doc.pdf", 3L, "Config", 85.0,
                    "clauses", "missing", "recs", "report",
                    "2025-01-01", "1.0", "COMPLETED", null);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different values")
        void equals_DifferentValues_ReturnsFalse() {
            ParserFeedbackDTO dto1 = new ParserFeedbackDTO();
            dto1.setId(1L);
            ParserFeedbackDTO dto2 = new ParserFeedbackDTO();
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
            ParserFeedbackDTO dto = new ParserFeedbackDTO();
            dto.setId(1L);
            dto.setDocumentName("test.pdf");
            dto.setComplianceScore(85.0);
            dto.setStatus("COMPLETED");

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("documentName=test.pdf"));
            assertTrue(result.contains("complianceScore=85.0"));
            assertTrue(result.contains("status=COMPLETED"));
        }
    }
}
