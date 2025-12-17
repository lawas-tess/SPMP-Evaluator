package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ComplianceScore entity.
 */
@DisplayName("ComplianceScore Entity Tests")
class ComplianceScoreTest {

    private ComplianceScore complianceScore;
    private SPMPDocument document;
    private User reviewer;

    @BeforeEach
    void setUp() {
        complianceScore = new ComplianceScore();
        document = new SPMPDocument();
        document.setId(1L);
        reviewer = new User();
        reviewer.setId(1L);
        reviewer.setUsername("professor");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            ComplianceScore entity = new ComplianceScore();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getDocument());
            assertNull(entity.getOverallScore());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime now = LocalDateTime.now();
            List<SectionAnalysis> analyses = new ArrayList<>();

            ComplianceScore entity = new ComplianceScore(
                    1L, document, 85.0, 90.0, 80.0, 11, 9,
                    true, "Good compliance", analyses, now,
                    92.0, "Excellent work", reviewer, now);

            assertEquals(1L, entity.getId());
            assertEquals(document, entity.getDocument());
            assertEquals(85.0, entity.getOverallScore());
            assertEquals(90.0, entity.getStructureScore());
            assertEquals(80.0, entity.getCompletenessScore());
            assertEquals(11, entity.getTotalSectionsRequired());
            assertEquals(9, entity.getSectionsFound());
            assertTrue(entity.isCompliant());
            assertEquals("Good compliance", entity.getSummary());
            assertEquals(analyses, entity.getSectionAnalyses());
            assertEquals(now, entity.getEvaluatedAt());
            assertEquals(92.0, entity.getProfessorOverride());
            assertEquals("Excellent work", entity.getProfessorNotes());
            assertEquals(reviewer, entity.getReviewedBy());
            assertEquals(now, entity.getReviewedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            complianceScore.setId(100L);
            assertEquals(100L, complianceScore.getId());
        }

        @Test
        @DisplayName("Should set and get document")
        void setAndGetDocument() {
            complianceScore.setDocument(document);
            assertEquals(document, complianceScore.getDocument());
        }

        @Test
        @DisplayName("Should set and get overallScore")
        void setAndGetOverallScore() {
            complianceScore.setOverallScore(85.5);
            assertEquals(85.5, complianceScore.getOverallScore());
        }

        @Test
        @DisplayName("Should set and get structureScore")
        void setAndGetStructureScore() {
            complianceScore.setStructureScore(90.0);
            assertEquals(90.0, complianceScore.getStructureScore());
        }

        @Test
        @DisplayName("Should set and get completenessScore")
        void setAndGetCompletenessScore() {
            complianceScore.setCompletenessScore(80.0);
            assertEquals(80.0, complianceScore.getCompletenessScore());
        }

        @Test
        @DisplayName("Should set and get totalSectionsRequired")
        void setAndGetTotalSectionsRequired() {
            complianceScore.setTotalSectionsRequired(11);
            assertEquals(11, complianceScore.getTotalSectionsRequired());
        }

        @Test
        @DisplayName("Should set and get sectionsFound")
        void setAndGetSectionsFound() {
            complianceScore.setSectionsFound(9);
            assertEquals(9, complianceScore.getSectionsFound());
        }

        @Test
        @DisplayName("Should set and get compliant")
        void setAndGetCompliant() {
            complianceScore.setCompliant(true);
            assertTrue(complianceScore.isCompliant());

            complianceScore.setCompliant(false);
            assertFalse(complianceScore.isCompliant());
        }

        @Test
        @DisplayName("Should set and get summary")
        void setAndGetSummary() {
            complianceScore.setSummary("Document meets requirements");
            assertEquals("Document meets requirements", complianceScore.getSummary());
        }

        @Test
        @DisplayName("Should set and get sectionAnalyses")
        void setAndGetSectionAnalyses() {
            List<SectionAnalysis> analyses = new ArrayList<>();
            SectionAnalysis analysis = new SectionAnalysis();
            analyses.add(analysis);

            complianceScore.setSectionAnalyses(analyses);
            assertEquals(analyses, complianceScore.getSectionAnalyses());
            assertEquals(1, complianceScore.getSectionAnalyses().size());
        }

