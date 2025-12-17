package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SectionAnalysis entity.
 */
@DisplayName("SectionAnalysis Entity Tests")
class SectionAnalysisTest {

    private SectionAnalysis analysis;
    private ComplianceScore complianceScore;

    @BeforeEach
    void setUp() {
        analysis = new SectionAnalysis();
        complianceScore = new ComplianceScore();
        complianceScore.setId(1L);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            SectionAnalysis entity = new SectionAnalysis();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getSectionName());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            SectionAnalysis entity = new SectionAnalysis(
                    1L, complianceScore,
                    SectionAnalysis.IEEE1058Section.OVERVIEW,
                    true, 85.0,
                    "Section found with good content",
                    "Consider adding more detail",
                    2, 75.5, "MEDIUM",
                    "Project Overview section...",
                    "1.1.1, 1.1.2", 10);

            assertEquals(1L, entity.getId());
            assertEquals(complianceScore, entity.getComplianceScore());
            assertEquals(SectionAnalysis.IEEE1058Section.OVERVIEW, entity.getSectionName());
            assertTrue(entity.isPresent());
            assertEquals(85.0, entity.getSectionScore());
            assertEquals("Section found with good content", entity.getFindings());
            assertEquals("Consider adding more detail", entity.getRecommendations());
            assertEquals(2, entity.getPageNumber());
            assertEquals(75.5, entity.getCoverage());
            assertEquals("MEDIUM", entity.getSeverity());
            assertEquals("Project Overview section...", entity.getEvidenceSnippet());
            assertEquals("1.1.1, 1.1.2", entity.getMissingSubclauses());
            assertEquals(10, entity.getSectionWeight());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testId() {
            analysis.setId(100L);
            assertEquals(100L, analysis.getId());
        }

        @Test
        @DisplayName("Should set and get complianceScore")
        void testComplianceScore() {
            analysis.setComplianceScore(complianceScore);
            assertEquals(complianceScore, analysis.getComplianceScore());
        }

        @Test
        @DisplayName("Should set and get sectionName")
        void testSectionName() {
            analysis.setSectionName(SectionAnalysis.IEEE1058Section.RISK_MANAGEMENT);
            assertEquals(SectionAnalysis.IEEE1058Section.RISK_MANAGEMENT, analysis.getSectionName());
        }

        @Test
        @DisplayName("Should set and get present")
        void testPresent() {
            analysis.setPresent(true);
            assertTrue(analysis.isPresent());
            analysis.setPresent(false);
            assertFalse(analysis.isPresent());
        }

        @Test
        @DisplayName("Should set and get sectionScore")
        void testSectionScore() {
            analysis.setSectionScore(92.5);
            assertEquals(92.5, analysis.getSectionScore());
        }

        @Test
        @DisplayName("Should set and get findings")
        void testFindings() {
            analysis.setFindings("The section contains all required elements.");
            assertEquals("The section contains all required elements.", analysis.getFindings());
        }

        @Test
        @DisplayName("Should set and get recommendations")
        void testRecommendations() {
            analysis.setRecommendations("Consider adding more specific milestones.");
            assertEquals("Consider adding more specific milestones.", analysis.getRecommendations());
        }

        @Test
        @DisplayName("Should set and get pageNumber")
        void testPageNumber() {
            analysis.setPageNumber(5);
            assertEquals(5, analysis.getPageNumber());
        }

        @Test
        @DisplayName("Should set and get coverage")
        void testCoverage() {
            analysis.setCoverage(85.5);
            assertEquals(85.5, analysis.getCoverage());
        }

        @Test
        @DisplayName("Should set and get severity")
        void testSeverity() {
            analysis.setSeverity("HIGH");
            assertEquals("HIGH", analysis.getSeverity());
        }

        @Test
        @DisplayName("Should set and get evidenceSnippet")
        void testEvidenceSnippet() {
            analysis.setEvidenceSnippet("The project overview describes...");
            assertEquals("The project overview describes...", analysis.getEvidenceSnippet());
        }

        @Test
        @DisplayName("Should set and get missingSubclauses")
        void testMissingSubclauses() {
            analysis.setMissingSubclauses("2.1.1, 2.1.2, 2.1.3");
            assertEquals("2.1.1, 2.1.2, 2.1.3", analysis.getMissingSubclauses());
        }

