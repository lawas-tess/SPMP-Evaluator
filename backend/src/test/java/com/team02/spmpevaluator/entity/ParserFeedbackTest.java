package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ParserFeedback entity.
 */
@DisplayName("ParserFeedback Entity Tests")
class ParserFeedbackTest {

    private ParserFeedback feedback;
    private SPMPDocument document;
    private ParserConfiguration config;

    @BeforeEach
    void setUp() {
        feedback = new ParserFeedback();
        document = new SPMPDocument();
        document.setId(1L);
        config = new ParserConfiguration();
        config.setId(1L);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            ParserFeedback entity = new ParserFeedback();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getDocument());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime now = LocalDateTime.now();
            ParserFeedback entity = new ParserFeedback(
                    1L, document, config, 85.5,
                    "[{\"clauseId\": \"1.1\"}]", "[{\"clauseId\": \"2.3\"}]",
                    "[{\"priority\": \"high\"}]", "Detailed report",
                    now, "v1.0", ParserFeedback.FeedbackStatus.COMPLETED, null);

            assertEquals(1L, entity.getId());
            assertEquals(document, entity.getDocument());
            assertEquals(config, entity.getParserConfiguration());
            assertEquals(85.5, entity.getComplianceScore());
            assertEquals(now, entity.getAnalyzedAt());
            assertEquals("v1.0", entity.getParserVersion());
            assertEquals(ParserFeedback.FeedbackStatus.COMPLETED, entity.getStatus());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testId() {
            feedback.setId(100L);
            assertEquals(100L, feedback.getId());
        }

        @Test
        @DisplayName("Should set and get document")
        void testDocument() {
            feedback.setDocument(document);
            assertEquals(document, feedback.getDocument());
        }

        @Test
        @DisplayName("Should set and get parserConfiguration")
        void testParserConfiguration() {
            feedback.setParserConfiguration(config);
            assertEquals(config, feedback.getParserConfiguration());
        }

        @Test
        @DisplayName("Should set and get complianceScore")
        void testComplianceScore() {
            feedback.setComplianceScore(92.5);
            assertEquals(92.5, feedback.getComplianceScore());
        }

        @Test
        @DisplayName("Should set and get detectedClauses")
        void testDetectedClauses() {
            String clauses = "[{\"clauseId\": \"1.1\", \"clauseName\": \"Purpose\", \"score\": 85}]";
            feedback.setDetectedClauses(clauses);
            assertEquals(clauses, feedback.getDetectedClauses());
        }

        @Test
        @DisplayName("Should set and get missingClauses")
        void testMissingClauses() {
            String missing = "[{\"clauseId\": \"2.3\", \"clauseName\": \"Risk Management\", \"severity\": \"high\"}]";
            feedback.setMissingClauses(missing);
            assertEquals(missing, feedback.getMissingClauses());
        }

        @Test
        @DisplayName("Should set and get recommendations")
        void testRecommendations() {
            String recs = "[{\"priority\": \"high\", \"recommendation\": \"Add risk management section\"}]";
            feedback.setRecommendations(recs);
            assertEquals(recs, feedback.getRecommendations());
        }

        @Test
        @DisplayName("Should set and get analysisReport")
        void testAnalysisReport() {
            feedback.setAnalysisReport("Detailed analysis of the SPMP document...");
            assertEquals("Detailed analysis of the SPMP document...", feedback.getAnalysisReport());
        }

        @Test
        @DisplayName("Should set and get analyzedAt")
        void testAnalyzedAt() {
            LocalDateTime now = LocalDateTime.now();
            feedback.setAnalyzedAt(now);
            assertEquals(now, feedback.getAnalyzedAt());
        }

        @Test
        @DisplayName("Should set and get parserVersion")
        void testParserVersion() {
            feedback.setParserVersion("v2.1.0");
            assertEquals("v2.1.0", feedback.getParserVersion());
        }

        @Test
        @DisplayName("Should set and get status")
        void testStatus() {
            feedback.setStatus(ParserFeedback.FeedbackStatus.COMPLETED);
            assertEquals(ParserFeedback.FeedbackStatus.COMPLETED, feedback.getStatus());
        }

