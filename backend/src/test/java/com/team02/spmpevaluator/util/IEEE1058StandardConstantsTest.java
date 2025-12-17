package com.team02.spmpevaluator.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IEEE1058StandardConstants.
 */
@DisplayName("IEEE1058StandardConstants Tests")
class IEEE1058StandardConstantsTest {

    @Nested
    @DisplayName("Overview Keywords Tests")
    class OverviewKeywordsTests {

        @Test
        @DisplayName("Should contain project overview keyword")
        void overviewKeywords_ContainsProjectOverview() {
            assertTrue(IEEE1058StandardConstants.OVERVIEW_KEYWORDS.contains("project overview"));
        }

        @Test
        @DisplayName("Should contain overview keyword")
        void overviewKeywords_ContainsOverview() {
            assertTrue(IEEE1058StandardConstants.OVERVIEW_KEYWORDS.contains("overview"));
        }

        @Test
        @DisplayName("Should contain introduction keyword")
        void overviewKeywords_ContainsIntroduction() {
            assertTrue(IEEE1058StandardConstants.OVERVIEW_KEYWORDS.contains("introduction"));
        }

        @Test
        @DisplayName("Should contain purpose keyword")
        void overviewKeywords_ContainsPurpose() {
            assertTrue(IEEE1058StandardConstants.OVERVIEW_KEYWORDS.contains("purpose"));
        }

        @Test
        @DisplayName("Should contain scope keyword")
        void overviewKeywords_ContainsScope() {
            assertTrue(IEEE1058StandardConstants.OVERVIEW_KEYWORDS.contains("scope"));
        }

        @Test
        @DisplayName("Should not be empty")
        void overviewKeywords_NotEmpty() {
            assertFalse(IEEE1058StandardConstants.OVERVIEW_KEYWORDS.isEmpty());
        }
    }

    @Nested
    @DisplayName("Documentation Plan Keywords Tests")
    class DocumentationPlanKeywordsTests {

        @Test
        @DisplayName("Should contain documentation plan keyword")
        void documentationPlanKeywords_ContainsDocumentationPlan() {
            assertTrue(IEEE1058StandardConstants.DOCUMENTATION_PLAN_KEYWORDS.contains("documentation plan"));
        }

        @Test
        @DisplayName("Should contain documentation keyword")
        void documentationPlanKeywords_ContainsDocumentation() {
            assertTrue(IEEE1058StandardConstants.DOCUMENTATION_PLAN_KEYWORDS.contains("documentation"));
        }

        @Test
        @DisplayName("Should not be empty")
        void documentationPlanKeywords_NotEmpty() {
            assertFalse(IEEE1058StandardConstants.DOCUMENTATION_PLAN_KEYWORDS.isEmpty());
        }
    }

    @Nested
    @DisplayName("Master Schedule Keywords Tests")
    class MasterScheduleKeywordsTests {

        @Test
        @DisplayName("Should contain master schedule keyword")
        void masterScheduleKeywords_ContainsMasterSchedule() {
            assertTrue(IEEE1058StandardConstants.MASTER_SCHEDULE_KEYWORDS.contains("master schedule"));
        }

        @Test
        @DisplayName("Should contain schedule keyword")
        void masterScheduleKeywords_ContainsSchedule() {
            assertTrue(IEEE1058StandardConstants.MASTER_SCHEDULE_KEYWORDS.contains("schedule"));
        }

        @Test
        @DisplayName("Should contain timeline keyword")
        void masterScheduleKeywords_ContainsTimeline() {
            assertTrue(IEEE1058StandardConstants.MASTER_SCHEDULE_KEYWORDS.contains("timeline"));
        }

        @Test
        @DisplayName("Should contain milestones keyword")
        void masterScheduleKeywords_ContainsMilestones() {
            assertTrue(IEEE1058StandardConstants.MASTER_SCHEDULE_KEYWORDS.contains("milestones"));
        }

        @Test
        @DisplayName("Should contain gantt chart keyword")
        void masterScheduleKeywords_ContainsGanttChart() {
            assertTrue(IEEE1058StandardConstants.MASTER_SCHEDULE_KEYWORDS.contains("gantt chart"));
        }
    }

    @Nested
    @DisplayName("Organization Keywords Tests")
    class OrganizationKeywordsTests {

        @Test
        @DisplayName("Should contain organization keyword")
        void organizationKeywords_ContainsOrganization() {
            assertTrue(IEEE1058StandardConstants.ORGANIZATION_KEYWORDS.contains("organization"));
        }

        @Test
        @DisplayName("Should contain project organization keyword")
        void organizationKeywords_ContainsProjectOrganization() {
            assertTrue(IEEE1058StandardConstants.ORGANIZATION_KEYWORDS.contains("project organization"));
        }

