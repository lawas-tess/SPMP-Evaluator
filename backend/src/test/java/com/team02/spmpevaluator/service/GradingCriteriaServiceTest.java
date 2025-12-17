package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.dto.GradingCriteriaDTO;
import com.team02.spmpevaluator.entity.GradingCriteria;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.GradingCriteriaRepository;
import com.team02.spmpevaluator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GradingCriteriaService.
 * UC 2.7 - Tests for grading criteria management operations.
 */
@ExtendWith(MockitoExtension.class)
class GradingCriteriaServiceTest {

    @Mock
    private GradingCriteriaRepository gradingCriteriaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GradingCriteriaService gradingCriteriaService;

    private User testProfessor;
    private GradingCriteria testCriteria;
    private GradingCriteriaDTO testDTO;

    @BeforeEach
    void setUp() {
        // Setup test professor
        testProfessor = new User();
        testProfessor.setId(1L);
        testProfessor.setEmail("professor@test.com");
        testProfessor.setFirstName("Test");
        testProfessor.setLastName("Professor");

        // Setup test criteria entity
        testCriteria = new GradingCriteria();
        testCriteria.setId(1L);
        testCriteria.setName("Test Criteria");
        testCriteria.setDescription("Test Description");
        testCriteria.setCreatedBy(testProfessor);
        testCriteria.setActive(true);
        testCriteria.setOverviewWeight(10);
        testCriteria.setReferencesWeight(5);
        testCriteria.setDefinitionsWeight(5);
        testCriteria.setOrganizationWeight(15);
        testCriteria.setManagerialProcessWeight(20);
        testCriteria.setTechnicalProcessWeight(20);
        testCriteria.setSupportingProcessWeight(15);
        testCriteria.setAdditionalPlansWeight(10);

        // Setup test DTO with valid weights (sum = 100)
        testDTO = new GradingCriteriaDTO();
        testDTO.setName("Test Criteria");
        testDTO.setDescription("Test Description");
        testDTO.setActive(true);
        testDTO.setOverviewWeight(10);
        testDTO.setReferencesWeight(5);
        testDTO.setDefinitionsWeight(5);
        testDTO.setOrganizationWeight(15);
        testDTO.setManagerialProcessWeight(20);
        testDTO.setTechnicalProcessWeight(20);
        testDTO.setSupportingProcessWeight(15);
        testDTO.setAdditionalPlansWeight(10);
    }

    @Nested
    @DisplayName("Create Grading Criteria Tests")
    class CreateGradingCriteriaTests {

