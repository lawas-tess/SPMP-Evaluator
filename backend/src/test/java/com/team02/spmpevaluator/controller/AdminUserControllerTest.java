package com.team02.spmpevaluator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AdminUserController Integration Tests")
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("GET /api/admin/users - Get All Users")
    class GetAllUsersTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should get all users for admin")
        void getAllUsers_Success() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of(adminDTO, professorDTO, studentDTO));

            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].username").value("admin"))
                    .andExpect(jsonPath("$[1].username").value("professor"))
                    .andExpect(jsonPath("$[2].username").value("student"));

            verify(userService).getAllUsers();
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should filter users by role")
        void getAllUsers_FilterByRole() throws Exception {
            when(userService.getUsersByRole(Role.STUDENT)).thenReturn(List.of(studentDTO));

            mockMvc.perform(get("/api/admin/users")
                    .param("role", "STUDENT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].username").value("student"))
                    .andExpect(jsonPath("$[0].role").value("STUDENT"));

            verify(userService).getUsersByRole(Role.STUDENT);
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should return empty list when no users")
        void getAllUsers_Empty() throws Exception {
            when(userService.getAllUsers()).thenReturn(List.of());

            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should deny access to professors")
        void getAllUsers_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Should deny access to students")
        void getAllUsers_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should require authentication")
        void getAllUsers_RequiresAuth() throws Exception {
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should handle service exception")
        void getAllUsers_Exception() throws Exception {
            when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to retrieve users")));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/users/{id} - Get User By ID")
    class GetUserByIdTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should get user by ID")
        void getUserById_Success() throws Exception {
            when(userService.getUserById(2L)).thenReturn(professorUser);
            when(userService.convertToDTO(professorUser)).thenReturn(professorDTO);

            mockMvc.perform(get("/api/admin/users/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.username").value("professor"))
                    .andExpect(jsonPath("$.email").value("professor@test.com"));

            verify(userService).getUserById(2L);
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should return 404 for non-existent user")
        void getUserById_NotFound() throws Exception {
            when(userService.getUserById(999L)).thenThrow(new RuntimeException("User not found"));

            mockMvc.perform(get("/api/admin/users/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("User not found")));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should deny access to non-admin")
        void getUserById_ForbiddenForNonAdmin() throws Exception {
            mockMvc.perform(get("/api/admin/users/2"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /api/admin/users - Create User")
    class CreateUserTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should create new user successfully")
        void createUser_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("username", "newuser");
            request.put("email", "newuser@test.com");
            request.put("password", "password123");
            request.put("firstName", "New");
            request.put("lastName", "User");
            request.put("role", "STUDENT");

            User newUser = new User();
            newUser.setId(4L);
            newUser.setUsername("newuser");
            newUser.setEmail("newuser@test.com");
            newUser.setFirstName("New");
            newUser.setLastName("User");
            newUser.setRole(Role.STUDENT);

            UserDTO newUserDTO = new UserDTO(4L, "newuser", "newuser@test.com", "New", "User", Role.STUDENT, true);

            when(userService.registerUser("newuser", "newuser@test.com", "password123", "New", "User", Role.STUDENT))
                    .thenReturn(newUser);
            when(userService.convertToDTO(newUser)).thenReturn(newUserDTO);

            mockMvc.perform(post("/api/admin/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(4))
                    .andExpect(jsonPath("$.username").value("newuser"))
                    .andExpect(jsonPath("$.role").value("STUDENT"));

            verify(userService).registerUser("newuser", "newuser@test.com", "password123", "New", "User", Role.STUDENT);
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should create professor user")
        void createUser_Professor() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("username", "newprof");
            request.put("email", "newprof@test.com");
            request.put("password", "password123");
            request.put("firstName", "New");
            request.put("lastName", "Professor");
            request.put("role", "PROFESSOR");

            User newUser = new User();
            newUser.setId(5L);
            newUser.setRole(Role.PROFESSOR);

            UserDTO newUserDTO = new UserDTO(5L, "newprof", "newprof@test.com", "New", "Professor", Role.PROFESSOR,
                    true);

            when(userService.registerUser(anyString(), anyString(), anyString(), anyString(), anyString(),
                    eq(Role.PROFESSOR)))
                    .thenReturn(newUser);
            when(userService.convertToDTO(newUser)).thenReturn(newUserDTO);

            mockMvc.perform(post("/api/admin/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.role").value("PROFESSOR"));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should fail with invalid role")
        void createUser_InvalidRole() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("username", "newuser");
            request.put("email", "newuser@test.com");
            request.put("password", "password123");
            request.put("firstName", "New");
            request.put("lastName", "User");
            request.put("role", "INVALID_ROLE");

            mockMvc.perform(post("/api/admin/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to create user")));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should fail with duplicate username")
        void createUser_DuplicateUsername() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("username", "admin");
            request.put("email", "newemail@test.com");
            request.put("password", "password123");
            request.put("firstName", "Duplicate");
            request.put("lastName", "User");
            request.put("role", "STUDENT");

            when(userService.registerUser(eq("admin"), anyString(), anyString(), anyString(), anyString(),
                    any(Role.class)))
                    .thenThrow(new RuntimeException("Username already exists"));

            mockMvc.perform(post("/api/admin/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to create user")));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should deny access to non-admin")
        void createUser_ForbiddenForNonAdmin() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("username", "newuser");
            request.put("email", "newuser@test.com");
            request.put("password", "password123");
            request.put("firstName", "New");
            request.put("lastName", "User");
            request.put("role", "STUDENT");

            mockMvc.perform(post("/api/admin/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/users/{id} - Update User")
    class UpdateUserTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should update user successfully")
        void updateUser_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("firstName", "Updated");
            request.put("lastName", "Name");
            request.put("email", "updated@test.com");

            User updatedUser = new User();
            updatedUser.setId(2L);
            updatedUser.setUsername("professor");
            updatedUser.setFirstName("Updated");
            updatedUser.setLastName("Name");
            updatedUser.setEmail("updated@test.com");
            updatedUser.setRole(Role.PROFESSOR);

            UserDTO updatedDTO = new UserDTO(2L, "professor", "updated@test.com", "Updated", "Name", Role.PROFESSOR,
                    true);

            when(userService.getUserById(2L)).thenReturn(professorUser);
            when(userService.updateUser(any(User.class))).thenReturn(updatedUser);
            when(userService.convertToDTO(updatedUser)).thenReturn(updatedDTO);

            mockMvc.perform(put("/api/admin/users/2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Updated"))
                    .andExpect(jsonPath("$.lastName").value("Name"))
                    .andExpect(jsonPath("$.email").value("updated@test.com"));

            verify(userService).updateUser(any(User.class));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should update user role")
        void updateUser_ChangeRole() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("role", "PROFESSOR");

            User updatedUser = new User();
            updatedUser.setId(3L);
            updatedUser.setRole(Role.PROFESSOR);

            UserDTO updatedDTO = new UserDTO(3L, "student", "student@test.com", "Jane", "Smith", Role.PROFESSOR, true);

            when(userService.getUserById(3L)).thenReturn(studentUser);
            when(userService.updateUser(any(User.class))).thenReturn(updatedUser);
            when(userService.convertToDTO(updatedUser)).thenReturn(updatedDTO);

            mockMvc.perform(put("/api/admin/users/3")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.role").value("PROFESSOR"));
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should fail for non-existent user")
        void updateUser_NotFound() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("firstName", "Updated");

            when(userService.getUserById(999L)).thenThrow(new RuntimeException("User not found"));

            mockMvc.perform(put("/api/admin/users/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to update user")));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should deny access to non-admin")
        void updateUser_ForbiddenForNonAdmin() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("firstName", "Updated");

            mockMvc.perform(put("/api/admin/users/2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/users/{id} - Delete User")
    class DeleteUserTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should delete user successfully")
        void deleteUser_Success() throws Exception {
            doNothing().when(userService).deleteUser(3L);

            mockMvc.perform(delete("/api/admin/users/3"))
                    .andExpect(status().isNoContent());

            verify(userService).deleteUser(3L);
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should fail when user not found")
        void deleteUser_NotFound() throws Exception {
            doThrow(new RuntimeException("User not found")).when(userService).deleteUser(999L);

            mockMvc.perform(delete("/api/admin/users/999"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to delete user")));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should deny access to non-admin")
        void deleteUser_ForbiddenForNonAdmin() throws Exception {
            mockMvc.perform(delete("/api/admin/users/3"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should require authentication")
        void deleteUser_RequiresAuth() throws Exception {
            mockMvc.perform(delete("/api/admin/users/3"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("POST /api/admin/users/{id}/reset-password - Reset Password")
    class ResetPasswordTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should reset password successfully")
        void resetPassword_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("newPassword", "newSecurePassword123");

            doNothing().when(userService).resetPassword(2L, "newSecurePassword123");

            mockMvc.perform(post("/api/admin/users/2/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Password reset successfully"));

            verify(userService).resetPassword(2L, "newSecurePassword123");
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should fail when user not found")
        void resetPassword_UserNotFound() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("newPassword", "newPassword123");

            doThrow(new RuntimeException("User not found")).when(userService).resetPassword(999L, "newPassword123");

            mockMvc.perform(post("/api/admin/users/999/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to reset password")));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should deny access to non-admin")
        void resetPassword_ForbiddenForNonAdmin() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("newPassword", "newPassword123");

            mockMvc.perform(post("/api/admin/users/2/reset-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /api/admin/users/{id}/lock - Lock User")
    class LockUserTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should lock user successfully")
        void lockUser_Success() throws Exception {
            doNothing().when(userService).lockUser(3L);

            mockMvc.perform(post("/api/admin/users/3/lock"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User locked successfully"));

            verify(userService).lockUser(3L);
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should fail when user not found")
        void lockUser_UserNotFound() throws Exception {
            doThrow(new RuntimeException("User not found")).when(userService).lockUser(999L);

            mockMvc.perform(post("/api/admin/users/999/lock"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to lock user")));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should deny access to non-admin")
        void lockUser_ForbiddenForNonAdmin() throws Exception {
            mockMvc.perform(post("/api/admin/users/3/lock"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("Should deny access to students")
        void lockUser_ForbiddenForStudent() throws Exception {
            mockMvc.perform(post("/api/admin/users/3/lock"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("POST /api/admin/users/{id}/unlock - Unlock User")
    class UnlockUserTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should unlock user successfully")
        void unlockUser_Success() throws Exception {
            doNothing().when(userService).unlockUser(3L);

            mockMvc.perform(post("/api/admin/users/3/unlock"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User unlocked successfully"));

            verify(userService).unlockUser(3L);
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Should fail when user not found")
        void unlockUser_UserNotFound() throws Exception {
            doThrow(new RuntimeException("User not found")).when(userService).unlockUser(999L);

            mockMvc.perform(post("/api/admin/users/999/unlock"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to unlock user")));
        }

        @Test
        @WithMockUser(username = "professor", roles = { "PROFESSOR" })
        @DisplayName("Should deny access to non-admin")
        void unlockUser_ForbiddenForNonAdmin() throws Exception {
            mockMvc.perform(post("/api/admin/users/3/unlock"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Security Tests - Class-Level @PreAuthorize")
    class SecurityTests {

        @Test
        @DisplayName("All endpoints should require authentication")
        void allEndpoints_RequireAuth() throws Exception {
            // Test all endpoints without auth
            mockMvc.perform(get("/api/admin/users")).andExpect(status().is3xxRedirection());
            mockMvc.perform(get("/api/admin/users/1")).andExpect(status().is3xxRedirection());
            mockMvc.perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().is3xxRedirection());
            mockMvc.perform(put("/api/admin/users/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().is3xxRedirection());
            mockMvc.perform(delete("/api/admin/users/1")).andExpect(status().is3xxRedirection());
            mockMvc.perform(
                    post("/api/admin/users/1/reset-password").contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().is3xxRedirection());
            mockMvc.perform(post("/api/admin/users/1/lock")).andExpect(status().is3xxRedirection());
            mockMvc.perform(post("/api/admin/users/1/unlock")).andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockUser(username = "student", roles = { "STUDENT" })
        @DisplayName("All endpoints should deny students")
        void allEndpoints_DenyStudents() throws Exception {
            mockMvc.perform(get("/api/admin/users")).andExpect(status().isForbidden());
            mockMvc.perform(get("/api/admin/users/1")).andExpect(status().isForbidden());
            mockMvc.perform(post("/api/admin/users").contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isForbidden());
            mockMvc.perform(put("/api/admin/users/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isForbidden());
            mockMvc.perform(delete("/api/admin/users/1")).andExpect(status().isForbidden());
            mockMvc.perform(
                    post("/api/admin/users/1/reset-password").contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isForbidden());
            mockMvc.perform(post("/api/admin/users/1/lock")).andExpect(status().isForbidden());
            mockMvc.perform(post("/api/admin/users/1/unlock")).andExpect(status().isForbidden());
        }
    }
}