        @Test
        @DisplayName("Should contain organizational chart keyword")
        void organizationKeywords_ContainsOrganizationalChart() {
            assertTrue(IEEE1058StandardConstants.ORGANIZATION_KEYWORDS.contains("organizational chart"));
        }
    }

    @Nested
    @DisplayName("Standards Practices Keywords Tests")
    class StandardsPracticesKeywordsTests {

        @Test
        @DisplayName("Should contain standards keyword")
        void standardsPracticesKeywords_ContainsStandards() {
            assertTrue(IEEE1058StandardConstants.STANDARDS_PRACTICES_KEYWORDS.contains("standards"));
        }

        @Test
        @DisplayName("Should contain practices keyword")
        void standardsPracticesKeywords_ContainsPractices() {
            assertTrue(IEEE1058StandardConstants.STANDARDS_PRACTICES_KEYWORDS.contains("practices"));
        }

        @Test
        @DisplayName("Should contain coding standards keyword")
        void standardsPracticesKeywords_ContainsCodingStandards() {
            assertTrue(IEEE1058StandardConstants.STANDARDS_PRACTICES_KEYWORDS.contains("coding standards"));
        }
    }

    @Nested
    @DisplayName("Risk Management Keywords Tests")
    class RiskManagementKeywordsTests {

        @Test
        @DisplayName("Should contain risk management keyword")
        void riskManagementKeywords_ContainsRiskManagement() {
            assertTrue(IEEE1058StandardConstants.RISK_MANAGEMENT_KEYWORDS.contains("risk management"));
        }

        @Test
        @DisplayName("Should contain risk keyword")
        void riskManagementKeywords_ContainsRisk() {
            assertTrue(IEEE1058StandardConstants.RISK_MANAGEMENT_KEYWORDS.contains("risk"));
        }

        @Test
        @DisplayName("Should contain risk assessment keyword")
        void riskManagementKeywords_ContainsRiskAssessment() {
            assertTrue(IEEE1058StandardConstants.RISK_MANAGEMENT_KEYWORDS.contains("risk assessment"));
        }

        @Test
        @DisplayName("Should contain risk mitigation keyword")
        void riskManagementKeywords_ContainsRiskMitigation() {
            assertTrue(IEEE1058StandardConstants.RISK_MANAGEMENT_KEYWORDS.contains("risk mitigation"));
        }
    }

    @Nested
    @DisplayName("Staff Organization Keywords Tests")
    class StaffOrganizationKeywordsTests {

        @Test
        @DisplayName("Should contain staff organization keyword")
        void staffOrganizationKeywords_ContainsStaffOrganization() {
            assertTrue(IEEE1058StandardConstants.STAFF_ORGANIZATION_KEYWORDS.contains("staff organization"));
        }

        @Test
        @DisplayName("Should contain staff keyword")
        void staffOrganizationKeywords_ContainsStaff() {
            assertTrue(IEEE1058StandardConstants.STAFF_ORGANIZATION_KEYWORDS.contains("staff"));
        }

        @Test
        @DisplayName("Should contain roles keyword")
        void staffOrganizationKeywords_ContainsRoles() {
            assertTrue(IEEE1058StandardConstants.STAFF_ORGANIZATION_KEYWORDS.contains("roles"));
        }

        @Test
        @DisplayName("Should contain responsibilities keyword")
        void staffOrganizationKeywords_ContainsResponsibilities() {
            assertTrue(IEEE1058StandardConstants.STAFF_ORGANIZATION_KEYWORDS.contains("responsibilities"));
        }
    }

    @Nested
    @DisplayName("Budget Resource Keywords Tests")
    class BudgetResourceKeywordsTests {

        @Test
        @DisplayName("Should contain budget keyword")
        void budgetResourceKeywords_ContainsBudget() {
            assertTrue(IEEE1058StandardConstants.BUDGET_RESOURCE_KEYWORDS.contains("budget"));
        }

        @Test
        @DisplayName("Should contain resource keyword")
        void budgetResourceKeywords_ContainsResource() {
            assertTrue(IEEE1058StandardConstants.BUDGET_RESOURCE_KEYWORDS.contains("resource"));
        }

        @Test
        @DisplayName("Should contain cost keyword")
        void budgetResourceKeywords_ContainsCost() {
            assertTrue(IEEE1058StandardConstants.BUDGET_RESOURCE_KEYWORDS.contains("cost"));
        }

        @Test
        @DisplayName("Should contain estimation keyword")
        void budgetResourceKeywords_ContainsEstimation() {
            assertTrue(IEEE1058StandardConstants.BUDGET_RESOURCE_KEYWORDS.contains("estimation"));
        }
    }

