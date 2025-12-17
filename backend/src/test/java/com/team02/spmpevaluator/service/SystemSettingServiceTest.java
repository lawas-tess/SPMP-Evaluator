package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.SystemSetting;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.SystemSettingRepository;
import com.team02.spmpevaluator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SystemSettingService.
 * Tests system setting management (UC 2.15).
 */
@ExtendWith(MockitoExtension.class)
class SystemSettingServiceTest {

    @Mock
    private SystemSettingRepository settingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SystemSettingService systemSettingService;

    private User testAdmin;
    private SystemSetting testSetting;

    @BeforeEach
    void setUp() {
        testAdmin = new User();
        testAdmin.setId(1L);
        testAdmin.setEmail("admin@test.com");

        testSetting = new SystemSetting();
        testSetting.setId(1L);
        testSetting.setSettingKey("ALLOW_STUDENT_REGISTRATION");
        testSetting.setSettingValue("true");
        testSetting.setCategory("REGISTRATION");
        testSetting.setDataType("BOOLEAN");
        testSetting.setDescription("Allow student registration");
    }

    @Nested
    @DisplayName("Get Setting by Key Tests")
    class GetSettingByKeyTests {

        @Test
        @DisplayName("Should return setting when found")
        void getSettingByKey_ExistingSetting_ReturnsSetting() {
            when(settingRepository.findBySettingKey("ALLOW_STUDENT_REGISTRATION"))
                    .thenReturn(Optional.of(testSetting));

            SystemSetting result = systemSettingService.getSettingByKey("ALLOW_STUDENT_REGISTRATION");

            assertNotNull(result);
            assertEquals("ALLOW_STUDENT_REGISTRATION", result.getSettingKey());
            assertEquals("true", result.getSettingValue());
        }

