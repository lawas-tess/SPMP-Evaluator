package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.dto.UserDTO;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@DisplayName("UserController Integration Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User adminUser;
    private User professorUser;
    private User studentUser;
    private UserDTO adminDTO;
    private UserDTO professorDTO;
    private UserDTO studentDTO;

    @BeforeEach
    void setUp() {
        // Setup admin user
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setRole(Role.ADMIN);
        adminUser.setEnabled(true);

        // Setup professor user
        professorUser = new User();
        professorUser.setId(2L);
        professorUser.setUsername("professor");
        professorUser.setEmail("professor@test.com");
        professorUser.setFirstName("John");
        professorUser.setLastName("Doe");
        professorUser.setRole(Role.PROFESSOR);
        professorUser.setEnabled(true);

        // Setup student user
        studentUser = new User();
        studentUser.setId(3L);
        studentUser.setUsername("student");
        studentUser.setEmail("student@test.com");
        studentUser.setFirstName("Jane");
        studentUser.setLastName("Smith");
        studentUser.setRole(Role.STUDENT);
        studentUser.setEnabled(true);

        // Setup DTOs
        adminDTO = new UserDTO(1L, "admin", "admin@test.com", "Admin", "User", Role.ADMIN, true);
        professorDTO = new UserDTO(2L, "professor", "professor@test.com", "John", "Doe", Role.PROFESSOR, true);
        studentDTO = new UserDTO(3L, "student", "student@test.com", "Jane", "Smith", Role.STUDENT, true);
    }

    @Nested
    @DisplayName("GET /api/users/students - Get All Students")
    class GetAllStudentsTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Professor should get all students")
        void getAllStudents_AsProfessor() throws Exception {
            when(userService.getUsersByRole(Role.STUDENT)).thenReturn(List.of(studentDTO));

            mockMvc.perform(get("/api/users/students"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].username").value("student"))
                    .andExpect(jsonPath("$[0].role").value("STUDENT"));

            verify(userService).getUsersByRole(Role.STUDENT);
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Admin should get all students")
        void getAllStudents_AsAdmin() throws Exception {
            when(userService.getUsersByRole(Role.STUDENT)).thenReturn(List.of(studentDTO));

            mockMvc.perform(get("/api/users/students"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].username").value("student"));

            verify(userService).getUsersByRole(Role.STUDENT);
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Student should not access student list")
        void getAllStudents_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/users/students"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).getUsersByRole(any());
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should return empty list when no students")
        void getAllStudents_EmptyList() throws Exception {
            when(userService.getUsersByRole(Role.STUDENT)).thenReturn(List.of());

            mockMvc.perform(get("/api/users/students"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should handle service exception")
        void getAllStudents_Exception() throws Exception {
            when(userService.getUsersByRole(Role.STUDENT))
                    .thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/users/students"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(org.hamcrest.Matchers.containsString("Failed to retrieve students")));
        }

        @Test
        @DisplayName("Should require authentication")
        void getAllStudents_RequiresAuth() throws Exception {
            mockMvc.perform(get("/api/users/students"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/users/professors - Get All Professors")
    class GetAllProfessorsTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Admin should get all professors")
        void getAllProfessors_AsAdmin() throws Exception {
            when(userService.getUsersByRole(Role.PROFESSOR)).thenReturn(List.of(professorDTO));

            mockMvc.perform(get("/api/users/professors"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].username").value("professor"))
                    .andExpect(jsonPath("$[0].role").value("PROFESSOR"));

            verify(userService).getUsersByRole(Role.PROFESSOR);
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Professor should not access professor list")
        void getAllProfessors_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/users/professors"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).getUsersByRole(any());
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Student should not access professor list")
        void getAllProfessors_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/users/professors"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should return empty list when no professors")
        void getAllProfessors_EmptyList() throws Exception {
            when(userService.getUsersByRole(Role.PROFESSOR)).thenReturn(List.of());

            mockMvc.perform(get("/api/users/professors"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should handle service exception")
        void getAllProfessors_Exception() throws Exception {
            when(userService.getUsersByRole(Role.PROFESSOR))
                    .thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/users/professors"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(org.hamcrest.Matchers.containsString("Failed to retrieve professors")));
        }
    }

    @Nested
    @DisplayName("GET /api/users - Get All Users")
    class GetAllUsersTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Admin should get all users")
        void getAllUsers_AsAdmin() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(adminDTO, professorDTO, studentDTO));

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].username").value("admin"))
                    .andExpect(jsonPath("$[1].username").value("professor"))
                    .andExpect(jsonPath("$[2].username").value("student"));

            verify(userService).getAllUsers();
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Professor should not access all users")
        void getAllUsers_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).getAllUsers();
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Student should not access all users")
        void getAllUsers_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should return empty list when no users")
        void getAllUsers_EmptyList() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of());

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should handle service exception")
        void getAllUsers_Exception() throws Exception {
            when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(org.hamcrest.Matchers.containsString("Failed to retrieve users")));
        }

        @Test
        @DisplayName("Should require authentication")
        void getAllUsers_RequiresAuth() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id} - Get User By ID")
    class GetUserByIdTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Admin should get any user by ID")
        void getUserById_AsAdmin() throws Exception {
            when(userService.findById(3L)).thenReturn(Optional.of(studentUser));
            when(userService.convertToDTO(studentUser)).thenReturn(studentDTO);

            mockMvc.perform(get("/api/users/3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(3))
                    .andExpect(jsonPath("$.username").value("student"));

            verify(userService).findById(3L);
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Professor should get user by ID")
        void getUserById_AsProfessor() throws Exception {
            when(userService.findById(3L)).thenReturn(Optional.of(studentUser));
            when(userService.convertToDTO(studentUser)).thenReturn(studentDTO);

            mockMvc.perform(get("/api/users/3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("student"));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should return 404 for non-existent user")
        void getUserById_NotFound() throws Exception {
            when(userService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/users/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should handle service exception")
        void getUserById_Exception() throws Exception {
            when(userService.findById(3L)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/users/3"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(org.hamcrest.Matchers.containsString("Failed to retrieve user")));
        }

        @Test
        @DisplayName("Should require authentication")
        void getUserById_RequiresAuth() throws Exception {
            mockMvc.perform(get("/api/users/3"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}/status - Toggle User Status")
    class ToggleUserStatusTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Admin should enable user")
        void toggleUserStatus_Enable() throws Exception {
            doNothing().when(userService).toggleUserStatus(3L, true);

            mockMvc.perform(put("/api/users/3/status")
                    .param("enabled", "true"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("User status updated successfully"));

            verify(userService).toggleUserStatus(3L, true);
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Admin should disable user")
        void toggleUserStatus_Disable() throws Exception {
            doNothing().when(userService).toggleUserStatus(3L, false);

            mockMvc.perform(put("/api/users/3/status")
                    .param("enabled", "false"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("User status updated successfully"));

            verify(userService).toggleUserStatus(3L, false);
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Professor should not toggle user status")
        void toggleUserStatus_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(put("/api/users/3/status")
                    .param("enabled", "true"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).toggleUserStatus(anyLong(), anyBoolean());
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Student should not toggle user status")
        void toggleUserStatus_ForbiddenForStudent() throws Exception {
            mockMvc.perform(put("/api/users/3/status")
                    .param("enabled", "true"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should fail for non-existent user")
        void toggleUserStatus_UserNotFound() throws Exception {
            doThrow(new IllegalArgumentException("User not found"))
                    .when(userService).toggleUserStatus(999L, true);

            mockMvc.perform(put("/api/users/999/status")
                    .param("enabled", "true"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should handle service exception")
        void toggleUserStatus_Exception() throws Exception {
            doThrow(new RuntimeException("Database error"))
                    .when(userService).toggleUserStatus(3L, true);

            mockMvc.perform(put("/api/users/3/status")
                    .param("enabled", "true"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(org.hamcrest.Matchers.containsString("Failed to update user status")));
        }

        @Test
        @DisplayName("Should require authentication")
        void toggleUserStatus_RequiresAuth() throws Exception {
            mockMvc.perform(put("/api/users/3/status")
                    .param("enabled", "true"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control Tests")
    class RoleBasedAccessTests {

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Professor can access students endpoint")
        void professor_CanAccessStudents() throws Exception {
            when(userService.getUsersByRole(Role.STUDENT)).thenReturn(List.of(studentDTO));

            mockMvc.perform(get("/api/users/students"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Professor cannot access professors endpoint")
        void professor_CannotAccessProfessors() throws Exception {
            mockMvc.perform(get("/api/users/professors"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Professor cannot access all users endpoint")
        void professor_CannotAccessAllUsers() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Professor cannot toggle user status")
        void professor_CannotToggleStatus() throws Exception {
            mockMvc.perform(put("/api/users/3/status")
                    .param("enabled", "true"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Admin can access all endpoints")
        void admin_CanAccessAllEndpoints() throws Exception {
            when(userService.getUsersByRole(Role.STUDENT)).thenReturn(List.of());
            when(userService.getUsersByRole(Role.PROFESSOR)).thenReturn(List.of());
            when(userService.getAllUsers()).thenReturn(List.of());
            doNothing().when(userService).toggleUserStatus(anyLong(), anyBoolean());

            mockMvc.perform(get("/api/users/students")).andExpect(status().isOk());
            mockMvc.perform(get("/api/users/professors")).andExpect(status().isOk());
            mockMvc.perform(get("/api/users")).andExpect(status().isOk());
            mockMvc.perform(put("/api/users/1/status").param("enabled", "true")).andExpect(status().isOk());
        }
    }
}
