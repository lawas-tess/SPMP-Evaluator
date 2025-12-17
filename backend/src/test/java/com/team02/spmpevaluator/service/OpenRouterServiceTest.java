package com.team02.spmpevaluator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OpenRouterService.
 * Tests AI integration for IEEE 1058 compliance analysis.
 */
@ExtendWith(MockitoExtension.class)
class OpenRouterServiceTest {

    private OpenRouterService openRouterService;

    @BeforeEach
    void setUp() {
        openRouterService = new OpenRouterService();
    }

    @Nested
    @DisplayName("isConfigured Tests")
    class IsConfiguredTests {

        @Test
        @DisplayName("Should return false when API key is null")
        void isConfigured_NullApiKey_ReturnsFalse() {
            ReflectionTestUtils.setField(openRouterService, "apiKey", null);

            assertFalse(openRouterService.isConfigured());
        }

        @Test
        @DisplayName("Should return false when API key is empty")
        void isConfigured_EmptyApiKey_ReturnsFalse() {
            ReflectionTestUtils.setField(openRouterService, "apiKey", "");

            assertFalse(openRouterService.isConfigured());
        }

        @Test
        @DisplayName("Should return true when API key is configured")
        void isConfigured_ValidApiKey_ReturnsTrue() {
            ReflectionTestUtils.setField(openRouterService, "apiKey", "sk-test-key-12345");

            assertTrue(openRouterService.isConfigured());
        }
    }

    @Nested
    @DisplayName("analyzeDocument Tests (Mock Mode)")
    class AnalyzeDocumentMockModeTests {

        @BeforeEach
        void setUpMockMode() {
            // No API key - service will return mock analysis
            ReflectionTestUtils.setField(openRouterService, "apiKey", "");
        }

        @Test
        @DisplayName("Should return mock analysis when API key not configured")
        void analyzeDocument_NoApiKey_ReturnsMockAnalysis() {
            String documentContent = "Sample SPMP document content for testing.";

            Map<String, Object> result = openRouterService.analyzeDocument(documentContent);

            assertNotNull(result);
            assertTrue(result.containsKey("complianceScore"));
            assertTrue(result.containsKey("detectedClauses"));
            assertTrue(result.containsKey("missingClauses"));
            assertTrue(result.containsKey("recommendations"));
            assertTrue(result.containsKey("summary"));
        }

        @Test
        @DisplayName("Should return compliance score of 65 in mock mode")
        void analyzeDocument_MockMode_ReturnsDefaultScore() {
            String documentContent = "Test content";

            Map<String, Object> result = openRouterService.analyzeDocument(documentContent);

            assertEquals(65.0, result.get("complianceScore"));
        }

        @Test
        @DisplayName("Should return detected clauses in mock mode")
        @SuppressWarnings("unchecked")
        void analyzeDocument_MockMode_ReturnsDetectedClauses() {
            String documentContent = "Test content";

            Map<String, Object> result = openRouterService.analyzeDocument(documentContent);

            List<Map<String, Object>> detectedClauses = (List<Map<String, Object>>) result.get("detectedClauses");
            assertNotNull(detectedClauses);
            assertFalse(detectedClauses.isEmpty());

            // Verify Overview clause is detected
            boolean hasOverview = detectedClauses.stream()
                    .anyMatch(c -> "Overview".equals(c.get("clauseName")));
            assertTrue(hasOverview);
        }

        @Test
        @DisplayName("Should return missing clauses in mock mode")
        @SuppressWarnings("unchecked")
        void analyzeDocument_MockMode_ReturnsMissingClauses() {
            String documentContent = "Test content";

            Map<String, Object> result = openRouterService.analyzeDocument(documentContent);

            List<Map<String, Object>> missingClauses = (List<Map<String, Object>>) result.get("missingClauses");
            assertNotNull(missingClauses);
            assertFalse(missingClauses.isEmpty());

            // Verify Definitions clause is missing
            boolean hasDefinitions = missingClauses.stream()
                    .anyMatch(c -> "Definitions".equals(c.get("clauseName")));
            assertTrue(hasDefinitions);
        }