        @Test
        @DisplayName("Should set and get sectionWeight")
        void testSectionWeight() {
            analysis.setSectionWeight(15);
            assertEquals(15, analysis.getSectionWeight());
        }
    }

    @Nested
    @DisplayName("IEEE1058Section Enum Tests")
    class IEEE1058SectionEnumTests {

        @Test
        @DisplayName("Should have OVERVIEW section")
        void overviewSection() {
            assertEquals("OVERVIEW", SectionAnalysis.IEEE1058Section.OVERVIEW.name());
            assertEquals("Project Overview", SectionAnalysis.IEEE1058Section.OVERVIEW.getDisplayName());
        }

        @Test
        @DisplayName("Should have DOCUMENTATION_PLAN section")
        void documentationPlanSection() {
            assertEquals("DOCUMENTATION_PLAN", SectionAnalysis.IEEE1058Section.DOCUMENTATION_PLAN.name());
            assertEquals("Documentation Plan", SectionAnalysis.IEEE1058Section.DOCUMENTATION_PLAN.getDisplayName());
        }

        @Test
        @DisplayName("Should have MASTER_SCHEDULE section")
        void masterScheduleSection() {
            assertEquals("MASTER_SCHEDULE", SectionAnalysis.IEEE1058Section.MASTER_SCHEDULE.name());
            assertEquals("Master Schedule", SectionAnalysis.IEEE1058Section.MASTER_SCHEDULE.getDisplayName());
        }

        @Test
        @DisplayName("Should have ORGANIZATION section")
        void organizationSection() {
            assertEquals("ORGANIZATION", SectionAnalysis.IEEE1058Section.ORGANIZATION.name());
            assertEquals("Project Organization", SectionAnalysis.IEEE1058Section.ORGANIZATION.getDisplayName());
        }

        @Test
        @DisplayName("Should have STANDARDS_PRACTICES section")
        void standardsPracticesSection() {
            assertEquals("STANDARDS_PRACTICES", SectionAnalysis.IEEE1058Section.STANDARDS_PRACTICES.name());
            assertEquals("Standards, Practices, and Conventions",
                    SectionAnalysis.IEEE1058Section.STANDARDS_PRACTICES.getDisplayName());
        }

        @Test
        @DisplayName("Should have RISK_MANAGEMENT section")
        void riskManagementSection() {
            assertEquals("RISK_MANAGEMENT", SectionAnalysis.IEEE1058Section.RISK_MANAGEMENT.name());
            assertEquals("Risk Management", SectionAnalysis.IEEE1058Section.RISK_MANAGEMENT.getDisplayName());
        }

        @Test
        @DisplayName("Should have STAFF_ORGANIZATION section")
        void staffOrganizationSection() {
            assertEquals("STAFF_ORGANIZATION", SectionAnalysis.IEEE1058Section.STAFF_ORGANIZATION.name());
            assertEquals("Staff Organization", SectionAnalysis.IEEE1058Section.STAFF_ORGANIZATION.getDisplayName());
        }

        @Test
        @DisplayName("Should have BUDGET_RESOURCE section")
        void budgetResourceSection() {
            assertEquals("BUDGET_RESOURCE", SectionAnalysis.IEEE1058Section.BUDGET_RESOURCE.name());
            assertEquals("Budget and Resource Planning",
                    SectionAnalysis.IEEE1058Section.BUDGET_RESOURCE.getDisplayName());
        }

        @Test
        @DisplayName("Should have REVIEWS_AUDITS section")
        void reviewsAuditsSection() {
            assertEquals("REVIEWS_AUDITS", SectionAnalysis.IEEE1058Section.REVIEWS_AUDITS.name());
            assertEquals("Reviews and Audits", SectionAnalysis.IEEE1058Section.REVIEWS_AUDITS.getDisplayName());
        }

        @Test
        @DisplayName("Should have PROBLEM_RESOLUTION section")
        void problemResolutionSection() {
            assertEquals("PROBLEM_RESOLUTION", SectionAnalysis.IEEE1058Section.PROBLEM_RESOLUTION.name());
            assertEquals("Problem Resolution", SectionAnalysis.IEEE1058Section.PROBLEM_RESOLUTION.getDisplayName());
        }

        @Test
        @DisplayName("Should have CHANGE_MANAGEMENT section")
        void changeManagementSection() {
            assertEquals("CHANGE_MANAGEMENT", SectionAnalysis.IEEE1058Section.CHANGE_MANAGEMENT.name());
            assertEquals("Change Management", SectionAnalysis.IEEE1058Section.CHANGE_MANAGEMENT.getDisplayName());
        }

        @Test
        @DisplayName("Should have GLOSSARY_APPENDIX section")
        void glossaryAppendixSection() {
            assertEquals("GLOSSARY_APPENDIX", SectionAnalysis.IEEE1058Section.GLOSSARY_APPENDIX.name());
            assertEquals("Glossary and Appendices", SectionAnalysis.IEEE1058Section.GLOSSARY_APPENDIX.getDisplayName());
        }

        @Test
        @DisplayName("Should have exactly 12 sections")
        void sectionCount() {
            assertEquals(12, SectionAnalysis.IEEE1058Section.values().length);
        }
    }

    @Nested
    @DisplayName("Severity Values Tests")
    class SeverityValuesTests {

        @Test
        @DisplayName("Should accept HIGH severity")
        void highSeverity() {
            analysis.setSeverity("HIGH");
            assertEquals("HIGH", analysis.getSeverity());
        }

        @Test
        @DisplayName("Should accept MEDIUM severity")
        void mediumSeverity() {
            analysis.setSeverity("MEDIUM");
            assertEquals("MEDIUM", analysis.getSeverity());
        }

        @Test
        @DisplayName("Should accept INFO severity")
        void infoSeverity() {
            analysis.setSeverity("INFO");
            assertEquals("INFO", analysis.getSeverity());
        }
    }

    @Nested
    @DisplayName("Score Validation Tests")
    class ScoreValidationTests {

        @Test
        @DisplayName("Should accept score of 0")
        void zeroScore() {
            analysis.setSectionScore(0.0);
            assertEquals(0.0, analysis.getSectionScore());
        }

        @Test
        @DisplayName("Should accept score of 100")
        void perfectScore() {
            analysis.setSectionScore(100.0);
            assertEquals(100.0, analysis.getSectionScore());
        }

        @Test
        @DisplayName("Should accept decimal scores")
        void decimalScore() {
            analysis.setSectionScore(87.5);
            assertEquals(87.5, analysis.getSectionScore());
        }
    }

    @Nested
    @DisplayName("Coverage Tests")
    class CoverageTests {

        @Test
        @DisplayName("Should handle 0% coverage")
        void zeroCoverage() {
            analysis.setCoverage(0.0);
            assertEquals(0.0, analysis.getCoverage());
        }

        @Test
        @DisplayName("Should handle 100% coverage")
        void fullCoverage() {
            analysis.setCoverage(100.0);
            assertEquals(100.0, analysis.getCoverage());
        }

        @Test
        @DisplayName("Should handle partial coverage")
        void partialCoverage() {
            analysis.setCoverage(67.5);
            assertEquals(67.5, analysis.getCoverage());
        }
    }

    @Nested
    @DisplayName("Present/Absent Section Tests")
    class PresentAbsentTests {

        @Test
        @DisplayName("Present section should have score and findings")
        void presentSection() {
            analysis.setPresent(true);
            analysis.setSectionScore(85.0);
            analysis.setFindings("Section found with complete content");
            analysis.setPageNumber(3);

            assertTrue(analysis.isPresent());
            assertEquals(85.0, analysis.getSectionScore());
            assertNotNull(analysis.getFindings());
            assertNotNull(analysis.getPageNumber());
        }

        @Test
        @DisplayName("Absent section should have recommendations")
        void absentSection() {
            analysis.setPresent(false);
            analysis.setSectionScore(0.0);
            analysis.setRecommendations("Add this required section");
            analysis.setSeverity("HIGH");

            assertFalse(analysis.isPresent());
            assertEquals(0.0, analysis.getSectionScore());
            assertNotNull(analysis.getRecommendations());
            assertEquals("HIGH", analysis.getSeverity());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same id")
        void equalsWithSameId() {
            SectionAnalysis analysis1 = new SectionAnalysis();
            analysis1.setId(1L);
            analysis1.setSectionName(SectionAnalysis.IEEE1058Section.OVERVIEW);

            SectionAnalysis analysis2 = new SectionAnalysis();
            analysis2.setId(1L);
            analysis2.setSectionName(SectionAnalysis.IEEE1058Section.OVERVIEW);

            assertEquals(analysis1, analysis2);
            assertEquals(analysis1.hashCode(), analysis2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different ids")
        void notEqualsWithDifferentIds() {
            SectionAnalysis analysis1 = new SectionAnalysis();
            analysis1.setId(1L);

            SectionAnalysis analysis2 = new SectionAnalysis();
            analysis2.setId(2L);

            assertNotEquals(analysis1, analysis2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void toStringContainsFields() {
            analysis.setId(1L);
            analysis.setSectionName(SectionAnalysis.IEEE1058Section.RISK_MANAGEMENT);
            analysis.setPresent(true);
            analysis.setSectionScore(85.0);

            String str = analysis.toString();
            assertNotNull(str);
            assertTrue(str.contains("1"));
        }
    }
}
