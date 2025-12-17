package com.team02.spmpevaluator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.StudentProfessorAssignment;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.AssignmentService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AssignmentController.
 * UC 2.12: Admin Assign Students to Professors
 */
@SpringBootTest
@AutoConfigureMockMvc
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssignmentService assignmentService;

    @MockBean
    private UserService userService;

    private User student;
    private User professor;
    private User admin;
    private StudentProfessorAssignment assignment;

    @BeforeEach
    void setUp() {
        // Create test student
        student = new User();
        student.setId(1L);
        student.setUsername("student1");
        student.setEmail("student1@example.com");
        student.setFirstName("John");
        student.setLastName("Student");
        student.setRole(Role.STUDENT);
        student.setEnabled(true);

        // Create test professor
        professor = new User();
        professor.setId(2L);
        professor.setUsername("professor1");
        professor.setEmail("professor1@example.com");
        professor.setFirstName("Jane");
        professor.setLastName("Professor");
        professor.setRole(Role.PROFESSOR);
        professor.setEnabled(true);

        // Create test admin
        admin = new User();
        admin.setId(3L);
        admin.setUsername("admin1");
        admin.setEmail("admin1@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);

        // Create test assignment
        assignment = new StudentProfessorAssignment();
        assignment.setId(1L);
        assignment.setStudent(student);
        assignment.setProfessor(professor);
        assignment.setAssignedBy(admin);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setNotes("Test assignment notes");
    }

    @Nested
    @DisplayName("GET /api/admin/assignments")
    class GetAllAssignments {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return all assignments successfully")
        void getAllAssignments_Success() throws Exception {
            List<StudentProfessorAssignment> assignments = Arrays.asList(assignment);
            when(assignmentService.getAllAssignments()).thenReturn(assignments);

            mockMvc.perform(get("/api/admin/assignments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].studentId").value(1))
                    .andExpect(jsonPath("$[0].studentName").value("John Student"))
                    .andExpect(jsonPath("$[0].professorId").value(2))
                    .andExpect(jsonPath("$[0].professorName").value("Jane Professor"));

            verify(assignmentService).getAllAssignments();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return empty list when no assignments exist")
        void getAllAssignments_EmptyList() throws Exception {
            when(assignmentService.getAllAssignments()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/assignments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(assignmentService).getAllAssignments();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void getAllAssignments_ServiceException() throws Exception {
            when(assignmentService.getAllAssignments()).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/admin/assignments"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to retrieve assignments")));

            verify(assignmentService).getAllAssignments();
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getAllAssignments_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/assignments"))
                    .andExpect(status().isForbidden());

            verify(assignmentService, never()).getAllAssignments();
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getAllAssignments_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/assignments"))
                    .andExpect(status().isForbidden());

            verify(assignmentService, never()).getAllAssignments();
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getAllAssignments_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/assignments"))
                    .andExpect(status().is3xxRedirection());

            verify(assignmentService, never()).getAllAssignments();
        }
    }

    @Nested
    @DisplayName("POST /api/admin/assignments")
    class CreateAssignment {

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should create assignment successfully")
        void createAssignment_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 1L);
            request.put("professorId", 2L);
            request.put("notes", "Test notes");

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(assignmentService.assignStudentToProfessor(eq(1L), eq(2L), eq(3L), eq("Test notes")))
                    .thenReturn(assignment);

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.studentId").value(1))
                    .andExpect(jsonPath("$.professorId").value(2));

            verify(userService).findByUsername("admin1");
            verify(assignmentService).assignStudentToProfessor(eq(1L), eq(2L), eq(3L), eq("Test notes"));
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should create assignment without notes")
        void createAssignment_WithoutNotes() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 1L);
            request.put("professorId", 2L);

            assignment.setNotes("");
            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(assignmentService.assignStudentToProfessor(eq(1L), eq(2L), eq(3L), eq("")))
                    .thenReturn(assignment);

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            verify(assignmentService).assignStudentToProfessor(eq(1L), eq(2L), eq(3L), eq(""));
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 400 when admin not found")
        void createAssignment_AdminNotFound() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 1L);
            request.put("professorId", 2L);
            request.put("notes", "Test notes");

            when(userService.findByUsername("admin1")).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to create assignment")));

            verify(userService).findByUsername("admin1");
            verify(assignmentService, never()).assignStudentToProfessor(anyLong(), anyLong(), anyLong(), anyString());
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 400 when student not found")
        void createAssignment_StudentNotFound() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 999L);
            request.put("professorId", 2L);
            request.put("notes", "Test notes");

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(assignmentService.assignStudentToProfessor(eq(999L), eq(2L), eq(3L), anyString()))
                    .thenThrow(new IllegalArgumentException("Student not found"));

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to create assignment")));
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 400 when professor not found")
        void createAssignment_ProfessorNotFound() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 1L);
            request.put("professorId", 999L);
            request.put("notes", "Test notes");

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(assignmentService.assignStudentToProfessor(eq(1L), eq(999L), eq(3L), anyString()))
                    .thenThrow(new IllegalArgumentException("Professor not found"));

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to create assignment")));
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 400 when assignment already exists")
        void createAssignment_DuplicateAssignment() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 1L);
            request.put("professorId", 2L);
            request.put("notes", "Test notes");

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(assignmentService.assignStudentToProfessor(eq(1L), eq(2L), eq(3L), anyString()))
                    .thenThrow(new IllegalArgumentException("Student is already assigned to this professor"));

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to create assignment")));
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 400 when user is not a student")
        void createAssignment_UserNotStudent() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 1L);
            request.put("professorId", 2L);
            request.put("notes", "Test notes");

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(assignmentService.assignStudentToProfessor(eq(1L), eq(2L), eq(3L), anyString()))
                    .thenThrow(new IllegalArgumentException("User is not a student"));

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to create assignment")));
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 400 when user is not a professor")
        void createAssignment_UserNotProfessor() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 1L);
            request.put("professorId", 2L);
            request.put("notes", "Test notes");

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(assignmentService.assignStudentToProfessor(eq(1L), eq(2L), eq(3L), anyString()))
                    .thenThrow(new IllegalArgumentException("User is not a professor"));

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to create assignment")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void createAssignment_ForbiddenForProfessor() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 1L);
            request.put("professorId", 2L);

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(assignmentService, never()).assignStudentToProfessor(anyLong(), anyLong(), anyLong(), anyString());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void createAssignment_ForbiddenForStudent() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 1L);
            request.put("professorId", 2L);

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(assignmentService, never()).assignStudentToProfessor(anyLong(), anyLong(), anyLong(), anyString());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void createAssignment_Unauthenticated() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("studentId", 1L);
            request.put("professorId", 2L);

            mockMvc.perform(post("/api/admin/assignments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is3xxRedirection());

            verify(assignmentService, never()).assignStudentToProfessor(anyLong(), anyLong(), anyLong(), anyString());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/assignments/{id}")
    class DeleteAssignment {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete assignment successfully")
        void deleteAssignment_Success() throws Exception {
            doNothing().when(assignmentService).removeAssignment(1L);

            mockMvc.perform(delete("/api/admin/assignments/1")
                    .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(assignmentService).removeAssignment(1L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void deleteAssignment_ServiceException() throws Exception {
            doThrow(new RuntimeException("Database error")).when(assignmentService).removeAssignment(1L);

            mockMvc.perform(delete("/api/admin/assignments/1")
                    .with(csrf()))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to delete assignment")));

            verify(assignmentService).removeAssignment(1L);
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void deleteAssignment_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(delete("/api/admin/assignments/1")
                    .with(csrf()))
                    .andExpect(status().isForbidden());

            verify(assignmentService, never()).removeAssignment(anyLong());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void deleteAssignment_ForbiddenForStudent() throws Exception {
            mockMvc.perform(delete("/api/admin/assignments/1")
                    .with(csrf()))
                    .andExpect(status().isForbidden());

            verify(assignmentService, never()).removeAssignment(anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void deleteAssignment_Unauthenticated() throws Exception {
            mockMvc.perform(delete("/api/admin/assignments/1")
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection());

            verify(assignmentService, never()).removeAssignment(anyLong());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/assignments/professor/{professorId}")
    class GetProfessorStudents {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return professor's students successfully")
        void getProfessorStudents_Success() throws Exception {
            List<User> students = Arrays.asList(student);
            when(assignmentService.getStudentsByProfessor(2L)).thenReturn(students);

            mockMvc.perform(get("/api/admin/assignments/professor/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].username").value("student1"));

            verify(assignmentService).getStudentsByProfessor(2L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return empty list when professor has no students")
        void getProfessorStudents_EmptyList() throws Exception {
            when(assignmentService.getStudentsByProfessor(2L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/assignments/professor/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(assignmentService).getStudentsByProfessor(2L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return multiple students")
        void getProfessorStudents_MultipleStudents() throws Exception {
            User student2 = new User();
            student2.setId(4L);
            student2.setUsername("student2");
            student2.setFirstName("Second");
            student2.setLastName("Student");
            student2.setRole(Role.STUDENT);

            List<User> students = Arrays.asList(student, student2);
            when(assignmentService.getStudentsByProfessor(2L)).thenReturn(students);

            mockMvc.perform(get("/api/admin/assignments/professor/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));

            verify(assignmentService).getStudentsByProfessor(2L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void getProfessorStudents_ServiceException() throws Exception {
            when(assignmentService.getStudentsByProfessor(2L)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/admin/assignments/professor/2"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to retrieve professor's students")));

            verify(assignmentService).getStudentsByProfessor(2L);
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getProfessorStudents_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/assignments/professor/2"))
                    .andExpect(status().isForbidden());

            verify(assignmentService, never()).getStudentsByProfessor(anyLong());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getProfessorStudents_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/assignments/professor/2"))
                    .andExpect(status().isForbidden());

            verify(assignmentService, never()).getStudentsByProfessor(anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getProfessorStudents_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/assignments/professor/2"))
                    .andExpect(status().is3xxRedirection());

            verify(assignmentService, never()).getStudentsByProfessor(anyLong());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/assignments/student/{studentId}")
    class GetStudentProfessor {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return student's professor successfully")
        void getStudentProfessor_Success() throws Exception {
            when(assignmentService.getProfessorByStudent(1L)).thenReturn(professor);

            mockMvc.perform(get("/api/admin/assignments/student/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2))
                    .andExpect(jsonPath("$.username").value("professor1"))
                    .andExpect(jsonPath("$.firstName").value("Jane"))
                    .andExpect(jsonPath("$.lastName").value("Professor"));

            verify(assignmentService).getProfessorByStudent(1L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return message when student has no professor assigned")
        void getStudentProfessor_NoProfessor() throws Exception {
            when(assignmentService.getProfessorByStudent(1L)).thenReturn(null);

            mockMvc.perform(get("/api/admin/assignments/student/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("No professor assigned"));

            verify(assignmentService).getProfessorByStudent(1L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void getStudentProfessor_ServiceException() throws Exception {
            when(assignmentService.getProfessorByStudent(1L)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/admin/assignments/student/1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to retrieve student's professor")));

            verify(assignmentService).getProfessorByStudent(1L);
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getStudentProfessor_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/assignments/student/1"))
                    .andExpect(status().isForbidden());

            verify(assignmentService, never()).getProfessorByStudent(anyLong());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getStudentProfessor_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/assignments/student/1"))
                    .andExpect(status().isForbidden());

            verify(assignmentService, never()).getProfessorByStudent(anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getStudentProfessor_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/assignments/student/1"))
                    .andExpect(status().is3xxRedirection());

            verify(assignmentService, never()).getProfessorByStudent(anyLong());
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control Tests")
    class RoleBasedAccessTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN should have full access to all endpoints")
        void adminHasFullAccess() throws Exception {
            when(assignmentService.getAllAssignments()).thenReturn(Collections.emptyList());
            when(assignmentService.getStudentsByProfessor(anyLong())).thenReturn(Collections.emptyList());
            when(assignmentService.getProfessorByStudent(anyLong())).thenReturn(null);

            // GET all assignments
            mockMvc.perform(get("/api/admin/assignments"))
                    .andExpect(status().isOk());

            // GET professor's students
            mockMvc.perform(get("/api/admin/assignments/professor/1"))
                    .andExpect(status().isOk());

            // GET student's professor
            mockMvc.perform(get("/api/admin/assignments/student/1"))
                    .andExpect(status().isOk());

            // DELETE assignment
            doNothing().when(assignmentService).removeAssignment(anyLong());
            mockMvc.perform(delete("/api/admin/assignments/1")
                    .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("PROFESSOR should be denied access to all admin endpoints")
        void professorDeniedAccess() throws Exception {
            mockMvc.perform(get("/api/admin/assignments"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/assignments/professor/1"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/assignments/student/1"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(delete("/api/admin/assignments/1")
                    .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("STUDENT should be denied access to all admin endpoints")
        void studentDeniedAccess() throws Exception {
            mockMvc.perform(get("/api/admin/assignments"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/assignments/professor/1"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/assignments/student/1"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(delete("/api/admin/assignments/1")
                    .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }
}