        @Test
        @DisplayName("Should return recommendations in mock mode")
        @SuppressWarnings("unchecked")
        void analyzeDocument_MockMode_ReturnsRecommendations() {
            String documentContent = "Test content";

            Map<String, Object> result = openRouterService.analyzeDocument(documentContent);

            List<Map<String, Object>> recommendations = (List<Map<String, Object>>) result.get("recommendations");
            assertNotNull(recommendations);
            assertFalse(recommendations.isEmpty());

            // Verify high priority recommendation exists
            boolean hasHighPriority = recommendations.stream()
                    .anyMatch(r -> "high".equals(r.get("priority")));
            assertTrue(hasHighPriority);
        }

        @Test
        @DisplayName("Should return summary in mock mode")
        void analyzeDocument_MockMode_ReturnsSummary() {
            String documentContent = "Test content";

            Map<String, Object> result = openRouterService.analyzeDocument(documentContent);

            String summary = (String) result.get("summary");
            assertNotNull(summary);
            assertFalse(summary.isEmpty());
            assertTrue(summary.contains("IEEE 1058"));
        }

        @Test
        @DisplayName("Should handle empty document content")
        void analyzeDocument_EmptyContent_ReturnsMockAnalysis() {
            Map<String, Object> result = openRouterService.analyzeDocument("");

            assertNotNull(result);
            assertEquals(65.0, result.get("complianceScore"));
        }

        @Test
        @DisplayName("Should handle null API key gracefully")
        void analyzeDocument_NullApiKey_ReturnsMockAnalysis() {
            ReflectionTestUtils.setField(openRouterService, "apiKey", null);

            Map<String, Object> result = openRouterService.analyzeDocument("Test content");

            assertNotNull(result);
            assertTrue(result.containsKey("complianceScore"));
        }
    }

    @Nested
    @DisplayName("generateSectionFeedback Tests (Mock Mode)")
    class GenerateSectionFeedbackMockModeTests {

        @BeforeEach
        void setUpMockMode() {
            ReflectionTestUtils.setField(openRouterService, "apiKey", "");
        }

        @Test
        @DisplayName("Should return default feedback when API not configured")
        void generateSectionFeedback_NoApiKey_ReturnsDefaultFeedback() {
            String sectionName = "Overview";
            String sectionContent = "Project overview content";

            String feedback = openRouterService.generateSectionFeedback(sectionName, sectionContent);

            assertNotNull(feedback);
            assertTrue(feedback.contains("Overview"));
            assertTrue(feedback.contains("IEEE 1058"));
        }

        @Test
        @DisplayName("Should include section name in default feedback")
        void generateSectionFeedback_NoApiKey_IncludesSectionName() {
            String sectionName = "Project Organization";
            String sectionContent = "Team structure and roles";

            String feedback = openRouterService.generateSectionFeedback(sectionName, sectionContent);

            assertTrue(feedback.contains("Project Organization"));
        }

        @Test
        @DisplayName("Should mention review requirement in default feedback")
        void generateSectionFeedback_NoApiKey_MentionsReview() {
            String sectionName = "References";
            String sectionContent = "IEEE 1058 Standard";

            String feedback = openRouterService.generateSectionFeedback(sectionName, sectionContent);

            assertTrue(feedback.contains("requires review"));
        }
    }

    @Nested
    @DisplayName("Mock Analysis Structure Tests")
    class MockAnalysisStructureTests {

        @BeforeEach
        void setUpMockMode() {
            ReflectionTestUtils.setField(openRouterService, "apiKey", "");
        }

        @Test
        @DisplayName("Detected clauses should have required fields")
        @SuppressWarnings("unchecked")
        void mockAnalysis_DetectedClausesHaveRequiredFields() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> detectedClauses = (List<Map<String, Object>>) result.get("detectedClauses");

            for (Map<String, Object> clause : detectedClauses) {
                assertTrue(clause.containsKey("clauseId"), "Missing clauseId");
                assertTrue(clause.containsKey("clauseName"), "Missing clauseName");
                assertTrue(clause.containsKey("score"), "Missing score");
                assertTrue(clause.containsKey("found"), "Missing found");
                assertTrue(clause.containsKey("location"), "Missing location");
            }
        }

