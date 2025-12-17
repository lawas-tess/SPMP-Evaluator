package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.ParserConfiguration;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.ParserConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ParserConfigurationService.
 * Tests parser configuration management including IEEE 1058 clause mappings.
 */
@ExtendWith(MockitoExtension.class)
class ParserConfigurationServiceTest {

    @Mock
    private ParserConfigurationRepository parserConfigurationRepository;

    @InjectMocks
    private ParserConfigurationService parserConfigurationService;

    private User testUser;
    private ParserConfiguration testConfig;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("professor@test.com");
        testUser.setFirstName("Test");
        testUser.setLastName("Professor");

        // Setup test configuration
        testConfig = new ParserConfiguration();
        testConfig.setId(1L);
        testConfig.setName("Test Configuration");
        testConfig.setDescription("Test Description");
        testConfig.setClauseMappings("[]");
        testConfig.setCustomRules("[]");
        testConfig.setIsActive(true);
        testConfig.setIsDefault(false);
        testConfig.setCreatedBy(testUser);
    }

    @Nested
    @DisplayName("Create Configuration Tests")
    class CreateConfigurationTests {

        @Test
        @DisplayName("Should create configuration successfully")
        void createConfiguration_Success() {
            // Arrange
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenReturn(testConfig);

            // Act
            ParserConfiguration result = parserConfigurationService.createConfiguration(testConfig, testUser);

            // Assert
            assertNotNull(result);
            assertEquals("Test Configuration", result.getName());
            verify(parserConfigurationRepository).save(testConfig);
        }

        @Test
        @DisplayName("Should set createdBy when creating configuration")
        void createConfiguration_SetsCreatedBy() {
            // Arrange
            ParserConfiguration newConfig = new ParserConfiguration();
            newConfig.setName("New Config");
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserConfiguration result = parserConfigurationService.createConfiguration(newConfig, testUser);

            // Assert
            assertEquals(testUser, result.getCreatedBy());
        }

        @Test
        @DisplayName("Should unset other defaults when creating default configuration")
        void createConfiguration_UnsetsOtherDefaults() {
            // Arrange
            testConfig.setIsDefault(true);
            ParserConfiguration existingDefault = new ParserConfiguration();
            existingDefault.setId(2L);
            existingDefault.setIsDefault(true);

            when(parserConfigurationRepository.findByIsDefaultTrue()).thenReturn(Optional.of(existingDefault));
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            parserConfigurationService.createConfiguration(testConfig, testUser);

            // Assert
            verify(parserConfigurationRepository, times(2)).save(any(ParserConfiguration.class));
            assertFalse(existingDefault.getIsDefault());
        }

        @Test
        @DisplayName("Should not unset defaults when creating non-default configuration")
        void createConfiguration_NoUnsetForNonDefault() {
            // Arrange
            testConfig.setIsDefault(false);
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenReturn(testConfig);

            // Act
            parserConfigurationService.createConfiguration(testConfig, testUser);

            // Assert
            verify(parserConfigurationRepository, never()).findByIsDefaultTrue();
        }
    }

    @Nested
    @DisplayName("Update Configuration Tests")
    class UpdateConfigurationTests {

        @Test
        @DisplayName("Should update configuration successfully")
        void updateConfiguration_Success() {
            // Arrange
            ParserConfiguration updatedConfig = new ParserConfiguration();
            updatedConfig.setName("Updated Name");
            updatedConfig.setDescription("Updated Description");
            updatedConfig.setClauseMappings("[updated]");
            updatedConfig.setCustomRules("[rules]");
            updatedConfig.setIsActive(false);
            updatedConfig.setIsDefault(false);

            when(parserConfigurationRepository.findById(1L)).thenReturn(Optional.of(testConfig));
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenReturn(testConfig);

            // Act
            ParserConfiguration result = parserConfigurationService.updateConfiguration(1L, updatedConfig);

            // Assert
            assertNotNull(result);
            verify(parserConfigurationRepository).save(testConfig);
            assertEquals("Updated Name", testConfig.getName());
            assertEquals("Updated Description", testConfig.getDescription());
        }

        @Test
        @DisplayName("Should throw exception when configuration not found")
        void updateConfiguration_NotFound() {
            // Arrange
            when(parserConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parserConfigurationService.updateConfiguration(99L, testConfig));
            assertTrue(exception.getMessage().contains("not found"));
        }

        @Test
        @DisplayName("Should unset other defaults when updating to default")
        void updateConfiguration_UnsetsOtherDefaultsWhenSettingDefault() {
            // Arrange
            testConfig.setIsDefault(false);
            ParserConfiguration updatedConfig = new ParserConfiguration();
            updatedConfig.setName("Updated");
            updatedConfig.setIsDefault(true);
            updatedConfig.setIsActive(true);

            ParserConfiguration existingDefault = new ParserConfiguration();
            existingDefault.setId(2L);
            existingDefault.setIsDefault(true);

            when(parserConfigurationRepository.findById(1L)).thenReturn(Optional.of(testConfig));
            when(parserConfigurationRepository.findByIsDefaultTrue()).thenReturn(Optional.of(existingDefault));
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            parserConfigurationService.updateConfiguration(1L, updatedConfig);

            // Assert
            assertFalse(existingDefault.getIsDefault());
        }
    }

    @Nested
    @DisplayName("Get Configuration Tests")
    class GetConfigurationTests {

        @Test
        @DisplayName("Should get configuration by ID")
        void getConfigurationById_Success() {
            // Arrange
            when(parserConfigurationRepository.findById(1L)).thenReturn(Optional.of(testConfig));

            // Act
            Optional<ParserConfiguration> result = parserConfigurationService.getConfigurationById(1L);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("Test Configuration", result.get().getName());
        }

        @Test
        @DisplayName("Should return empty when configuration not found")
        void getConfigurationById_NotFound() {
            // Arrange
            when(parserConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

            // Act
            Optional<ParserConfiguration> result = parserConfigurationService.getConfigurationById(99L);

            // Assert
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should get all active configurations")
        void getActiveConfigurations_Success() {
            // Arrange
            ParserConfiguration config2 = new ParserConfiguration();
            config2.setId(2L);
            config2.setIsActive(true);

            when(parserConfigurationRepository.findByIsActiveTrue())
                    .thenReturn(Arrays.asList(testConfig, config2));

            // Act
            List<ParserConfiguration> result = parserConfigurationService.getActiveConfigurations();

            // Assert
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should get configurations by user")
        void getConfigurationsByUser_Success() {
            // Arrange
            when(parserConfigurationRepository.findByCreatedByOrderByCreatedAtDesc(testUser))
                    .thenReturn(Arrays.asList(testConfig));

            // Act
            List<ParserConfiguration> result = parserConfigurationService.getConfigurationsByUser(testUser);

            // Assert
            assertEquals(1, result.size());
            assertEquals("Test Configuration", result.get(0).getName());
        }

        @Test
        @DisplayName("Should get default configuration")
        void getDefaultConfiguration_Success() {
            // Arrange
            testConfig.setIsDefault(true);
            when(parserConfigurationRepository.findByIsDefaultTrue()).thenReturn(Optional.of(testConfig));

            // Act
            Optional<ParserConfiguration> result = parserConfigurationService.getDefaultConfiguration();

            // Assert
            assertTrue(result.isPresent());
            assertTrue(result.get().getIsDefault());
        }

        @Test
        @DisplayName("Should return empty when no default exists")
        void getDefaultConfiguration_NoDefault() {
            // Arrange
            when(parserConfigurationRepository.findByIsDefaultTrue()).thenReturn(Optional.empty());

            // Act
            Optional<ParserConfiguration> result = parserConfigurationService.getDefaultConfiguration();

            // Assert
            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("Delete Configuration Tests")
    class DeleteConfigurationTests {

        @Test
        @DisplayName("Should delete configuration successfully")
        void deleteConfiguration_Success() {
            // Arrange
            testConfig.setIsDefault(false);
            when(parserConfigurationRepository.findById(1L)).thenReturn(Optional.of(testConfig));

            // Act
            parserConfigurationService.deleteConfiguration(1L);

            // Assert
            verify(parserConfigurationRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting default configuration")
        void deleteConfiguration_CannotDeleteDefault() {
            // Arrange
            testConfig.setIsDefault(true);
            when(parserConfigurationRepository.findById(1L)).thenReturn(Optional.of(testConfig));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parserConfigurationService.deleteConfiguration(1L));
            assertTrue(exception.getMessage().contains("default"));
        }

        @Test
        @DisplayName("Should throw exception when configuration not found")
        void deleteConfiguration_NotFound() {
            // Arrange
            when(parserConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parserConfigurationService.deleteConfiguration(99L));
            assertTrue(exception.getMessage().contains("not found"));
        }
    }

    @Nested
    @DisplayName("Set As Default Tests")
    class SetAsDefaultTests {

        @Test
        @DisplayName("Should set configuration as default")
        void setAsDefault_Success() {
            // Arrange
            when(parserConfigurationRepository.findById(1L)).thenReturn(Optional.of(testConfig));
            when(parserConfigurationRepository.findByIsDefaultTrue()).thenReturn(Optional.empty());
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserConfiguration result = parserConfigurationService.setAsDefault(1L);

            // Assert
            assertTrue(result.getIsDefault());
        }

        @Test
        @DisplayName("Should unset previous default")
        void setAsDefault_UnsetsPrevious() {
            // Arrange
            ParserConfiguration previousDefault = new ParserConfiguration();
            previousDefault.setId(2L);
            previousDefault.setIsDefault(true);

            when(parserConfigurationRepository.findById(1L)).thenReturn(Optional.of(testConfig));
            when(parserConfigurationRepository.findByIsDefaultTrue()).thenReturn(Optional.of(previousDefault));
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            parserConfigurationService.setAsDefault(1L);

            // Assert
            assertFalse(previousDefault.getIsDefault());
            assertTrue(testConfig.getIsDefault());
        }

        @Test
        @DisplayName("Should throw exception when configuration not found")
        void setAsDefault_NotFound() {
            // Arrange
            when(parserConfigurationRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> parserConfigurationService.setAsDefault(99L));
            assertTrue(exception.getMessage().contains("not found"));
        }
    }

    @Nested
    @DisplayName("Create Default Configuration Tests")
    class CreateDefaultConfigurationTests {

        @Test
        @DisplayName("Should create default configuration when none exists")
        void createDefaultConfiguration_CreatesNew() {
            // Arrange
            when(parserConfigurationRepository.findByIsDefaultTrue()).thenReturn(Optional.empty());
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserConfiguration result = parserConfigurationService.createDefaultConfiguration(testUser);

            // Assert
            assertNotNull(result);
            assertEquals("IEEE 1058 Standard Configuration", result.getName());
            assertTrue(result.getIsDefault());
            assertTrue(result.getIsActive());
            assertEquals(testUser, result.getCreatedBy());
        }

        @Test
        @DisplayName("Should return existing default when one exists")
        void createDefaultConfiguration_ReturnsExisting() {
            // Arrange
            testConfig.setIsDefault(true);
            testConfig.setName("Existing Default");
            when(parserConfigurationRepository.findByIsDefaultTrue()).thenReturn(Optional.of(testConfig));

            // Act
            ParserConfiguration result = parserConfigurationService.createDefaultConfiguration(testUser);

            // Assert
            assertEquals("Existing Default", result.getName());
            verify(parserConfigurationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should include IEEE 1058 clause mappings")
        void createDefaultConfiguration_IncludesClauseMappings() {
            // Arrange
            when(parserConfigurationRepository.findByIsDefaultTrue()).thenReturn(Optional.empty());
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserConfiguration result = parserConfigurationService.createDefaultConfiguration(testUser);

            // Assert
            assertNotNull(result.getClauseMappings());
            assertTrue(result.getClauseMappings().contains("Overview"));
            assertTrue(result.getClauseMappings().contains("Project Organization"));
        }

        @Test
        @DisplayName("Should include default custom rules")
        void createDefaultConfiguration_IncludesCustomRules() {
            // Arrange
            when(parserConfigurationRepository.findByIsDefaultTrue()).thenReturn(Optional.empty());
            when(parserConfigurationRepository.save(any(ParserConfiguration.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            ParserConfiguration result = parserConfigurationService.createDefaultConfiguration(testUser);

            // Assert
            assertNotNull(result.getCustomRules());
            assertTrue(result.getCustomRules().contains("clause completeness"));
        }
    }
}
