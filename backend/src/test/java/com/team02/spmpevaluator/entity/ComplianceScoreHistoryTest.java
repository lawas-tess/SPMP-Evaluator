package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ComplianceScoreHistory entity.
 */
@DisplayName("ComplianceScoreHistory Entity Tests")
class ComplianceScoreHistoryTest {

    private ComplianceScoreHistory history;
    private SPMPDocument document;

    @BeforeEach
    void setUp() {
        history = new ComplianceScoreHistory();
        document = new SPMPDocument();
        document.setId(1L);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            ComplianceScoreHistory entity = new ComplianceScoreHistory();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getDocument());
            assertNull(entity.getOverallScore());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime evaluatedAt = LocalDateTime.now().minusDays(1);
            LocalDateTime recordedAt = LocalDateTime.now();

            ComplianceScoreHistory entity = new ComplianceScoreHistory(
                    1L, document, 85.0, 90.0, 80.0, 9, 11,
                    true, 92.0, "Good work", "Summary text",
                    evaluatedAt, recordedAt, 5L, 2, "AI_EVALUATION");

            assertEquals(1L, entity.getId());
            assertEquals(document, entity.getDocument());
            assertEquals(85.0, entity.getOverallScore());
            assertEquals(90.0, entity.getStructureScore());
            assertEquals(80.0, entity.getCompletenessScore());
            assertEquals(9, entity.getSectionsFound());
            assertEquals(11, entity.getTotalSectionsRequired());
            assertTrue(entity.isCompliant());
            assertEquals(92.0, entity.getProfessorOverride());
            assertEquals("Good work", entity.getProfessorNotes());
            assertEquals("Summary text", entity.getSummary());
            assertEquals(evaluatedAt, entity.getEvaluatedAt());
            assertEquals(recordedAt, entity.getRecordedAt());
            assertEquals(5L, entity.getRecordedByUserId());
            assertEquals(2, entity.getVersionNumber());
            assertEquals("AI_EVALUATION", entity.getSource());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            history.setId(100L);
            assertEquals(100L, history.getId());
        }

        @Test
        @DisplayName("Should set and get document")
        void setAndGetDocument() {
            history.setDocument(document);
            assertEquals(document, history.getDocument());
        }

        @Test
        @DisplayName("Should set and get overallScore")
        void setAndGetOverallScore() {
            history.setOverallScore(85.5);
            assertEquals(85.5, history.getOverallScore());
        }

        @Test
        @DisplayName("Should set and get structureScore")
        void setAndGetStructureScore() {
            history.setStructureScore(90.0);
            assertEquals(90.0, history.getStructureScore());
        }

        @Test
        @DisplayName("Should set and get completenessScore")
        void setAndGetCompletenessScore() {
            history.setCompletenessScore(80.0);
            assertEquals(80.0, history.getCompletenessScore());
        }

        @Test
        @DisplayName("Should set and get sectionsFound")
        void setAndGetSectionsFound() {
            history.setSectionsFound(9);
            assertEquals(9, history.getSectionsFound());
        }

        @Test
        @DisplayName("Should set and get totalSectionsRequired")
        void setAndGetTotalSectionsRequired() {
            history.setTotalSectionsRequired(11);
            assertEquals(11, history.getTotalSectionsRequired());
        }

        @Test
        @DisplayName("Should set and get compliant")
        void setAndGetCompliant() {
            history.setCompliant(true);
            assertTrue(history.isCompliant());

            history.setCompliant(false);
            assertFalse(history.isCompliant());
        }

        @Test
        @DisplayName("Should set and get professorOverride")
        void setAndGetProfessorOverride() {
            history.setProfessorOverride(95.0);
            assertEquals(95.0, history.getProfessorOverride());
        }

        @Test
        @DisplayName("Should set and get professorNotes")
        void setAndGetProfessorNotes() {
            history.setProfessorNotes("Improved significantly");
            assertEquals("Improved significantly", history.getProfessorNotes());
        }

        @Test
        @DisplayName("Should set and get summary")
        void setAndGetSummary() {
            history.setSummary("Evaluation summary");
            assertEquals("Evaluation summary", history.getSummary());
        }

        @Test
        @DisplayName("Should set and get evaluatedAt")
        void setAndGetEvaluatedAt() {
            LocalDateTime time = LocalDateTime.now();
            history.setEvaluatedAt(time);
            assertEquals(time, history.getEvaluatedAt());
        }

        @Test
        @DisplayName("Should set and get recordedAt")
        void setAndGetRecordedAt() {
            LocalDateTime time = LocalDateTime.now();
            history.setRecordedAt(time);
            assertEquals(time, history.getRecordedAt());
        }

        @Test
        @DisplayName("Should set and get recordedByUserId")
        void setAndGetRecordedByUserId() {
            history.setRecordedByUserId(5L);
            assertEquals(5L, history.getRecordedByUserId());
        }

        @Test
        @DisplayName("Should set and get versionNumber")
        void setAndGetVersionNumber() {
            history.setVersionNumber(3);
            assertEquals(3, history.getVersionNumber());
        }

        @Test
        @DisplayName("Should set and get source")
        void setAndGetSource() {
            history.setSource("OVERRIDE");
            assertEquals("OVERRIDE", history.getSource());
        }
    }

    @Nested
    @DisplayName("Source Values Tests")
    class SourceValuesTests {

        @Test
        @DisplayName("Should handle AI_EVALUATION source")
        void sourceAiEvaluation() {
            history.setSource("AI_EVALUATION");
            assertEquals("AI_EVALUATION", history.getSource());
        }

        @Test
        @DisplayName("Should handle RE_EVALUATION source")
        void sourceReEvaluation() {
            history.setSource("RE_EVALUATION");
            assertEquals("RE_EVALUATION", history.getSource());
        }

        @Test
        @DisplayName("Should handle OVERRIDE source")
        void sourceOverride() {
            history.setSource("OVERRIDE");
            assertEquals("OVERRIDE", history.getSource());
        }
    }

    @Nested
    @DisplayName("Version Tracking Tests")
    class VersionTrackingTests {

        @Test
        @DisplayName("Should track version 1 for initial evaluation")
        void initialVersion() {
            history.setVersionNumber(1);
            assertEquals(1, history.getVersionNumber());
        }

        @Test
        @DisplayName("Should increment version for re-evaluation")
        void incrementVersion() {
            history.setVersionNumber(1);
            history.setVersionNumber(history.getVersionNumber() + 1);
            assertEquals(2, history.getVersionNumber());
        }

        @Test
        @DisplayName("Should track multiple versions")
        void multipleVersions() {
            for (int i = 1; i <= 5; i++) {
                history.setVersionNumber(i);
                assertEquals(i, history.getVersionNumber());
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null values")
        void nullValues() {
            ComplianceScoreHistory entity = new ComplianceScoreHistory(
                    null, null, null, null, null, null, null,
                    false, null, null, null, null, null, null, null, null);
            assertNull(entity.getId());
            assertNull(entity.getDocument());
            assertNull(entity.getOverallScore());
        }

        @Test
        @DisplayName("Should handle zero score")
        void zeroScore() {
            history.setOverallScore(0.0);
            assertEquals(0.0, history.getOverallScore());
        }

        @Test
        @DisplayName("Should handle perfect score")
        void perfectScore() {
            history.setOverallScore(100.0);
            assertEquals(100.0, history.getOverallScore());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            LocalDateTime now = LocalDateTime.now();
            ComplianceScoreHistory h1 = new ComplianceScoreHistory(
                    1L, document, 85.0, 90.0, 80.0, 9, 11,
                    true, null, null, null, now, now, null, 1, "AI_EVALUATION");
            ComplianceScoreHistory h2 = new ComplianceScoreHistory(
                    1L, document, 85.0, 90.0, 80.0, 9, 11,
                    true, null, null, null, now, now, null, 1, "AI_EVALUATION");

            assertEquals(h1, h2);
            assertEquals(h1.hashCode(), h2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different IDs")
        void equals_DifferentIds_ReturnsFalse() {
            ComplianceScoreHistory h1 = new ComplianceScoreHistory();
            h1.setId(1L);
            ComplianceScoreHistory h2 = new ComplianceScoreHistory();
            h2.setId(2L);

            assertNotEquals(h1, h2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with fields")
        void toString_ContainsFields() {
            history.setId(1L);
            history.setOverallScore(85.0);
            history.setVersionNumber(2);
            history.setSource("AI_EVALUATION");

            String result = history.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("overallScore=85.0"));
            assertTrue(result.contains("versionNumber=2"));
            assertTrue(result.contains("source=AI_EVALUATION"));
        }
    }
}
