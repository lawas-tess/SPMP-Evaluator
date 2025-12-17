package com.team02.spmpevaluator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team02.spmpevaluator.dto.GradingCriteriaDTO;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.GradingCriteriaService;
import com.team02.spmpevaluator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("GradingCriteriaController Integration Tests")
class GradingCriteriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GradingCriteriaService gradingCriteriaService;

    @MockBean
    private UserService userService;

    private User professorUser;
    private User studentUser;
    private GradingCriteriaDTO testCriteriaDTO;
    private GradingCriteriaDTO defaultCriteriaDTO;

    @BeforeEach
    void setUp() {
        // Setup professor user
        professorUser = new User();
        professorUser.setId(1L);
        professorUser.setUsername("professor");
        professorUser.setEmail("professor@test.com");
        professorUser.setRole(Role.PROFESSOR);

        // Setup student user
        studentUser = new User();
        studentUser.setId(2L);
        studentUser.setUsername("student");
        studentUser.setEmail("student@test.com");
        studentUser.setRole(Role.STUDENT);

        // Setup test grading criteria DTO
        testCriteriaDTO = new GradingCriteriaDTO();
        testCriteriaDTO.setId(1L);
        testCriteriaDTO.setName("Custom Criteria");
        testCriteriaDTO.setDescription("Custom grading criteria for SPMP evaluation");
        testCriteriaDTO.setCreatedById(1L);
        testCriteriaDTO.setCreatedByName("professor");
        testCriteriaDTO.setActive(true);
        testCriteriaDTO.setOverviewWeight(10);
        testCriteriaDTO.setReferencesWeight(5);
        testCriteriaDTO.setDefinitionsWeight(5);
        testCriteriaDTO.setOrganizationWeight(15);
        testCriteriaDTO.setManagerialProcessWeight(20);
        testCriteriaDTO.setTechnicalProcessWeight(20);
        testCriteriaDTO.setSupportingProcessWeight(15);
        testCriteriaDTO.setAdditionalPlansWeight(10);

        // Setup default criteria DTO
        defaultCriteriaDTO = new GradingCriteriaDTO();
        defaultCriteriaDTO.setId(0L);
        defaultCriteriaDTO.setName("IEEE 1058 Default");
        defaultCriteriaDTO.setDescription("Default IEEE 1058 standard weights");
        defaultCriteriaDTO.setActive(true);
        defaultCriteriaDTO.setOverviewWeight(10);
        defaultCriteriaDTO.setReferencesWeight(5);
        defaultCriteriaDTO.setDefinitionsWeight(5);
        defaultCriteriaDTO.setOrganizationWeight(15);
        defaultCriteriaDTO.setManagerialProcessWeight(20);
        defaultCriteriaDTO.setTechnicalProcessWeight(20);
        defaultCriteriaDTO.setSupportingProcessWeight(15);
        defaultCriteriaDTO.setAdditionalPlansWeight(10);
    }

    @Nested
    @DisplayName("POST /api/grading-criteria - Create Grading Criteria")
    class CreateGradingCriteriaTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should create grading criteria successfully as professor")
        void createGradingCriteria_Success() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.createGradingCriteria(any(GradingCriteriaDTO.class), eq(1L)))
                    .thenReturn(testCriteriaDTO);

            mockMvc.perform(post("/api/grading-criteria")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCriteriaDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Custom Criteria"))
                    .andExpect(jsonPath("$.overviewWeight").value(10));

            verify(gradingCriteriaService).createGradingCriteria(any(GradingCriteriaDTO.class), eq(1L));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should fail creation with invalid data")
        void createGradingCriteria_InvalidData() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.createGradingCriteria(any(GradingCriteriaDTO.class), eq(1L)))
                    .thenThrow(new IllegalArgumentException("Invalid criteria data"));

            mockMvc.perform(post("/api/grading-criteria")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCriteriaDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid criteria data"));
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Should forbid student from creating grading criteria")
        void createGradingCriteria_ForbiddenForStudent() throws Exception {
            mockMvc.perform(post("/api/grading-criteria")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCriteriaDTO)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should require authentication for creation")
        void createGradingCriteria_RequiresAuth() throws Exception {
            mockMvc.perform(post("/api/grading-criteria")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCriteriaDTO)))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("PUT /api/grading-criteria/{id} - Update Grading Criteria")
    class UpdateGradingCriteriaTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should update grading criteria successfully")
        void updateGradingCriteria_Success() throws Exception {
            testCriteriaDTO.setOverviewWeight(15);

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.updateGradingCriteria(eq(1L), any(GradingCriteriaDTO.class), eq(1L)))
                    .thenReturn(testCriteriaDTO);

            mockMvc.perform(put("/api/grading-criteria/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCriteriaDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.overviewWeight").value(15));

            verify(gradingCriteriaService).updateGradingCriteria(eq(1L), any(GradingCriteriaDTO.class), eq(1L));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should fail update when criteria not found")
        void updateGradingCriteria_NotFound() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.updateGradingCriteria(eq(999L), any(GradingCriteriaDTO.class), eq(1L)))
                    .thenThrow(new IllegalArgumentException("Grading criteria not found"));

            mockMvc.perform(put("/api/grading-criteria/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCriteriaDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Grading criteria not found"));
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Should forbid student from updating grading criteria")
        void updateGradingCriteria_ForbiddenForStudent() throws Exception {
            mockMvc.perform(put("/api/grading-criteria/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCriteriaDTO)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /api/grading-criteria/{id} - Delete Grading Criteria")
    class DeleteGradingCriteriaTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should delete grading criteria successfully")
        void deleteGradingCriteria_Success() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            doNothing().when(gradingCriteriaService).deleteGradingCriteria(1L, 1L);

            mockMvc.perform(delete("/api/grading-criteria/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Grading criteria deleted successfully"));

            verify(gradingCriteriaService).deleteGradingCriteria(1L, 1L);
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should fail deletion when criteria not found")
        void deleteGradingCriteria_NotFound() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            doThrow(new IllegalArgumentException("Grading criteria not found"))
                    .when(gradingCriteriaService).deleteGradingCriteria(999L, 1L);

            mockMvc.perform(delete("/api/grading-criteria/999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Grading criteria not found"));
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Should forbid student from deleting grading criteria")
        void deleteGradingCriteria_ForbiddenForStudent() throws Exception {
            mockMvc.perform(delete("/api/grading-criteria/1"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/grading-criteria - Get My Grading Criteria")
    class GetMyGradingCriteriaTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should get professor's grading criteria successfully")
        void getMyGradingCriteria_Success() throws Exception {
            List<GradingCriteriaDTO> criteriaList = List.of(testCriteriaDTO);

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.getGradingCriteriaByProfessor(1L)).thenReturn(criteriaList);

            mockMvc.perform(get("/api/grading-criteria"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("Custom Criteria"));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should return empty list when no criteria exist")
        void getMyGradingCriteria_EmptyList() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.getGradingCriteriaByProfessor(1L)).thenReturn(List.of());

            mockMvc.perform(get("/api/grading-criteria"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Should forbid student from getting all grading criteria")
        void getMyGradingCriteria_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/grading-criteria"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/grading-criteria/active - Get Active Grading Criteria")
    class GetActiveGradingCriteriaTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should get active grading criteria as professor")
        void getActiveGradingCriteria_AsProfessor() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.getActiveCriteriaByProfessor(1L)).thenReturn(testCriteriaDTO);

            mockMvc.perform(get("/api/grading-criteria/active"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Should get active grading criteria as student")
        void getActiveGradingCriteria_AsStudent() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(gradingCriteriaService.getActiveCriteriaByProfessor(2L)).thenReturn(defaultCriteriaDTO);

            mockMvc.perform(get("/api/grading-criteria/active"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("IEEE 1058 Default"));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should handle exception when retrieving active criteria")
        void getActiveGradingCriteria_Exception() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.getActiveCriteriaByProfessor(1L))
                    .thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/grading-criteria/active"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message")
                            .value(org.hamcrest.Matchers.containsString("Failed to retrieve active grading criteria")));
        }
    }

    @Nested
    @DisplayName("GET /api/grading-criteria/{id} - Get Grading Criteria By ID")
    class GetGradingCriteriaByIdTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should get grading criteria by ID successfully")
        void getGradingCriteriaById_Success() throws Exception {
            when(gradingCriteriaService.getGradingCriteriaById(1L)).thenReturn(testCriteriaDTO);

            mockMvc.perform(get("/api/grading-criteria/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Custom Criteria"));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should return 404 when criteria not found")
        void getGradingCriteriaById_NotFound() throws Exception {
            when(gradingCriteriaService.getGradingCriteriaById(999L))
                    .thenThrow(new IllegalArgumentException("Grading criteria not found"));

            mockMvc.perform(get("/api/grading-criteria/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Grading criteria not found"));
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Should forbid student from getting criteria by ID")
        void getGradingCriteriaById_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/grading-criteria/1"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /api/grading-criteria/{id}/activate - Set Active Grading Criteria")
    class SetActiveGradingCriteriaTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should activate grading criteria successfully")
        void setActiveGradingCriteria_Success() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.setActiveGradingCriteria(1L, 1L)).thenReturn(testCriteriaDTO);

            mockMvc.perform(put("/api/grading-criteria/1/activate"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.active").value(true));

            verify(gradingCriteriaService).setActiveGradingCriteria(1L, 1L);
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should fail activation when criteria not found")
        void setActiveGradingCriteria_NotFound() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.setActiveGradingCriteria(999L, 1L))
                    .thenThrow(new IllegalArgumentException("Grading criteria not found"));

            mockMvc.perform(put("/api/grading-criteria/999/activate"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Grading criteria not found"));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should fail activation when criteria belongs to another professor")
        void setActiveGradingCriteria_NotOwner() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(gradingCriteriaService.setActiveGradingCriteria(2L, 1L))
                    .thenThrow(new IllegalArgumentException("You can only activate your own grading criteria"));

            mockMvc.perform(put("/api/grading-criteria/2/activate"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("You can only activate your own grading criteria"));
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Should forbid student from activating grading criteria")
        void setActiveGradingCriteria_ForbiddenForStudent() throws Exception {
            mockMvc.perform(put("/api/grading-criteria/1/activate"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/grading-criteria/default - Get Default Grading Criteria")
    class GetDefaultGradingCriteriaTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should get default grading criteria as professor")
        void getDefaultGradingCriteria_AsProfessor() throws Exception {
            when(gradingCriteriaService.getDefaultCriteria()).thenReturn(defaultCriteriaDTO);

            mockMvc.perform(get("/api/grading-criteria/default"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("IEEE 1058 Default"))
                    .andExpect(jsonPath("$.overviewWeight").value(10))
                    .andExpect(jsonPath("$.referencesWeight").value(5))
                    .andExpect(jsonPath("$.definitionsWeight").value(5))
                    .andExpect(jsonPath("$.organizationWeight").value(15))
                    .andExpect(jsonPath("$.managerialProcessWeight").value(20))
                    .andExpect(jsonPath("$.technicalProcessWeight").value(20))
                    .andExpect(jsonPath("$.supportingProcessWeight").value(15))
                    .andExpect(jsonPath("$.additionalPlansWeight").value(10));
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Should get default grading criteria as student")
        void getDefaultGradingCriteria_AsStudent() throws Exception {
            when(gradingCriteriaService.getDefaultCriteria()).thenReturn(defaultCriteriaDTO);

            mockMvc.perform(get("/api/grading-criteria/default"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("IEEE 1058 Default"));
        }

        @Test
        @DisplayName("Should get default grading criteria without authentication")
        void getDefaultGradingCriteria_NoAuth() throws Exception {
            when(gradingCriteriaService.getDefaultCriteria()).thenReturn(defaultCriteriaDTO);

            // This endpoint might be public - test to see behavior
            mockMvc.perform(get("/api/grading-criteria/default"))
                    .andExpect(status().is3xxRedirection()); // Redirects to login if not public
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should handle exception when retrieving default criteria")
        void getDefaultGradingCriteria_Exception() throws Exception {
            when(gradingCriteriaService.getDefaultCriteria())
                    .thenThrow(new RuntimeException("Service error"));

            mockMvc.perform(get("/api/grading-criteria/default"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("Failed to retrieve default grading criteria")));
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should reject criteria with blank name")
        void createGradingCriteria_BlankName() throws Exception {
            GradingCriteriaDTO invalidDTO = new GradingCriteriaDTO();
            invalidDTO.setName(""); // Blank name
            invalidDTO.setOverviewWeight(10);

            mockMvc.perform(post("/api/grading-criteria")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should reject criteria with weight exceeding 100")
        void createGradingCriteria_WeightExceeds100() throws Exception {
            GradingCriteriaDTO invalidDTO = new GradingCriteriaDTO();
            invalidDTO.setName("Invalid Criteria");
            invalidDTO.setOverviewWeight(150); // Exceeds max

            mockMvc.perform(post("/api/grading-criteria")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should reject criteria with negative weight")
        void createGradingCriteria_NegativeWeight() throws Exception {
            GradingCriteriaDTO invalidDTO = new GradingCriteriaDTO();
            invalidDTO.setName("Invalid Criteria");
            invalidDTO.setOverviewWeight(-5); // Negative

            mockMvc.perform(post("/api/grading-criteria")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());
        }
    }
}
