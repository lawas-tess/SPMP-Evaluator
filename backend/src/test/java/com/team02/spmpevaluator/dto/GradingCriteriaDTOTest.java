package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GradingCriteriaDTO.
 */
@DisplayName("GradingCriteriaDTO Tests")
class GradingCriteriaDTOTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();

            assertNotNull(dto);
            assertNull(dto.getId());
            assertNull(dto.getName());
            // Default weights should be set
            assertEquals(10, dto.getOverviewWeight());
            assertEquals(5, dto.getReferencesWeight());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO(
                    1L, "Test Criteria", "Description", 2L, "Professor",
                    true, 10, 5, 5, 15, 20, 20, 15, 10,
                    "2025-12-18", "2025-12-18");

            assertEquals(1L, dto.getId());
            assertEquals("Test Criteria", dto.getName());
            assertEquals("Description", dto.getDescription());
            assertEquals(2L, dto.getCreatedById());
            assertEquals("Professor", dto.getCreatedByName());
            assertTrue(dto.isActive());
            assertEquals(10, dto.getOverviewWeight());
            assertEquals(5, dto.getReferencesWeight());
            assertEquals(5, dto.getDefinitionsWeight());
            assertEquals(15, dto.getOrganizationWeight());
            assertEquals(20, dto.getManagerialProcessWeight());
            assertEquals(20, dto.getTechnicalProcessWeight());
            assertEquals(15, dto.getSupportingProcessWeight());
            assertEquals(10, dto.getAdditionalPlansWeight());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setId(100L);
            assertEquals(100L, dto.getId());
        }

        @Test
        @DisplayName("Should set and get name")
        void setAndGetName() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setName("Custom Criteria");
            assertEquals("Custom Criteria", dto.getName());
        }

        @Test
        @DisplayName("Should set and get description")
        void setAndGetDescription() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setDescription("Test description");
            assertEquals("Test description", dto.getDescription());
        }

        @Test
        @DisplayName("Should set and get createdById")
        void setAndGetCreatedById() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setCreatedById(50L);
            assertEquals(50L, dto.getCreatedById());
        }

        @Test
        @DisplayName("Should set and get createdByName")
        void setAndGetCreatedByName() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setCreatedByName("Prof. Smith");
            assertEquals("Prof. Smith", dto.getCreatedByName());
        }

        @Test
        @DisplayName("Should set and get isActive")
        void setAndGetIsActive() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setActive(true);
            assertTrue(dto.isActive());

            dto.setActive(false);
            assertFalse(dto.isActive());
        }

        @Test
        @DisplayName("Should set and get overviewWeight")
        void setAndGetOverviewWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setOverviewWeight(15);
            assertEquals(15, dto.getOverviewWeight());
        }

        @Test
        @DisplayName("Should set and get referencesWeight")
        void setAndGetReferencesWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setReferencesWeight(8);
            assertEquals(8, dto.getReferencesWeight());
        }

        @Test
        @DisplayName("Should set and get definitionsWeight")
        void setAndGetDefinitionsWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setDefinitionsWeight(7);
            assertEquals(7, dto.getDefinitionsWeight());
        }

        @Test
        @DisplayName("Should set and get organizationWeight")
        void setAndGetOrganizationWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setOrganizationWeight(18);
            assertEquals(18, dto.getOrganizationWeight());
        }

        @Test
        @DisplayName("Should set and get managerialProcessWeight")
        void setAndGetManagerialProcessWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setManagerialProcessWeight(25);
            assertEquals(25, dto.getManagerialProcessWeight());
        }

        @Test
        @DisplayName("Should set and get technicalProcessWeight")
        void setAndGetTechnicalProcessWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setTechnicalProcessWeight(22);
            assertEquals(22, dto.getTechnicalProcessWeight());
        }

        @Test
        @DisplayName("Should set and get supportingProcessWeight")
        void setAndGetSupportingProcessWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setSupportingProcessWeight(12);
            assertEquals(12, dto.getSupportingProcessWeight());
        }

        @Test
        @DisplayName("Should set and get additionalPlansWeight")
        void setAndGetAdditionalPlansWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setAdditionalPlansWeight(8);
            assertEquals(8, dto.getAdditionalPlansWeight());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void setAndGetCreatedAt() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setCreatedAt("2025-01-01");
            assertEquals("2025-01-01", dto.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get updatedAt")
        void setAndGetUpdatedAt() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setUpdatedAt("2025-12-31");
            assertEquals("2025-12-31", dto.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Weight Calculation Tests")
    class WeightCalculationTests {

        @Test
        @DisplayName("Should calculate total weight correctly")
        void getTotalWeight_CalculatesCorrectly() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setOverviewWeight(10);
            dto.setReferencesWeight(5);
            dto.setDefinitionsWeight(5);
            dto.setOrganizationWeight(15);
            dto.setManagerialProcessWeight(20);
            dto.setTechnicalProcessWeight(20);
            dto.setSupportingProcessWeight(15);
            dto.setAdditionalPlansWeight(10);

            assertEquals(100, dto.getTotalWeight());
        }

        @Test
        @DisplayName("Should return true when weights sum to 100")
        void isValidWeights_SumTo100_ReturnsTrue() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setOverviewWeight(10);
            dto.setReferencesWeight(5);
            dto.setDefinitionsWeight(5);
            dto.setOrganizationWeight(15);
            dto.setManagerialProcessWeight(20);
            dto.setTechnicalProcessWeight(20);
            dto.setSupportingProcessWeight(15);
            dto.setAdditionalPlansWeight(10);

            assertTrue(dto.isValidWeights());
        }

        @Test
        @DisplayName("Should return false when weights do not sum to 100")
        void isValidWeights_NotSumTo100_ReturnsFalse() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setOverviewWeight(10);
            dto.setReferencesWeight(10);
            dto.setDefinitionsWeight(10);
            dto.setOrganizationWeight(10);
            dto.setManagerialProcessWeight(10);
            dto.setTechnicalProcessWeight(10);
            dto.setSupportingProcessWeight(10);
            dto.setAdditionalPlansWeight(10);

            assertFalse(dto.isValidWeights());
            assertEquals(80, dto.getTotalWeight());
        }

        @Test
        @DisplayName("Should handle null weights in total calculation")
        void getTotalWeight_WithNullWeights_HandlesGracefully() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setOverviewWeight(null);
            dto.setReferencesWeight(null);
            dto.setDefinitionsWeight(null);
            dto.setOrganizationWeight(null);
            dto.setManagerialProcessWeight(null);
            dto.setTechnicalProcessWeight(null);
            dto.setSupportingProcessWeight(null);
            dto.setAdditionalPlansWeight(null);

            assertEquals(0, dto.getTotalWeight());
        }

        @Test
        @DisplayName("Should handle partial null weights")
        void getTotalWeight_WithPartialNullWeights_CalculatesCorrectly() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            dto.setOverviewWeight(50);
            dto.setReferencesWeight(null);
            dto.setDefinitionsWeight(25);
            dto.setOrganizationWeight(null);
            dto.setManagerialProcessWeight(25);
            dto.setTechnicalProcessWeight(null);
            dto.setSupportingProcessWeight(null);
            dto.setAdditionalPlansWeight(null);

            assertEquals(100, dto.getTotalWeight());
            assertTrue(dto.isValidWeights());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have default overview weight of 10")
        void defaultOverviewWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            assertEquals(10, dto.getOverviewWeight());
        }

        @Test
        @DisplayName("Should have default references weight of 5")
        void defaultReferencesWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            assertEquals(5, dto.getReferencesWeight());
        }

        @Test
        @DisplayName("Should have default definitions weight of 5")
        void defaultDefinitionsWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            assertEquals(5, dto.getDefinitionsWeight());
        }

        @Test
        @DisplayName("Should have default organization weight of 15")
        void defaultOrganizationWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            assertEquals(15, dto.getOrganizationWeight());
        }

        @Test
        @DisplayName("Should have default managerial process weight of 20")
        void defaultManagerialProcessWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            assertEquals(20, dto.getManagerialProcessWeight());
        }

        @Test
        @DisplayName("Should have default technical process weight of 20")
        void defaultTechnicalProcessWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            assertEquals(20, dto.getTechnicalProcessWeight());
        }

        @Test
        @DisplayName("Should have default supporting process weight of 15")
        void defaultSupportingProcessWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            assertEquals(15, dto.getSupportingProcessWeight());
        }

        @Test
        @DisplayName("Should have default additional plans weight of 10")
        void defaultAdditionalPlansWeight() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            assertEquals(10, dto.getAdditionalPlansWeight());
        }

        @Test
        @DisplayName("Default weights should sum to 100")
        void defaultWeights_SumTo100() {
            GradingCriteriaDTO dto = new GradingCriteriaDTO();
            assertEquals(100, dto.getTotalWeight());
            assertTrue(dto.isValidWeights());
        }
    }
}