        @Test
        @DisplayName("Should create grading criteria successfully")
        void createGradingCriteria_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testProfessor));
            when(gradingCriteriaRepository.existsByNameAndCreatedBy(testDTO.getName(), testProfessor))
                    .thenReturn(false);
            when(gradingCriteriaRepository.save(any(GradingCriteria.class))).thenReturn(testCriteria);

            // Act
            GradingCriteriaDTO result = gradingCriteriaService.createGradingCriteria(testDTO, 1L);

            // Assert
            assertNotNull(result);
            assertEquals("Test Criteria", result.getName());
            verify(gradingCriteriaRepository).deactivateAllCriteriaForProfessor(1L);
            verify(gradingCriteriaRepository).save(any(GradingCriteria.class));
        }

        @Test
        @DisplayName("Should throw exception when professor not found")
        void createGradingCriteria_ProfessorNotFound() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.createGradingCriteria(testDTO, 99L));
            assertEquals("Professor not found", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when weights don't sum to 100")
        void createGradingCriteria_InvalidWeights() {
            // Arrange
            testDTO.setOverviewWeight(50); // Now total is 140
            when(userRepository.findById(1L)).thenReturn(Optional.of(testProfessor));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.createGradingCriteria(testDTO, 1L));
            assertTrue(exception.getMessage().contains("Section weights must sum to 100"));
        }

        @Test
        @DisplayName("Should throw exception when name already exists")
        void createGradingCriteria_DuplicateName() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testProfessor));
            when(gradingCriteriaRepository.existsByNameAndCreatedBy(testDTO.getName(), testProfessor)).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.createGradingCriteria(testDTO, 1L));
            assertEquals("A grading criteria with this name already exists", exception.getMessage());
        }

        @Test
        @DisplayName("Should not deactivate others when creating inactive criteria")
        void createGradingCriteria_InactiveDoesNotDeactivateOthers() {
            // Arrange
            testDTO.setActive(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testProfessor));
            when(gradingCriteriaRepository.existsByNameAndCreatedBy(testDTO.getName(), testProfessor))
                    .thenReturn(false);

            GradingCriteria inactiveCriteria = new GradingCriteria();
            inactiveCriteria.setId(2L);
            inactiveCriteria.setName("Test Criteria");
            inactiveCriteria.setCreatedBy(testProfessor);
            inactiveCriteria.setActive(false);
            when(gradingCriteriaRepository.save(any(GradingCriteria.class))).thenReturn(inactiveCriteria);

            // Act
            gradingCriteriaService.createGradingCriteria(testDTO, 1L);

            // Assert
            verify(gradingCriteriaRepository, never()).deactivateAllCriteriaForProfessor(anyLong());
        }
    }

    @Nested
    @DisplayName("Update Grading Criteria Tests")
    class UpdateGradingCriteriaTests {

        @Test
        @DisplayName("Should update grading criteria successfully")
        void updateGradingCriteria_Success() {
            // Arrange
            testDTO.setName("Updated Criteria");
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));
            when(gradingCriteriaRepository.findByNameAndCreatedBy("Updated Criteria", testProfessor))
                    .thenReturn(Optional.empty());
            when(gradingCriteriaRepository.save(any(GradingCriteria.class))).thenReturn(testCriteria);

            // Act
            GradingCriteriaDTO result = gradingCriteriaService.updateGradingCriteria(1L, testDTO, 1L);

            // Assert
            assertNotNull(result);
            verify(gradingCriteriaRepository).save(any(GradingCriteria.class));
        }

        @Test
        @DisplayName("Should throw exception when criteria not found")
        void updateGradingCriteria_NotFound() {
            // Arrange
            when(gradingCriteriaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.updateGradingCriteria(99L, testDTO, 1L));
            assertEquals("Grading criteria not found", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when not owner")
        void updateGradingCriteria_NotOwner() {
            // Arrange
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));

            // Act & Assert (different professor ID)
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.updateGradingCriteria(1L, testDTO, 2L));
            assertEquals("You can only update your own grading criteria", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception for invalid weights")
        void updateGradingCriteria_InvalidWeights() {
            // Arrange
            testDTO.setOverviewWeight(100); // Total exceeds 100
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.updateGradingCriteria(1L, testDTO, 1L));
            assertTrue(exception.getMessage().contains("Section weights must sum to 100"));
        }

        @Test
        @DisplayName("Should throw exception when duplicate name exists")
        void updateGradingCriteria_DuplicateName() {
            // Arrange
            testDTO.setName("Existing Criteria");
            GradingCriteria existingCriteria = new GradingCriteria();
            existingCriteria.setId(2L); // Different ID
            existingCriteria.setName("Existing Criteria");

            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));
            when(gradingCriteriaRepository.findByNameAndCreatedBy("Existing Criteria", testProfessor))
                    .thenReturn(Optional.of(existingCriteria));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.updateGradingCriteria(1L, testDTO, 1L));
            assertEquals("A grading criteria with this name already exists", exception.getMessage());
        }

        @Test
        @DisplayName("Should allow updating with same name")
        void updateGradingCriteria_SameNameAllowed() {
            // Arrange
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));
            when(gradingCriteriaRepository.findByNameAndCreatedBy(testDTO.getName(), testProfessor))
                    .thenReturn(Optional.of(testCriteria)); // Same entity
            when(gradingCriteriaRepository.save(any(GradingCriteria.class))).thenReturn(testCriteria);

            // Act
            GradingCriteriaDTO result = gradingCriteriaService.updateGradingCriteria(1L, testDTO, 1L);

            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should deactivate others when activating criteria")
        void updateGradingCriteria_ActivatesDeactivatesOthers() {
            // Arrange
            testCriteria.setActive(false); // Was inactive
            testDTO.setActive(true); // Now activating

            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));
            when(gradingCriteriaRepository.findByNameAndCreatedBy(testDTO.getName(), testProfessor))
                    .thenReturn(Optional.empty());
            when(gradingCriteriaRepository.save(any(GradingCriteria.class))).thenReturn(testCriteria);

            // Act
            gradingCriteriaService.updateGradingCriteria(1L, testDTO, 1L);

            // Assert
            verify(gradingCriteriaRepository).deactivateAllCriteriaForProfessor(1L);
        }
    }

    @Nested
    @DisplayName("Delete Grading Criteria Tests")
    class DeleteGradingCriteriaTests {

        @Test
        @DisplayName("Should delete grading criteria successfully")
        void deleteGradingCriteria_Success() {
            // Arrange
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));

            // Act
            gradingCriteriaService.deleteGradingCriteria(1L, 1L);

            // Assert
            verify(gradingCriteriaRepository).delete(testCriteria);
        }

        @Test
        @DisplayName("Should throw exception when criteria not found")
        void deleteGradingCriteria_NotFound() {
            // Arrange
            when(gradingCriteriaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.deleteGradingCriteria(99L, 1L));
            assertEquals("Grading criteria not found", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when not owner")
        void deleteGradingCriteria_NotOwner() {
            // Arrange
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.deleteGradingCriteria(1L, 2L));
            assertEquals("You can only delete your own grading criteria", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Grading Criteria Tests")
    class GetGradingCriteriaTests {

        @Test
        @DisplayName("Should get criteria by professor")
        void getGradingCriteriaByProfessor_Success() {
            // Arrange
            GradingCriteria criteria2 = new GradingCriteria();
            criteria2.setId(2L);
            criteria2.setName("Second Criteria");
            criteria2.setCreatedBy(testProfessor);

            when(gradingCriteriaRepository.findByProfessorId(1L))
                    .thenReturn(Arrays.asList(testCriteria, criteria2));

            // Act
            List<GradingCriteriaDTO> result = gradingCriteriaService.getGradingCriteriaByProfessor(1L);

            // Assert
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no criteria exist")
        void getGradingCriteriaByProfessor_Empty() {
            // Arrange
            when(gradingCriteriaRepository.findByProfessorId(1L)).thenReturn(Collections.emptyList());

            // Act
            List<GradingCriteriaDTO> result = gradingCriteriaService.getGradingCriteriaByProfessor(1L);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should get criteria by ID")
        void getGradingCriteriaById_Success() {
            // Arrange
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));

            // Act
            GradingCriteriaDTO result = gradingCriteriaService.getGradingCriteriaById(1L);

            // Assert
            assertNotNull(result);
            assertEquals("Test Criteria", result.getName());
        }

        @Test
        @DisplayName("Should throw exception when criteria not found by ID")
        void getGradingCriteriaById_NotFound() {
            // Arrange
            when(gradingCriteriaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.getGradingCriteriaById(99L));
            assertEquals("Grading criteria not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Active Criteria Tests")
    class GetActiveCriteriaTests {

        @Test
        @DisplayName("Should get active criteria for professor")
        void getActiveCriteriaByProfessor_Success() {
            // Arrange
            when(gradingCriteriaRepository.findActiveCriteriaByProfessorId(1L))
                    .thenReturn(Optional.of(testCriteria));

            // Act
            GradingCriteriaDTO result = gradingCriteriaService.getActiveCriteriaByProfessor(1L);

            // Assert
            assertNotNull(result);
            assertEquals("Test Criteria", result.getName());
        }

        @Test
        @DisplayName("Should return default criteria when no active criteria exists")
        void getActiveCriteriaByProfessor_ReturnsDefault() {
            // Arrange
            when(gradingCriteriaRepository.findActiveCriteriaByProfessorId(1L))
                    .thenReturn(Optional.empty());

            // Act
            GradingCriteriaDTO result = gradingCriteriaService.getActiveCriteriaByProfessor(1L);

            // Assert
            assertNotNull(result);
            assertEquals("Default IEEE 1058 Weights", result.getName());
            assertTrue(result.isActive());
        }
    }

    @Nested
    @DisplayName("Set Active Grading Criteria Tests")
    class SetActiveGradingCriteriaTests {

        @Test
        @DisplayName("Should set criteria as active")
        void setActiveGradingCriteria_Success() {
            // Arrange
            testCriteria.setActive(false);
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));
            when(gradingCriteriaRepository.save(any(GradingCriteria.class))).thenReturn(testCriteria);

            // Act
            GradingCriteriaDTO result = gradingCriteriaService.setActiveGradingCriteria(1L, 1L);

            // Assert
            assertNotNull(result);
            verify(gradingCriteriaRepository).deactivateAllCriteriaForProfessor(1L);

            ArgumentCaptor<GradingCriteria> captor = ArgumentCaptor.forClass(GradingCriteria.class);
            verify(gradingCriteriaRepository).save(captor.capture());
            assertTrue(captor.getValue().isActive());
        }

        @Test
        @DisplayName("Should throw exception when criteria not found")
        void setActiveGradingCriteria_NotFound() {
            // Arrange
            when(gradingCriteriaRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.setActiveGradingCriteria(99L, 1L));
            assertEquals("Grading criteria not found", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when not owner")
        void setActiveGradingCriteria_NotOwner() {
            // Arrange
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> gradingCriteriaService.setActiveGradingCriteria(1L, 2L));
            assertEquals("You can only activate your own grading criteria", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Default Criteria Tests")
    class DefaultCriteriaTests {

        @Test
        @DisplayName("Should return default criteria with correct weights")
        void getDefaultCriteria_CorrectWeights() {
            // Act
            GradingCriteriaDTO result = gradingCriteriaService.getDefaultCriteria();

            // Assert
            assertNotNull(result);
            assertEquals("Default IEEE 1058 Weights", result.getName());
            assertEquals(10, result.getOverviewWeight());
            assertEquals(5, result.getReferencesWeight());
            assertEquals(5, result.getDefinitionsWeight());
            assertEquals(15, result.getOrganizationWeight());
            assertEquals(20, result.getManagerialProcessWeight());
            assertEquals(20, result.getTechnicalProcessWeight());
            assertEquals(15, result.getSupportingProcessWeight());
            assertEquals(10, result.getAdditionalPlansWeight());
            assertTrue(result.isValidWeights());
        }

        @Test
        @DisplayName("Default criteria should be marked as active")
        void getDefaultCriteria_IsActive() {
            // Act
            GradingCriteriaDTO result = gradingCriteriaService.getDefaultCriteria();

            // Assert
            assertTrue(result.isActive());
        }
    }

    @Nested
    @DisplayName("DTO Conversion Tests")
    class DTOConversionTests {

        @Test
        @DisplayName("Should correctly convert entity to DTO with timestamps")
        void convertToDTO_WithTimestamps() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            testCriteria.setCreatedAt(now);
            testCriteria.setUpdatedAt(now);
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));

            // Act
            GradingCriteriaDTO result = gradingCriteriaService.getGradingCriteriaById(1L);

            // Assert
            assertNotNull(result.getCreatedAt());
            assertNotNull(result.getUpdatedAt());
        }

        @Test
        @DisplayName("Should include professor name in DTO")
        void convertToDTO_IncludesProfessorName() {
            // Arrange
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));

            // Act
            GradingCriteriaDTO result = gradingCriteriaService.getGradingCriteriaById(1L);

            // Assert
            assertEquals("Test Professor", result.getCreatedByName());
            assertEquals(1L, result.getCreatedById());
        }

        @Test
        @DisplayName("Should include all weights in DTO")
        void convertToDTO_IncludesAllWeights() {
            // Arrange
            when(gradingCriteriaRepository.findById(1L)).thenReturn(Optional.of(testCriteria));

            // Act
            GradingCriteriaDTO result = gradingCriteriaService.getGradingCriteriaById(1L);

            // Assert
            assertEquals(10, result.getOverviewWeight());
            assertEquals(5, result.getReferencesWeight());
            assertEquals(5, result.getDefinitionsWeight());
            assertEquals(15, result.getOrganizationWeight());
            assertEquals(20, result.getManagerialProcessWeight());
            assertEquals(20, result.getTechnicalProcessWeight());
            assertEquals(15, result.getSupportingProcessWeight());
            assertEquals(10, result.getAdditionalPlansWeight());
        }
    }
}