        @Test
        @DisplayName("Should set and get errorMessage")
        void testErrorMessage() {
            feedback.setErrorMessage("Document parsing failed: invalid format");
            assertEquals("Document parsing failed: invalid format", feedback.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("FeedbackStatus Enum Tests")
    class FeedbackStatusEnumTests {

        @Test
        @DisplayName("Should have PENDING status")
        void pendingStatus() {
            assertEquals("PENDING", ParserFeedback.FeedbackStatus.PENDING.name());
        }

        @Test
        @DisplayName("Should have IN_PROGRESS status")
        void inProgressStatus() {
            assertEquals("IN_PROGRESS", ParserFeedback.FeedbackStatus.IN_PROGRESS.name());
        }

        @Test
        @DisplayName("Should have COMPLETED status")
        void completedStatus() {
            assertEquals("COMPLETED", ParserFeedback.FeedbackStatus.COMPLETED.name());
        }

        @Test
        @DisplayName("Should have FAILED status")
        void failedStatus() {
            assertEquals("FAILED", ParserFeedback.FeedbackStatus.FAILED.name());
        }

        @Test
        @DisplayName("Should have exactly 4 statuses")
        void statusCount() {
            assertEquals(4, ParserFeedback.FeedbackStatus.values().length);
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have PENDING as default status")
        void defaultStatusIsPending() {
            ParserFeedback entity = new ParserFeedback();
            assertEquals(ParserFeedback.FeedbackStatus.PENDING, entity.getStatus());
        }
    }

    @Nested
    @DisplayName("Score Validation Tests")
    class ScoreValidationTests {

        @Test
        @DisplayName("Should accept score of 0")
        void zeroScore() {
            feedback.setComplianceScore(0.0);
            assertEquals(0.0, feedback.getComplianceScore());
        }

        @Test
        @DisplayName("Should accept score of 100")
        void perfectScore() {
            feedback.setComplianceScore(100.0);
            assertEquals(100.0, feedback.getComplianceScore());
        }

        @Test
        @DisplayName("Should accept decimal scores")
        void decimalScore() {
            feedback.setComplianceScore(87.5);
            assertEquals(87.5, feedback.getComplianceScore());
        }
    }

    @Nested
    @DisplayName("Status Workflow Tests")
    class StatusWorkflowTests {

        @Test
        @DisplayName("Should transition from PENDING to IN_PROGRESS")
        void pendingToInProgress() {
            feedback.setStatus(ParserFeedback.FeedbackStatus.PENDING);
            feedback.setStatus(ParserFeedback.FeedbackStatus.IN_PROGRESS);
            assertEquals(ParserFeedback.FeedbackStatus.IN_PROGRESS, feedback.getStatus());
        }

        @Test
        @DisplayName("Should transition from IN_PROGRESS to COMPLETED")
        void inProgressToCompleted() {
            feedback.setStatus(ParserFeedback.FeedbackStatus.IN_PROGRESS);
            feedback.setStatus(ParserFeedback.FeedbackStatus.COMPLETED);
            assertEquals(ParserFeedback.FeedbackStatus.COMPLETED, feedback.getStatus());
        }

        @Test
        @DisplayName("Should transition from IN_PROGRESS to FAILED")
        void inProgressToFailed() {
            feedback.setStatus(ParserFeedback.FeedbackStatus.IN_PROGRESS);
            feedback.setStatus(ParserFeedback.FeedbackStatus.FAILED);
            feedback.setErrorMessage("Parsing error occurred");
            assertEquals(ParserFeedback.FeedbackStatus.FAILED, feedback.getStatus());
            assertNotNull(feedback.getErrorMessage());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same id")
        void equalsWithSameId() {
            ParserFeedback feedback1 = new ParserFeedback();
            feedback1.setId(1L);

            ParserFeedback feedback2 = new ParserFeedback();
            feedback2.setId(1L);

            assertEquals(feedback1, feedback2);
            assertEquals(feedback1.hashCode(), feedback2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different ids")
        void notEqualsWithDifferentIds() {
            ParserFeedback feedback1 = new ParserFeedback();
            feedback1.setId(1L);

            ParserFeedback feedback2 = new ParserFeedback();
            feedback2.setId(2L);

            assertNotEquals(feedback1, feedback2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void toStringContainsFields() {
            feedback.setId(1L);
            feedback.setComplianceScore(85.0);
            feedback.setStatus(ParserFeedback.FeedbackStatus.COMPLETED);

            String str = feedback.toString();
            assertNotNull(str);
            assertTrue(str.contains("1"));
        }
    }
}
