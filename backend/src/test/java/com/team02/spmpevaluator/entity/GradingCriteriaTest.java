package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GradingCriteria entity.
 */
@DisplayName("GradingCriteria Entity Tests")
class GradingCriteriaTest {

    private GradingCriteria gradingCriteria;
    private User professor;

    @BeforeEach
    void setUp() {
        gradingCriteria = new GradingCriteria();
        professor = new User();
        professor.setId(1L);
        professor.setUsername("professor");
        professor.setRole(Role.PROFESSOR);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            GradingCriteria entity = new GradingCriteria();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getName());
            assertFalse(entity.isActive());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            gradingCriteria.setId(100L);
            assertEquals(100L, gradingCriteria.getId());
        }

        @Test
        @DisplayName("Should set and get name")
        void setAndGetName() {
            gradingCriteria.setName("Final Evaluation");
            assertEquals("Final Evaluation", gradingCriteria.getName());
        }

        @Test
        @DisplayName("Should set and get description")
        void setAndGetDescription() {
            gradingCriteria.setDescription("Criteria for final submission");
            assertEquals("Criteria for final submission", gradingCriteria.getDescription());
        }

        @Test
        @DisplayName("Should set and get createdBy")
        void setAndGetCreatedBy() {
            gradingCriteria.setCreatedBy(professor);
            assertEquals(professor, gradingCriteria.getCreatedBy());
        }

        @Test
        @DisplayName("Should set and get isActive")
        void setAndGetIsActive() {
            gradingCriteria.setActive(true);
            assertTrue(gradingCriteria.isActive());

            gradingCriteria.setActive(false);
            assertFalse(gradingCriteria.isActive());
        }

        @Test
        @DisplayName("Should set and get overviewWeight")
        void setAndGetOverviewWeight() {
            gradingCriteria.setOverviewWeight(15);
            assertEquals(15, gradingCriteria.getOverviewWeight());
        }

        @Test
        @DisplayName("Should set and get referencesWeight")
        void setAndGetReferencesWeight() {
            gradingCriteria.setReferencesWeight(8);
            assertEquals(8, gradingCriteria.getReferencesWeight());
        }

        @Test
        @DisplayName("Should set and get definitionsWeight")
        void setAndGetDefinitionsWeight() {
            gradingCriteria.setDefinitionsWeight(7);
            assertEquals(7, gradingCriteria.getDefinitionsWeight());
        }

        @Test
        @DisplayName("Should set and get organizationWeight")
        void setAndGetOrganizationWeight() {
            gradingCriteria.setOrganizationWeight(18);
            assertEquals(18, gradingCriteria.getOrganizationWeight());
        }

        @Test
        @DisplayName("Should set and get managerialProcessWeight")
        void setAndGetManagerialProcessWeight() {
            gradingCriteria.setManagerialProcessWeight(22);
            assertEquals(22, gradingCriteria.getManagerialProcessWeight());
        }

        @Test
        @DisplayName("Should set and get technicalProcessWeight")
        void setAndGetTechnicalProcessWeight() {
            gradingCriteria.setTechnicalProcessWeight(18);
            assertEquals(18, gradingCriteria.getTechnicalProcessWeight());
        }

        @Test
        @DisplayName("Should set and get supportingProcessWeight")
        void setAndGetSupportingProcessWeight() {
            gradingCriteria.setSupportingProcessWeight(12);
            assertEquals(12, gradingCriteria.getSupportingProcessWeight());
        }

        @Test
        @DisplayName("Should set and get additionalPlansWeight")
        void setAndGetAdditionalPlansWeight() {
            gradingCriteria.setAdditionalPlansWeight(8);
            assertEquals(8, gradingCriteria.getAdditionalPlansWeight());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void setAndGetCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            gradingCriteria.setCreatedAt(now);
            assertEquals(now, gradingCriteria.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get updatedAt")
        void setAndGetUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            gradingCriteria.setUpdatedAt(now);
            assertEquals(now, gradingCriteria.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have default overview weight of 10")
        void defaultOverviewWeight() {
            assertEquals(10, gradingCriteria.getOverviewWeight());
        }

        @Test
        @DisplayName("Should have default references weight of 5")
        void defaultReferencesWeight() {
            assertEquals(5, gradingCriteria.getReferencesWeight());
        }

        @Test
        @DisplayName("Should have default definitions weight of 5")
        void defaultDefinitionsWeight() {
            assertEquals(5, gradingCriteria.getDefinitionsWeight());
        }

        @Test
        @DisplayName("Should have default organization weight of 15")
        void defaultOrganizationWeight() {
            assertEquals(15, gradingCriteria.getOrganizationWeight());
        }

        @Test
        @DisplayName("Should have default managerial process weight of 20")
        void defaultManagerialProcessWeight() {
            assertEquals(20, gradingCriteria.getManagerialProcessWeight());
        }

        @Test
        @DisplayName("Should have default technical process weight of 20")
        void defaultTechnicalProcessWeight() {
            assertEquals(20, gradingCriteria.getTechnicalProcessWeight());
        }

        @Test
        @DisplayName("Should have default supporting process weight of 15")
        void defaultSupportingProcessWeight() {
            assertEquals(15, gradingCriteria.getSupportingProcessWeight());
        }

        @Test
        @DisplayName("Should have default additional plans weight of 10")
        void defaultAdditionalPlansWeight() {
            assertEquals(10, gradingCriteria.getAdditionalPlansWeight());
        }

        @Test
        @DisplayName("Should have isActive default to false")
        void defaultIsActive() {
            assertFalse(gradingCriteria.isActive());
        }
    }

    @Nested
    @DisplayName("Weight Calculation Tests")
    class WeightCalculationTests {

        @Test
        @DisplayName("Should calculate total weight correctly with defaults")
        void getTotalWeight_WithDefaults_Returns100() {
            assertEquals(100, gradingCriteria.getTotalWeight());
        }

        @Test
        @DisplayName("Should calculate total weight correctly with custom values")
        void getTotalWeight_WithCustomValues_CalculatesCorrectly() {
            gradingCriteria.setOverviewWeight(15);
            gradingCriteria.setReferencesWeight(10);
            gradingCriteria.setDefinitionsWeight(5);
            gradingCriteria.setOrganizationWeight(10);
            gradingCriteria.setManagerialProcessWeight(20);
            gradingCriteria.setTechnicalProcessWeight(20);
            gradingCriteria.setSupportingProcessWeight(10);
            gradingCriteria.setAdditionalPlansWeight(10);

            assertEquals(100, gradingCriteria.getTotalWeight());
        }

        @Test
        @DisplayName("Should return true when weights sum to 100")
        void isValidWeights_SumTo100_ReturnsTrue() {
            assertTrue(gradingCriteria.isValidWeights());
        }

        @Test
        @DisplayName("Should return false when weights do not sum to 100")
        void isValidWeights_NotSumTo100_ReturnsFalse() {
            gradingCriteria.setOverviewWeight(50);
            assertFalse(gradingCriteria.isValidWeights());
        }

        @Test
        @DisplayName("Should calculate when weights sum to less than 100")
        void getTotalWeight_LessThan100() {
            gradingCriteria.setOverviewWeight(5);
            gradingCriteria.setReferencesWeight(5);
            gradingCriteria.setDefinitionsWeight(5);
            gradingCriteria.setOrganizationWeight(5);
            gradingCriteria.setManagerialProcessWeight(5);
            gradingCriteria.setTechnicalProcessWeight(5);
            gradingCriteria.setSupportingProcessWeight(5);
            gradingCriteria.setAdditionalPlansWeight(5);

            assertEquals(40, gradingCriteria.getTotalWeight());
            assertFalse(gradingCriteria.isValidWeights());
        }

        @Test
        @DisplayName("Should calculate when weights sum to more than 100")
        void getTotalWeight_MoreThan100() {
            gradingCriteria.setOverviewWeight(20);
            gradingCriteria.setReferencesWeight(20);
            gradingCriteria.setDefinitionsWeight(20);
            gradingCriteria.setOrganizationWeight(20);
            gradingCriteria.setManagerialProcessWeight(20);
            gradingCriteria.setTechnicalProcessWeight(20);
            gradingCriteria.setSupportingProcessWeight(20);
            gradingCriteria.setAdditionalPlansWeight(20);

            assertEquals(160, gradingCriteria.getTotalWeight());
            assertFalse(gradingCriteria.isValidWeights());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle zero weight values")
        void zeroWeightValues() {
            gradingCriteria.setOverviewWeight(0);
            assertEquals(0, gradingCriteria.getOverviewWeight());
        }

        @Test
        @DisplayName("Should handle empty name")
        void emptyName() {
            gradingCriteria.setName("");
            assertEquals("", gradingCriteria.getName());
        }

        @Test
        @DisplayName("Should handle null description")
        void nullDescription() {
            gradingCriteria.setDescription(null);
            assertNull(gradingCriteria.getDescription());
        }

        @Test
        @DisplayName("Should handle long description")
        void longDescription() {
            String longDesc = "x".repeat(500);
            gradingCriteria.setDescription(longDesc);
            assertEquals(longDesc, gradingCriteria.getDescription());
        }
    }

    @Nested
    @DisplayName("IEEE 1058 Section Tests")
    class IEEE1058SectionTests {

        @Test
        @DisplayName("Should have 8 section weights for IEEE 1058")
        void eightSectionWeights() {
            // Verify all 8 IEEE 1058 sections have weights
            assertNotNull(gradingCriteria.getOverviewWeight());
            assertNotNull(gradingCriteria.getReferencesWeight());
            assertNotNull(gradingCriteria.getDefinitionsWeight());
            assertNotNull(gradingCriteria.getOrganizationWeight());
            assertNotNull(gradingCriteria.getManagerialProcessWeight());
            assertNotNull(gradingCriteria.getTechnicalProcessWeight());
            assertNotNull(gradingCriteria.getSupportingProcessWeight());
            assertNotNull(gradingCriteria.getAdditionalPlansWeight());
        }

        @Test
        @DisplayName("Default weights should reflect typical IEEE 1058 importance")
        void defaultWeightsReflectImportance() {
            // Managerial and Technical processes should have highest weights
            assertTrue(gradingCriteria.getManagerialProcessWeight() >= 15);
            assertTrue(gradingCriteria.getTechnicalProcessWeight() >= 15);

            // References and Definitions typically have lower weights
            assertTrue(gradingCriteria.getReferencesWeight() <= 10);
            assertTrue(gradingCriteria.getDefinitionsWeight() <= 10);
        }
    }
}