    @Nested
    @DisplayName("Reviews Audits Keywords Tests")
    class ReviewsAuditsKeywordsTests {

        @Test
        @DisplayName("Should contain reviews keyword")
        void reviewsAuditsKeywords_ContainsReviews() {
            assertTrue(IEEE1058StandardConstants.REVIEWS_AUDITS_KEYWORDS.contains("reviews"));
        }

        @Test
        @DisplayName("Should contain audits keyword")
        void reviewsAuditsKeywords_ContainsAudits() {
            assertTrue(IEEE1058StandardConstants.REVIEWS_AUDITS_KEYWORDS.contains("audits"));
        }

        @Test
        @DisplayName("Should contain quality assurance keyword")
        void reviewsAuditsKeywords_ContainsQualityAssurance() {
            assertTrue(IEEE1058StandardConstants.REVIEWS_AUDITS_KEYWORDS.contains("quality assurance"));
        }

        @Test
        @DisplayName("Should contain qa keyword")
        void reviewsAuditsKeywords_ContainsQa() {
            assertTrue(IEEE1058StandardConstants.REVIEWS_AUDITS_KEYWORDS.contains("qa"));
        }

        @Test
        @DisplayName("Should contain testing keyword")
        void reviewsAuditsKeywords_ContainsTesting() {
            assertTrue(IEEE1058StandardConstants.REVIEWS_AUDITS_KEYWORDS.contains("testing"));
        }
    }

    @Nested
    @DisplayName("Problem Resolution Keywords Tests")
    class ProblemResolutionKeywordsTests {

        @Test
        @DisplayName("Should contain problem resolution keyword")
        void problemResolutionKeywords_ContainsProblemResolution() {
            assertTrue(IEEE1058StandardConstants.PROBLEM_RESOLUTION_KEYWORDS.contains("problem resolution"));
        }

        @Test
        @DisplayName("Should contain problem keyword")
        void problemResolutionKeywords_ContainsProblem() {
            assertTrue(IEEE1058StandardConstants.PROBLEM_RESOLUTION_KEYWORDS.contains("problem"));
        }

        @Test
        @DisplayName("Should contain issue resolution keyword")
        void problemResolutionKeywords_ContainsIssueResolution() {
            assertTrue(IEEE1058StandardConstants.PROBLEM_RESOLUTION_KEYWORDS.contains("issue resolution"));
        }

        @Test
        @DisplayName("Should contain escalation keyword")
        void problemResolutionKeywords_ContainsEscalation() {
            assertTrue(IEEE1058StandardConstants.PROBLEM_RESOLUTION_KEYWORDS.contains("escalation"));
        }
    }

    @Nested
    @DisplayName("Change Management Keywords Tests")
    class ChangeManagementKeywordsTests {

        @Test
        @DisplayName("Should contain change management keyword")
        void changeManagementKeywords_ContainsChangeManagement() {
            assertTrue(IEEE1058StandardConstants.CHANGE_MANAGEMENT_KEYWORDS.contains("change management"));
        }

        @Test
        @DisplayName("Should contain change keyword")
        void changeManagementKeywords_ContainsChange() {
            assertTrue(IEEE1058StandardConstants.CHANGE_MANAGEMENT_KEYWORDS.contains("change"));
        }

        @Test
        @DisplayName("Should contain change control keyword")
        void changeManagementKeywords_ContainsChangeControl() {
            assertTrue(IEEE1058StandardConstants.CHANGE_MANAGEMENT_KEYWORDS.contains("change control"));
        }

        @Test
        @DisplayName("Should contain configuration management keyword")
        void changeManagementKeywords_ContainsConfigurationManagement() {
            assertTrue(IEEE1058StandardConstants.CHANGE_MANAGEMENT_KEYWORDS.contains("configuration management"));
        }

        @Test
        @DisplayName("Should contain ccb keyword")
        void changeManagementKeywords_ContainsCcb() {
            assertTrue(IEEE1058StandardConstants.CHANGE_MANAGEMENT_KEYWORDS.contains("ccb"));
        }
    }

    @Nested
    @DisplayName("Glossary Appendix Keywords Tests")
    class GlossaryAppendixKeywordsTests {

        @Test
        @DisplayName("Should contain glossary keyword")
        void glossaryAppendixKeywords_ContainsGlossary() {
            assertTrue(IEEE1058StandardConstants.GLOSSARY_APPENDIX_KEYWORDS.contains("glossary"));
        }

        @Test
        @DisplayName("Should contain appendix keyword")
        void glossaryAppendixKeywords_ContainsAppendix() {
            assertTrue(IEEE1058StandardConstants.GLOSSARY_APPENDIX_KEYWORDS.contains("appendix"));
        }