        @Test
        @DisplayName("Should set and get evaluatedAt")
        void setAndGetEvaluatedAt() {
            LocalDateTime now = LocalDateTime.now();
            complianceScore.setEvaluatedAt(now);
            assertEquals(now, complianceScore.getEvaluatedAt());
        }

        @Test
        @DisplayName("Should set and get professorOverride")
        void setAndGetProfessorOverride() {
            complianceScore.setProfessorOverride(95.0);
            assertEquals(95.0, complianceScore.getProfessorOverride());
        }

        @Test
        @DisplayName("Should set and get professorNotes")
        void setAndGetProfessorNotes() {
            complianceScore.setProfessorNotes("Great improvement");
            assertEquals("Great improvement", complianceScore.getProfessorNotes());
        }

        @Test
        @DisplayName("Should set and get reviewedBy")
        void setAndGetReviewedBy() {
            complianceScore.setReviewedBy(reviewer);
            assertEquals(reviewer, complianceScore.getReviewedBy());
        }

        @Test
        @DisplayName("Should set and get reviewedAt")
        void setAndGetReviewedAt() {
            LocalDateTime now = LocalDateTime.now();
            complianceScore.setReviewedAt(now);
            assertEquals(now, complianceScore.getReviewedAt());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have default totalSectionsRequired of 11")
        void defaultTotalSectionsRequired() {
            ComplianceScore newScore = new ComplianceScore();
            assertEquals(11, newScore.getTotalSectionsRequired());
        }

        @Test
        @DisplayName("Should have default sectionsFound of 0")
        void defaultSectionsFound() {
            ComplianceScore newScore = new ComplianceScore();
            assertEquals(0, newScore.getSectionsFound());
        }
    }

    @Nested
    @DisplayName("Score Validation Tests")
    class ScoreValidationTests {

        @Test
        @DisplayName("Should handle zero score")
        void zeroScore() {
            complianceScore.setOverallScore(0.0);
            assertEquals(0.0, complianceScore.getOverallScore());
        }

        @Test
        @DisplayName("Should handle perfect score")
        void perfectScore() {
            complianceScore.setOverallScore(100.0);
            assertEquals(100.0, complianceScore.getOverallScore());
        }

        @Test
        @DisplayName("Should handle decimal scores")
        void decimalScore() {
            complianceScore.setOverallScore(85.75);
            assertEquals(85.75, complianceScore.getOverallScore());
        }
    }

    @Nested
    @DisplayName("Compliance Threshold Tests")
    class ComplianceThresholdTests {

        @Test
        @DisplayName("Should be compliant when score >= 80")
        void compliantAboveThreshold() {
            complianceScore.setOverallScore(80.0);
            complianceScore.setCompliant(true);
            assertTrue(complianceScore.isCompliant());
        }

        @Test
        @DisplayName("Should not be compliant when score < 80")
        void notCompliantBelowThreshold() {
            complianceScore.setOverallScore(79.0);
            complianceScore.setCompliant(false);
            assertFalse(complianceScore.isCompliant());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            LocalDateTime now = LocalDateTime.now();
            ComplianceScore score1 = new ComplianceScore(
                    1L, document, 85.0, 90.0, 80.0, 11, 9,
                    true, "summary", null, now, null, null, null, null);
            ComplianceScore score2 = new ComplianceScore(
                    1L, document, 85.0, 90.0, 80.0, 11, 9,
                    true, "summary", null, now, null, null, null, null);

            assertEquals(score1, score2);
            assertEquals(score1.hashCode(), score2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different IDs")
        void equals_DifferentIds_ReturnsFalse() {
            ComplianceScore score1 = new ComplianceScore();
            score1.setId(1L);
            ComplianceScore score2 = new ComplianceScore();
            score2.setId(2L);

            assertNotEquals(score1, score2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with fields")
        void toString_ContainsFields() {
            complianceScore.setId(1L);
            complianceScore.setOverallScore(85.0);
            complianceScore.setCompliant(true);

            String result = complianceScore.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("overallScore=85.0"));
            assertTrue(result.contains("compliant=true"));
        }
    }
}