        @Test
        @DisplayName("Missing clauses should have required fields")
        @SuppressWarnings("unchecked")
        void mockAnalysis_MissingClausesHaveRequiredFields() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> missingClauses = (List<Map<String, Object>>) result.get("missingClauses");

            for (Map<String, Object> clause : missingClauses) {
                assertTrue(clause.containsKey("clauseId"), "Missing clauseId");
                assertTrue(clause.containsKey("clauseName"), "Missing clauseName");
                assertTrue(clause.containsKey("severity"), "Missing severity");
                assertTrue(clause.containsKey("reason"), "Missing reason");
            }
        }

        @Test
        @DisplayName("Recommendations should have required fields")
        @SuppressWarnings("unchecked")
        void mockAnalysis_RecommendationsHaveRequiredFields() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> recommendations = (List<Map<String, Object>>) result.get("recommendations");

            for (Map<String, Object> recommendation : recommendations) {
                assertTrue(recommendation.containsKey("priority"), "Missing priority");
                assertTrue(recommendation.containsKey("recommendation"), "Missing recommendation");
                assertTrue(recommendation.containsKey("clauseRef"), "Missing clauseRef");
            }
        }

        @Test
        @DisplayName("Should have correct number of detected clauses")
        @SuppressWarnings("unchecked")
        void mockAnalysis_HasCorrectDetectedClausesCount() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> detectedClauses = (List<Map<String, Object>>) result.get("detectedClauses");

            assertEquals(3, detectedClauses.size());
        }

        @Test
        @DisplayName("Should have correct number of missing clauses")
        @SuppressWarnings("unchecked")
        void mockAnalysis_HasCorrectMissingClausesCount() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> missingClauses = (List<Map<String, Object>>) result.get("missingClauses");

            assertEquals(3, missingClauses.size());
        }

        @Test
        @DisplayName("Should have correct number of recommendations")
        @SuppressWarnings("unchecked")
        void mockAnalysis_HasCorrectRecommendationsCount() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> recommendations = (List<Map<String, Object>>) result.get("recommendations");

            assertEquals(3, recommendations.size());
        }
    }

    @Nested
    @DisplayName("IEEE 1058 Clauses Coverage Tests")
    class IEEE1058ClausesCoverageTests {

        @BeforeEach
        void setUpMockMode() {
            ReflectionTestUtils.setField(openRouterService, "apiKey", "");
        }

        @Test
        @DisplayName("Should detect Overview clause (Clause 1)")
        @SuppressWarnings("unchecked")
        void mockAnalysis_DetectsOverviewClause() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> detectedClauses = (List<Map<String, Object>>) result.get("detectedClauses");

            boolean found = detectedClauses.stream()
                    .anyMatch(c -> "1".equals(c.get("clauseId")) && "Overview".equals(c.get("clauseName")));
            assertTrue(found, "Overview clause should be detected");
        }

        @Test
        @DisplayName("Should detect References clause (Clause 2)")
        @SuppressWarnings("unchecked")
        void mockAnalysis_DetectsReferencesClause() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> detectedClauses = (List<Map<String, Object>>) result.get("detectedClauses");

            boolean found = detectedClauses.stream()
                    .anyMatch(c -> "2".equals(c.get("clauseId")) && "References".equals(c.get("clauseName")));
            assertTrue(found, "References clause should be detected");
        }

        @Test
        @DisplayName("Should mark Definitions as missing (Clause 3)")
        @SuppressWarnings("unchecked")
        void mockAnalysis_MarkDefinitionsAsMissing() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> missingClauses = (List<Map<String, Object>>) result.get("missingClauses");

            boolean found = missingClauses.stream()
                    .anyMatch(c -> "3".equals(c.get("clauseId")) && "Definitions".equals(c.get("clauseName")));
            assertTrue(found, "Definitions clause should be marked as missing");
        }

        @Test
        @DisplayName("Should detect Project Organization clause (Clause 4)")
        @SuppressWarnings("unchecked")
        void mockAnalysis_DetectsProjectOrganizationClause() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> detectedClauses = (List<Map<String, Object>>) result.get("detectedClauses");

            boolean found = detectedClauses.stream()
                    .anyMatch(c -> "4".equals(c.get("clauseId")) && "Project Organization".equals(c.get("clauseName")));
            assertTrue(found, "Project Organization clause should be detected");
        }

        @Test
        @DisplayName("Should mark Technical Process Plans as missing (Clause 6)")
        @SuppressWarnings("unchecked")
        void mockAnalysis_MarkTechnicalProcessAsMissing() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> missingClauses = (List<Map<String, Object>>) result.get("missingClauses");

            boolean found = missingClauses.stream()
                    .anyMatch(c -> "6".equals(c.get("clauseId"))
                            && "Technical Process Plans".equals(c.get("clauseName")));
            assertTrue(found, "Technical Process Plans clause should be marked as missing");
        }

        @Test
        @DisplayName("Should mark Supporting Process Plans as missing (Clause 7)")
        @SuppressWarnings("unchecked")
        void mockAnalysis_MarkSupportingProcessAsMissing() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> missingClauses = (List<Map<String, Object>>) result.get("missingClauses");

            boolean found = missingClauses.stream()
                    .anyMatch(c -> "7".equals(c.get("clauseId"))
                            && "Supporting Process Plans".equals(c.get("clauseName")));
            assertTrue(found, "Supporting Process Plans clause should be marked as missing");
        }
    }

    @Nested
    @DisplayName("Severity and Priority Tests")
    class SeverityAndPriorityTests {

        @BeforeEach
        void setUpMockMode() {
            ReflectionTestUtils.setField(openRouterService, "apiKey", "");
        }

        @Test
        @DisplayName("Missing Technical Process should have high severity")
        @SuppressWarnings("unchecked")
        void mockAnalysis_TechnicalProcessHighSeverity() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> missingClauses = (List<Map<String, Object>>) result.get("missingClauses");

            Map<String, Object> technicalProcess = missingClauses.stream()
                    .filter(c -> "Technical Process Plans".equals(c.get("clauseName")))
                    .findFirst()
                    .orElseThrow();

            assertEquals("high", technicalProcess.get("severity"));
        }

        @Test
        @DisplayName("Missing Definitions should have medium severity")
        @SuppressWarnings("unchecked")
        void mockAnalysis_DefinitionsMediumSeverity() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> missingClauses = (List<Map<String, Object>>) result.get("missingClauses");

            Map<String, Object> definitions = missingClauses.stream()
                    .filter(c -> "Definitions".equals(c.get("clauseName")))
                    .findFirst()
                    .orElseThrow();

            assertEquals("medium", definitions.get("severity"));
        }

        @Test
        @DisplayName("Should have high priority recommendations")
        @SuppressWarnings("unchecked")
        void mockAnalysis_HasHighPriorityRecommendations() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> recommendations = (List<Map<String, Object>>) result.get("recommendations");

            long highPriorityCount = recommendations.stream()
                    .filter(r -> "high".equals(r.get("priority")))
                    .count();

            assertTrue(highPriorityCount >= 2, "Should have at least 2 high priority recommendations");
        }
    }

    @Nested
    @DisplayName("Score Range Tests")
    class ScoreRangeTests {

        @BeforeEach
        void setUpMockMode() {
            ReflectionTestUtils.setField(openRouterService, "apiKey", "");
        }

        @Test
        @DisplayName("Compliance score should be between 0 and 100")
        void mockAnalysis_ComplianceScoreInRange() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            Double score = (Double) result.get("complianceScore");

            assertTrue(score >= 0 && score <= 100, "Compliance score should be between 0 and 100");
        }

        @Test
        @DisplayName("Clause scores should be between 0 and 100")
        @SuppressWarnings("unchecked")
        void mockAnalysis_ClauseScoresInRange() {
            Map<String, Object> result = openRouterService.analyzeDocument("Test");
            List<Map<String, Object>> detectedClauses = (List<Map<String, Object>>) result.get("detectedClauses");

            for (Map<String, Object> clause : detectedClauses) {
                Integer score = (Integer) clause.get("score");
                assertTrue(score >= 0 && score <= 100,
                        "Clause score should be between 0 and 100 for " + clause.get("clauseName"));
            }
        }
    }
}
