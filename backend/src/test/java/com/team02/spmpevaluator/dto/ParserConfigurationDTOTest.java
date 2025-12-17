package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ParserConfigurationDTO.
 */
@DisplayName("ParserConfigurationDTO Tests")
class ParserConfigurationDTOTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();

            assertNotNull(dto);
            assertNull(dto.getId());
            assertNull(dto.getName());
            assertNull(dto.getIsActive());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO(
                    1L, "Default Config", "Standard IEEE 1058 parser",
                    "{\"overview\": \"1\"}", "{\"rule1\": \"value\"}",
                    true, true, 2L, "admin", "2025-12-18", "2025-12-18");

            assertEquals(1L, dto.getId());
            assertEquals("Default Config", dto.getName());
            assertEquals("Standard IEEE 1058 parser", dto.getDescription());
            assertEquals("{\"overview\": \"1\"}", dto.getClauseMappings());
            assertEquals("{\"rule1\": \"value\"}", dto.getCustomRules());
            assertTrue(dto.getIsActive());
            assertTrue(dto.getIsDefault());
            assertEquals(2L, dto.getCreatedByUserId());
            assertEquals("admin", dto.getCreatedByUsername());
            assertEquals("2025-12-18", dto.getCreatedAt());
            assertEquals("2025-12-18", dto.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setId(100L);
            assertEquals(100L, dto.getId());
        }

        @Test
        @DisplayName("Should set and get name")
        void setAndGetName() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setName("Custom Parser");
            assertEquals("Custom Parser", dto.getName());
        }

        @Test
        @DisplayName("Should set and get description")
        void setAndGetDescription() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setDescription("A custom parser configuration");
            assertEquals("A custom parser configuration", dto.getDescription());
        }

        @Test
        @DisplayName("Should set and get clauseMappings")
        void setAndGetClauseMappings() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            String mappings = "{\"section1\": \"clause1\"}";
            dto.setClauseMappings(mappings);
            assertEquals(mappings, dto.getClauseMappings());
        }

        @Test
        @DisplayName("Should set and get customRules")
        void setAndGetCustomRules() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            String rules = "{\"rule\": \"value\"}";
            dto.setCustomRules(rules);
            assertEquals(rules, dto.getCustomRules());
        }

        @Test
        @DisplayName("Should set and get isActive")
        void setAndGetIsActive() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setIsActive(true);
            assertTrue(dto.getIsActive());

            dto.setIsActive(false);
            assertFalse(dto.getIsActive());
        }

        @Test
        @DisplayName("Should set and get isDefault")
        void setAndGetIsDefault() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setIsDefault(true);
            assertTrue(dto.getIsDefault());

            dto.setIsDefault(false);
            assertFalse(dto.getIsDefault());
        }

        @Test
        @DisplayName("Should set and get createdByUserId")
        void setAndGetCreatedByUserId() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setCreatedByUserId(50L);
            assertEquals(50L, dto.getCreatedByUserId());
        }

        @Test
        @DisplayName("Should set and get createdByUsername")
        void setAndGetCreatedByUsername() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setCreatedByUsername("professor");
            assertEquals("professor", dto.getCreatedByUsername());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void setAndGetCreatedAt() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setCreatedAt("2025-01-01T10:00:00");
            assertEquals("2025-01-01T10:00:00", dto.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get updatedAt")
        void setAndGetUpdatedAt() {
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setUpdatedAt("2025-12-31T23:59:59");
            assertEquals("2025-12-31T23:59:59", dto.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            ParserConfigurationDTO dto1 = new ParserConfigurationDTO(
                    1L, "Config", "Desc", "{}", "{}", true, false,
                    1L, "user", "2025-01-01", "2025-01-01");
            ParserConfigurationDTO dto2 = new ParserConfigurationDTO(
                    1L, "Config", "Desc", "{}", "{}", true, false,
                    1L, "user", "2025-01-01", "2025-01-01");

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different values")
        void equals_DifferentValues_ReturnsFalse() {
            ParserConfigurationDTO dto1 = new ParserConfigurationDTO();
            dto1.setId(1L);
            ParserConfigurationDTO dto2 = new ParserConfigurationDTO();
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
            ParserConfigurationDTO dto = new ParserConfigurationDTO();
            dto.setId(1L);
            dto.setName("Test Config");
            dto.setIsActive(true);

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("name=Test Config"));
            assertTrue(result.contains("isActive=true"));
        }
    }
}
