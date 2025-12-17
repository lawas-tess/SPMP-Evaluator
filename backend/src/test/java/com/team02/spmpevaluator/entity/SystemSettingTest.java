package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SystemSetting entity.
 * UC 2.15: Admin System Settings
 */
@DisplayName("SystemSetting Entity Tests")
class SystemSettingTest {

    private SystemSetting setting;
    private User admin;

    @BeforeEach
    void setUp() {
        setting = new SystemSetting();
        admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            SystemSetting entity = new SystemSetting();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getSettingKey());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime now = LocalDateTime.now();
            SystemSetting entity = new SystemSetting(
                    1L, "max.file.size", "10485760",
                    "Upload", "Maximum file size for document upload in bytes",
                    "INTEGER", admin, now);

            assertEquals(1L, entity.getId());
            assertEquals("max.file.size", entity.getSettingKey());
            assertEquals("10485760", entity.getSettingValue());
            assertEquals("Upload", entity.getCategory());
            assertEquals("Maximum file size for document upload in bytes", entity.getDescription());
            assertEquals("INTEGER", entity.getDataType());
            assertEquals(admin, entity.getUpdatedBy());
            assertEquals(now, entity.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testId() {
            setting.setId(100L);
            assertEquals(100L, setting.getId());
        }

        @Test
        @DisplayName("Should set and get settingKey")
        void testSettingKey() {
            setting.setSettingKey("app.name");
            assertEquals("app.name", setting.getSettingKey());
        }

        @Test
        @DisplayName("Should set and get settingValue")
        void testSettingValue() {
            setting.setSettingValue("SPMP Evaluator");
            assertEquals("SPMP Evaluator", setting.getSettingValue());
        }

        @Test
        @DisplayName("Should set and get category")
        void testCategory() {
            setting.setCategory("General");
            assertEquals("General", setting.getCategory());
        }

        @Test
        @DisplayName("Should set and get description")
        void testDescription() {
            setting.setDescription("Application display name");
            assertEquals("Application display name", setting.getDescription());
        }

        @Test
        @DisplayName("Should set and get dataType")
        void testDataType() {
            setting.setDataType("BOOLEAN");
            assertEquals("BOOLEAN", setting.getDataType());
        }

        @Test
        @DisplayName("Should set and get updatedBy")
        void testUpdatedBy() {
            setting.setUpdatedBy(admin);
            assertEquals(admin, setting.getUpdatedBy());
        }

        @Test
        @DisplayName("Should set and get updatedAt")
        void testUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            setting.setUpdatedAt(now);
            assertEquals(now, setting.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have STRING as default dataType")
        void defaultDataType() {
            SystemSetting entity = new SystemSetting();
            assertEquals("STRING", entity.getDataType());
        }
    }

    @Nested
    @DisplayName("Data Type Tests")
    class DataTypeTests {

        @Test
        @DisplayName("Should accept STRING data type")
        void stringDataType() {
            setting.setDataType("STRING");
            setting.setSettingValue("Hello World");
            assertEquals("STRING", setting.getDataType());
        }

        @Test
        @DisplayName("Should accept BOOLEAN data type")
        void booleanDataType() {
            setting.setDataType("BOOLEAN");
            setting.setSettingValue("true");
            assertEquals("BOOLEAN", setting.getDataType());
        }

        @Test
        @DisplayName("Should accept INTEGER data type")
        void integerDataType() {
            setting.setDataType("INTEGER");
            setting.setSettingValue("100");
            assertEquals("INTEGER", setting.getDataType());
        }

        @Test
        @DisplayName("Should accept DECIMAL data type")
        void decimalDataType() {
            setting.setDataType("DECIMAL");
            setting.setSettingValue("99.99");
            assertEquals("DECIMAL", setting.getDataType());
        }
    }

    @Nested
    @DisplayName("Category Tests")
    class CategoryTests {

        @Test
        @DisplayName("Should support General category")
        void generalCategory() {
            setting.setCategory("General");
            setting.setSettingKey("app.version");
            setting.setSettingValue("1.0.0");
            assertEquals("General", setting.getCategory());
        }

        @Test
        @DisplayName("Should support Upload category")
        void uploadCategory() {
            setting.setCategory("Upload");
            setting.setSettingKey("max.file.size");
            setting.setSettingValue("10485760");
            assertEquals("Upload", setting.getCategory());
        }

        @Test
        @DisplayName("Should support Evaluation category")
        void evaluationCategory() {
            setting.setCategory("Evaluation");
            setting.setSettingKey("compliance.threshold");
            setting.setSettingValue("70");
            assertEquals("Evaluation", setting.getCategory());
        }

        @Test
        @DisplayName("Should support Security category")
        void securityCategory() {
            setting.setCategory("Security");
            setting.setSettingKey("session.timeout");
            setting.setSettingValue("3600");
            assertEquals("Security", setting.getCategory());
        }
    }

    @Nested
    @DisplayName("System Setting Use Cases Tests")
    class UseCaseTests {

        @Test
        @DisplayName("Should configure compliance threshold (UC 2.15)")
        void complianceThresholdSetting() {
            setting.setSettingKey("compliance.threshold");
            setting.setSettingValue("70");
            setting.setCategory("Evaluation");
            setting.setDescription("Minimum score for document to be considered compliant");
            setting.setDataType("INTEGER");
            setting.setUpdatedBy(admin);

            assertEquals("70", setting.getSettingValue());
            assertEquals("Evaluation", setting.getCategory());
        }

        @Test
        @DisplayName("Should configure file upload limit")
        void fileUploadLimitSetting() {
            setting.setSettingKey("upload.max.size");
            setting.setSettingValue("20971520"); // 20MB
            setting.setCategory("Upload");
            setting.setDescription("Maximum file upload size in bytes");
            setting.setDataType("INTEGER");

            assertEquals("20971520", setting.getSettingValue());
        }

        @Test
        @DisplayName("Should configure email notifications")
        void emailNotificationsSetting() {
            setting.setSettingKey("notifications.email.enabled");
            setting.setSettingValue("true");
            setting.setCategory("Notifications");
            setting.setDescription("Enable email notifications");
            setting.setDataType("BOOLEAN");

            assertEquals("true", setting.getSettingValue());
            assertEquals("BOOLEAN", setting.getDataType());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same id")
        void equalsWithSameId() {
            SystemSetting s1 = new SystemSetting();
            s1.setId(1L);
            s1.setSettingKey("key1");

            SystemSetting s2 = new SystemSetting();
            s2.setId(1L);
            s2.setSettingKey("key1");

            assertEquals(s1, s2);
            assertEquals(s1.hashCode(), s2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different ids")
        void notEqualsWithDifferentIds() {
            SystemSetting s1 = new SystemSetting();
            s1.setId(1L);

            SystemSetting s2 = new SystemSetting();
            s2.setId(2L);

            assertNotEquals(s1, s2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void toStringContainsFields() {
            setting.setId(1L);
            setting.setSettingKey("test.setting");
            setting.setSettingValue("test_value");

            String str = setting.toString();
            assertNotNull(str);
            assertTrue(str.contains("1"));
        }
    }
}