        @Test
        @DisplayName("Should throw exception when setting not found")
        void getSettingByKey_NonExistentSetting_ThrowsException() {
            when(settingRepository.findBySettingKey("INVALID_KEY"))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> systemSettingService.getSettingByKey("INVALID_KEY"));
        }
    }

    @Nested
    @DisplayName("Get Settings by Category Tests")
    class GetSettingsByCategoryTests {

        @Test
        @DisplayName("Should return settings for category")
        void getSettingsByCategory_ValidCategory_ReturnsSettings() {
            when(settingRepository.findByCategory("REGISTRATION"))
                    .thenReturn(Arrays.asList(testSetting));

            List<SystemSetting> result = systemSettingService.getSettingsByCategory("REGISTRATION");

            assertEquals(1, result.size());
            assertEquals("REGISTRATION", result.get(0).getCategory());
        }

        @Test
        @DisplayName("Should return empty list for unknown category")
        void getSettingsByCategory_UnknownCategory_ReturnsEmptyList() {
            when(settingRepository.findByCategory("UNKNOWN"))
                    .thenReturn(List.of());

            List<SystemSetting> result = systemSettingService.getSettingsByCategory("UNKNOWN");

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get All Settings Tests")
    class GetAllSettingsTests {

        @Test
        @DisplayName("Should return all settings")
        void getAllSettings_ReturnsAllSettings() {
            SystemSetting setting2 = new SystemSetting();
            setting2.setId(2L);
            setting2.setSettingKey("MAX_FILE_SIZE");
            setting2.setSettingValue("50");

            when(settingRepository.findAll()).thenReturn(Arrays.asList(testSetting, setting2));

            List<SystemSetting> result = systemSettingService.getAllSettings();

            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("Update Setting Tests")
    class UpdateSettingTests {

        @Test
        @DisplayName("Should update existing setting")
        void updateSetting_ExistingSetting_UpdatesSetting() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
            when(settingRepository.findBySettingKey("ALLOW_STUDENT_REGISTRATION"))
                    .thenReturn(Optional.of(testSetting));
            when(settingRepository.save(any(SystemSetting.class))).thenReturn(testSetting);

            SystemSetting result = systemSettingService.updateSetting(
                    "ALLOW_STUDENT_REGISTRATION", "false", 1L);

            assertNotNull(result);
            assertEquals("false", result.getSettingValue());
            assertEquals(testAdmin, result.getUpdatedBy());
        }

        @Test
        @DisplayName("Should create new setting if not exists")
        void updateSetting_NewSetting_CreatesSetting() {
            SystemSetting newSetting = new SystemSetting();
            newSetting.setSettingKey("NEW_SETTING");
            newSetting.setSettingValue("value");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
            when(settingRepository.findBySettingKey("NEW_SETTING"))
                    .thenReturn(Optional.empty());
            when(settingRepository.save(any(SystemSetting.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            SystemSetting result = systemSettingService.updateSetting("NEW_SETTING", "value", 1L);

            assertNotNull(result);
            assertEquals("NEW_SETTING", result.getSettingKey());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void updateSetting_UserNotFound_ThrowsException() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> systemSettingService.updateSetting("KEY", "value", 999L));
        }
    }

    @Nested
    @DisplayName("Get Boolean Setting Tests")
    class GetBooleanSettingTests {

        @Test
        @DisplayName("Should return true for 'true' value")
        void getBooleanSetting_TrueValue_ReturnsTrue() {
            testSetting.setSettingValue("true");
            when(settingRepository.findBySettingKey("ALLOW_STUDENT_REGISTRATION"))
                    .thenReturn(Optional.of(testSetting));

            boolean result = systemSettingService.getBooleanSetting("ALLOW_STUDENT_REGISTRATION");

            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false for 'false' value")
        void getBooleanSetting_FalseValue_ReturnsFalse() {
            testSetting.setSettingValue("false");
            when(settingRepository.findBySettingKey("ALLOW_STUDENT_REGISTRATION"))
                    .thenReturn(Optional.of(testSetting));

            boolean result = systemSettingService.getBooleanSetting("ALLOW_STUDENT_REGISTRATION");

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Get Integer Setting Tests")
    class GetIntegerSettingTests {

        @Test
        @DisplayName("Should return integer value")
        void getIntegerSetting_ValidInteger_ReturnsInteger() {
            testSetting.setSettingValue("50");
            when(settingRepository.findBySettingKey("MAX_FILE_SIZE"))
                    .thenReturn(Optional.of(testSetting));

            int result = systemSettingService.getIntegerSetting("MAX_FILE_SIZE");

            assertEquals(50, result);
        }

        @Test
        @DisplayName("Should throw exception for non-integer value")
        void getIntegerSetting_NonInteger_ThrowsException() {
            testSetting.setSettingValue("not a number");
            when(settingRepository.findBySettingKey("MAX_FILE_SIZE"))
                    .thenReturn(Optional.of(testSetting));

            assertThrows(NumberFormatException.class, () -> systemSettingService.getIntegerSetting("MAX_FILE_SIZE"));
        }
    }

    @Nested
    @DisplayName("Get String Setting Tests")
    class GetStringSettingTests {

        @Test
        @DisplayName("Should return string value")
        void getStringSetting_ValidString_ReturnsString() {
            testSetting.setSettingValue("test value");
            when(settingRepository.findBySettingKey("APP_NAME"))
                    .thenReturn(Optional.of(testSetting));

            String result = systemSettingService.getStringSetting("APP_NAME");

            assertEquals("test value", result);
        }
    }

    @Nested
    @DisplayName("Data Type Detection Tests")
    class DataTypeDetectionTests {

        @Test
        @DisplayName("Should detect BOOLEAN type for 'true'")
        void updateSetting_BooleanValue_SetsCorrectDataType() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
            when(settingRepository.findBySettingKey("NEW_BOOL"))
                    .thenReturn(Optional.empty());
            when(settingRepository.save(any(SystemSetting.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            SystemSetting result = systemSettingService.updateSetting("NEW_BOOL", "true", 1L);

            assertEquals("BOOLEAN", result.getDataType());
        }

        @Test
        @DisplayName("Should detect NUMBER type for integer")
        void updateSetting_IntegerValue_SetsCorrectDataType() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
            when(settingRepository.findBySettingKey("NEW_NUM"))
                    .thenReturn(Optional.empty());
            when(settingRepository.save(any(SystemSetting.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            SystemSetting result = systemSettingService.updateSetting("NEW_NUM", "42", 1L);

            assertEquals("NUMBER", result.getDataType());
        }

        @Test
        @DisplayName("Should detect TEXT type for string")
        void updateSetting_StringValue_SetsCorrectDataType() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
            when(settingRepository.findBySettingKey("NEW_TEXT"))
                    .thenReturn(Optional.empty());
            when(settingRepository.save(any(SystemSetting.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            SystemSetting result = systemSettingService.updateSetting("NEW_TEXT", "some text", 1L);

            assertEquals("TEXT", result.getDataType());
        }
    }

    @Nested
    @DisplayName("Category Extraction Tests")
    class CategoryExtractionTests {

        @Test
        @DisplayName("Should extract category from key")
        void updateSetting_KeyWithUnderscore_ExtractsCategory() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
            when(settingRepository.findBySettingKey("ALLOW_STUDENT_REGISTRATION"))
                    .thenReturn(Optional.empty());
            when(settingRepository.save(any(SystemSetting.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            SystemSetting result = systemSettingService.updateSetting(
                    "ALLOW_STUDENT_REGISTRATION", "true", 1L);

            assertEquals("REGISTRATION", result.getCategory());
        }
    }
}
