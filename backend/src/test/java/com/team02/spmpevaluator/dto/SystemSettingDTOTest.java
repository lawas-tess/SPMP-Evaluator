package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SystemSettingDTO.
 */
@DisplayName("SystemSettingDTO Tests")
class SystemSettingDTOTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            SystemSettingDTO dto = new SystemSettingDTO();

            assertNotNull(dto);
            assertNull(dto.getId());
            assertNull(dto.getSettingKey());
            assertNull(dto.getSettingValue());
            assertNull(dto.getCategory());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            SystemSettingDTO dto = new SystemSettingDTO(
                    1L, "max.upload.size", "10485760", "FILE_UPLOAD",
                    "Maximum file upload size in bytes", "INTEGER",
                    "admin", "2025-12-18T10:00:00");

            assertEquals(1L, dto.getId());
            assertEquals("max.upload.size", dto.getSettingKey());
            assertEquals("10485760", dto.getSettingValue());
            assertEquals("FILE_UPLOAD", dto.getCategory());
            assertEquals("Maximum file upload size in bytes", dto.getDescription());
            assertEquals("INTEGER", dto.getDataType());
            assertEquals("admin", dto.getUpdatedBy());
            assertEquals("2025-12-18T10:00:00", dto.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setId(100L);
            assertEquals(100L, dto.getId());
        }

        @Test
        @DisplayName("Should set and get settingKey")
        void setAndGetSettingKey() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setSettingKey("app.name");
            assertEquals("app.name", dto.getSettingKey());
        }

        @Test
        @DisplayName("Should set and get settingValue")
        void setAndGetSettingValue() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setSettingValue("SPMP Evaluator");
            assertEquals("SPMP Evaluator", dto.getSettingValue());
        }

        @Test
        @DisplayName("Should set and get category")
        void setAndGetCategory() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setCategory("GENERAL");
            assertEquals("GENERAL", dto.getCategory());
        }

        @Test
        @DisplayName("Should set and get description")
        void setAndGetDescription() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setDescription("Application name displayed in UI");
            assertEquals("Application name displayed in UI", dto.getDescription());
        }

        @Test
        @DisplayName("Should set and get dataType")
        void setAndGetDataType() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setDataType("STRING");
            assertEquals("STRING", dto.getDataType());
        }

        @Test
        @DisplayName("Should set and get updatedBy")
        void setAndGetUpdatedBy() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setUpdatedBy("system_admin");
            assertEquals("system_admin", dto.getUpdatedBy());
        }

        @Test
        @DisplayName("Should set and get updatedAt")
        void setAndGetUpdatedAt() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setUpdatedAt("2025-12-18T15:30:00");
            assertEquals("2025-12-18T15:30:00", dto.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Data Type Values Tests")
    class DataTypeValuesTests {

        @Test
        @DisplayName("Should handle STRING data type")
        void dataTypeString() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setDataType("STRING");
            dto.setSettingValue("some text value");
            assertEquals("STRING", dto.getDataType());
        }

        @Test
        @DisplayName("Should handle INTEGER data type")
        void dataTypeInteger() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setDataType("INTEGER");
            dto.setSettingValue("42");
            assertEquals("INTEGER", dto.getDataType());
        }

        @Test
        @DisplayName("Should handle BOOLEAN data type")
        void dataTypeBoolean() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setDataType("BOOLEAN");
            dto.setSettingValue("true");
            assertEquals("BOOLEAN", dto.getDataType());
        }

        @Test
        @DisplayName("Should handle DOUBLE data type")
        void dataTypeDouble() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setDataType("DOUBLE");
            dto.setSettingValue("3.14159");
            assertEquals("DOUBLE", dto.getDataType());
        }

        @Test
        @DisplayName("Should handle JSON data type")
        void dataTypeJson() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setDataType("JSON");
            dto.setSettingValue("{\"key\": \"value\"}");
            assertEquals("JSON", dto.getDataType());
        }
    }

    @Nested
    @DisplayName("Category Values Tests")
    class CategoryValuesTests {

        @Test
        @DisplayName("Should handle GENERAL category")
        void categoryGeneral() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setCategory("GENERAL");
            assertEquals("GENERAL", dto.getCategory());
        }

        @Test
        @DisplayName("Should handle FILE_UPLOAD category")
        void categoryFileUpload() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setCategory("FILE_UPLOAD");
            assertEquals("FILE_UPLOAD", dto.getCategory());
        }

        @Test
        @DisplayName("Should handle SECURITY category")
        void categorySecurity() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setCategory("SECURITY");
            assertEquals("SECURITY", dto.getCategory());
        }

        @Test
        @DisplayName("Should handle PARSER category")
        void categoryParser() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setCategory("PARSER");
            assertEquals("PARSER", dto.getCategory());
        }

        @Test
        @DisplayName("Should handle EMAIL category")
        void categoryEmail() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setCategory("EMAIL");
            assertEquals("EMAIL", dto.getCategory());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty values")
        void emptyValues() {
            SystemSettingDTO dto = new SystemSettingDTO(
                    1L, "", "", "", "", "", "", "");
            assertEquals("", dto.getSettingKey());
            assertEquals("", dto.getSettingValue());
        }

        @Test
        @DisplayName("Should handle null values")
        void nullValues() {
            SystemSettingDTO dto = new SystemSettingDTO(
                    null, null, null, null, null, null, null, null);
            assertNull(dto.getId());
            assertNull(dto.getSettingKey());
            assertNull(dto.getSettingValue());
        }

        @Test
        @DisplayName("Should handle special characters in value")
        void specialCharactersInValue() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setSettingValue("Special!@#$%^&*()_+{}|:<>?");
            assertEquals("Special!@#$%^&*()_+{}|:<>?", dto.getSettingValue());
        }

        @Test
        @DisplayName("Should handle long values")
        void longValues() {
            SystemSettingDTO dto = new SystemSettingDTO();
            String longValue = "x".repeat(1000);
            dto.setSettingValue(longValue);
            assertEquals(longValue, dto.getSettingValue());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            SystemSettingDTO dto1 = new SystemSettingDTO(
                    1L, "key", "value", "GENERAL", "desc", "STRING", "admin", "2025-01-01");
            SystemSettingDTO dto2 = new SystemSettingDTO(
                    1L, "key", "value", "GENERAL", "desc", "STRING", "admin", "2025-01-01");

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different values")
        void equals_DifferentValues_ReturnsFalse() {
            SystemSettingDTO dto1 = new SystemSettingDTO();
            dto1.setSettingKey("key1");
            SystemSettingDTO dto2 = new SystemSettingDTO();
            dto2.setSettingKey("key2");

            assertNotEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with all fields")
        void toString_ContainsAllFields() {
            SystemSettingDTO dto = new SystemSettingDTO();
            dto.setId(1L);
            dto.setSettingKey("test.key");
            dto.setSettingValue("test_value");
            dto.setCategory("GENERAL");

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("settingKey=test.key"));
            assertTrue(result.contains("settingValue=test_value"));
            assertTrue(result.contains("category=GENERAL"));
        }
    }
}