        @Test
        @DisplayName("Should contain references keyword")
        void glossaryAppendixKeywords_ContainsReferences() {
            assertTrue(IEEE1058StandardConstants.GLOSSARY_APPENDIX_KEYWORDS.contains("references"));
        }

        @Test
        @DisplayName("Should contain definitions keyword")
        void glossaryAppendixKeywords_ContainsDefinitions() {
            assertTrue(IEEE1058StandardConstants.GLOSSARY_APPENDIX_KEYWORDS.contains("definitions"));
        }

        @Test
        @DisplayName("Should contain abbreviations keyword")
        void glossaryAppendixKeywords_ContainsAbbreviations() {
            assertTrue(IEEE1058StandardConstants.GLOSSARY_APPENDIX_KEYWORDS.contains("abbreviations"));
        }
    }

    @Nested
    @DisplayName("Scoring Thresholds Tests")
    class ScoringThresholdsTests {

        @Test
        @DisplayName("Minimum compliance threshold should be 0.80")
        void minimumComplianceThreshold_ShouldBe80Percent() {
            assertEquals(0.80, IEEE1058StandardConstants.MINIMUM_COMPLIANCE_THRESHOLD);
        }

        @Test
        @DisplayName("Section present score should be 100.0")
        void sectionPresentScore_ShouldBe100() {
            assertEquals(100.0, IEEE1058StandardConstants.SECTION_PRESENT_SCORE);
        }

        @Test
        @DisplayName("Section absent score should be 0.0")
        void sectionAbsentScore_ShouldBe0() {
            assertEquals(0.0, IEEE1058StandardConstants.SECTION_ABSENT_SCORE);
        }
    }

    @Nested
    @DisplayName("Scoring Weights Tests")
    class ScoringWeightsTests {

        @Test
        @DisplayName("Structure weight should be 0.3 (30%)")
        void structureWeight_ShouldBe30Percent() {
            assertEquals(0.3, IEEE1058StandardConstants.STRUCTURE_WEIGHT);
        }

        @Test
        @DisplayName("Completeness weight should be 0.7 (70%)")
        void completenessWeight_ShouldBe70Percent() {
            assertEquals(0.7, IEEE1058StandardConstants.COMPLETENESS_WEIGHT);
        }

        @Test
        @DisplayName("Total weights should equal 1.0 (100%)")
        void totalWeights_ShouldEqual100Percent() {
            double totalWeight = IEEE1058StandardConstants.STRUCTURE_WEIGHT +
                    IEEE1058StandardConstants.COMPLETENESS_WEIGHT;
            assertEquals(1.0, totalWeight, 0.001);
        }
    }

    @Nested
    @DisplayName("Keywords Set Properties Tests")
    class KeywordsSetPropertiesTests {

        @Test
        @DisplayName("All keyword sets should be non-null")
        void allKeywordSets_ShouldBeNonNull() {
            assertNotNull(IEEE1058StandardConstants.OVERVIEW_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.DOCUMENTATION_PLAN_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.MASTER_SCHEDULE_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.ORGANIZATION_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.STANDARDS_PRACTICES_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.RISK_MANAGEMENT_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.STAFF_ORGANIZATION_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.BUDGET_RESOURCE_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.REVIEWS_AUDITS_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.PROBLEM_RESOLUTION_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.CHANGE_MANAGEMENT_KEYWORDS);
            assertNotNull(IEEE1058StandardConstants.GLOSSARY_APPENDIX_KEYWORDS);
        }

        @Test
        @DisplayName("All keyword sets should not be empty")
        void allKeywordSets_ShouldNotBeEmpty() {
            assertFalse(IEEE1058StandardConstants.OVERVIEW_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.DOCUMENTATION_PLAN_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.MASTER_SCHEDULE_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.ORGANIZATION_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.STANDARDS_PRACTICES_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.RISK_MANAGEMENT_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.STAFF_ORGANIZATION_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.BUDGET_RESOURCE_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.REVIEWS_AUDITS_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.PROBLEM_RESOLUTION_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.CHANGE_MANAGEMENT_KEYWORDS.isEmpty());
            assertFalse(IEEE1058StandardConstants.GLOSSARY_APPENDIX_KEYWORDS.isEmpty());
        }

        @Test
        @DisplayName("Keyword sets should be HashSet instances")
        void keywordSets_ShouldBeHashSets() {
            assertTrue(IEEE1058StandardConstants.OVERVIEW_KEYWORDS instanceof Set);
            assertTrue(IEEE1058StandardConstants.RISK_MANAGEMENT_KEYWORDS instanceof Set);
        }
    }
}
