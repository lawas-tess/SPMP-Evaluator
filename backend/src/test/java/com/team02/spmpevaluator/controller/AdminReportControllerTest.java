package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.dto.UserDTO;
import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.service.ComplianceEvaluationService;
import com.team02.spmpevaluator.service.SPMPDocumentService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AdminReportController.
 * UC 2.14: Admin Reports
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private SPMPDocumentService documentService;

    @MockBean
    private ComplianceEvaluationService evaluationService;

    private List<UserDTO> allUsers;
    private List<UserDTO> students;
    private List<UserDTO> professors;
    private List<UserDTO> admins;
    private List<SPMPDocument> documents;
    private List<ComplianceScore> evaluations;

    @BeforeEach
    void setUp() {
        // Create test users as DTOs
        UserDTO student1 = new UserDTO(1L, "student1", "student1@example.com", "John", "Student", Role.STUDENT, true);
        UserDTO student2 = new UserDTO(2L, "student2", "student2@example.com", "Jane", "Student", Role.STUDENT, true);
        UserDTO professor1 = new UserDTO(3L, "professor1", "professor1@example.com", "Bob", "Professor", Role.PROFESSOR,
                true);
        UserDTO admin1 = new UserDTO(4L, "admin1", "admin1@example.com", "Admin", "User", Role.ADMIN, true);

        students = Arrays.asList(student1, student2);
        professors = Arrays.asList(professor1);
        admins = Arrays.asList(admin1);
        allUsers = Arrays.asList(student1, student2, professor1, admin1);

        // Create test documents
        SPMPDocument doc1 = new SPMPDocument();
        doc1.setId(1L);
        doc1.setFileName("doc1.pdf");

        SPMPDocument doc2 = new SPMPDocument();
        doc2.setId(2L);
        doc2.setFileName("doc2.pdf");

        documents = Arrays.asList(doc1, doc2);

        // Create test evaluations
        ComplianceScore eval1 = new ComplianceScore();
        eval1.setId(1L);

        ComplianceScore eval2 = new ComplianceScore();
        eval2.setId(2L);

        ComplianceScore eval3 = new ComplianceScore();
        eval3.setId(3L);

        evaluations = Arrays.asList(eval1, eval2, eval3);
    }

    @Nested
    @DisplayName("GET /api/admin/reports/users")
    class GetUserReport {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return user report successfully")
        void getUserReport_Success() throws Exception {
            when(userService.getAllUsers()).thenReturn(allUsers);
            when(userService.getUsersByRole(Role.STUDENT)).thenReturn(students);
            when(userService.getUsersByRole(Role.PROFESSOR)).thenReturn(professors);
            when(userService.getUsersByRole(Role.ADMIN)).thenReturn(admins);

            mockMvc.perform(get("/api/admin/reports/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalUsers").value(4))
                    .andExpect(jsonPath("$.totalStudents").value(2))
                    .andExpect(jsonPath("$.totalProfessors").value(1))
                    .andExpect(jsonPath("$.totalAdmins").value(1))
                    .andExpect(jsonPath("$.generatedAt").exists());

            verify(userService).getAllUsers();
            verify(userService).getUsersByRole(Role.STUDENT);
            verify(userService).getUsersByRole(Role.PROFESSOR);
            verify(userService).getUsersByRole(Role.ADMIN);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return zeros when no users exist")
        void getUserReport_NoUsers() throws Exception {
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());
            when(userService.getUsersByRole(Role.STUDENT)).thenReturn(Collections.emptyList());
            when(userService.getUsersByRole(Role.PROFESSOR)).thenReturn(Collections.emptyList());
            when(userService.getUsersByRole(Role.ADMIN)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/reports/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalUsers").value(0))
                    .andExpect(jsonPath("$.totalStudents").value(0))
                    .andExpect(jsonPath("$.totalProfessors").value(0))
                    .andExpect(jsonPath("$.totalAdmins").value(0));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void getUserReport_ServiceException() throws Exception {
            when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/admin/reports/users"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to generate user report")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getUserReport_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/reports/users"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).getAllUsers();
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getUserReport_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/reports/users"))
                    .andExpect(status().isForbidden());

            verify(userService, never()).getAllUsers();
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getUserReport_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/reports/users"))
                    .andExpect(status().is3xxRedirection());

            verify(userService, never()).getAllUsers();
        }
    }

    @Nested
    @DisplayName("GET /api/admin/reports/submissions")
    class GetSubmissionReport {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return submission report successfully")
        void getSubmissionReport_Success() throws Exception {
            when(documentService.getAllDocuments()).thenReturn(documents);

            mockMvc.perform(get("/api/admin/reports/submissions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalSubmissions").value(2))
                    .andExpect(jsonPath("$.generatedAt").exists());

            verify(documentService).getAllDocuments();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return zero when no submissions exist")
        void getSubmissionReport_NoSubmissions() throws Exception {
            when(documentService.getAllDocuments()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/reports/submissions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalSubmissions").value(0));

            verify(documentService).getAllDocuments();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void getSubmissionReport_ServiceException() throws Exception {
            when(documentService.getAllDocuments()).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/admin/reports/submissions"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to generate submission report")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getSubmissionReport_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/reports/submissions"))
                    .andExpect(status().isForbidden());

            verify(documentService, never()).getAllDocuments();
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getSubmissionReport_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/reports/submissions"))
                    .andExpect(status().isForbidden());

            verify(documentService, never()).getAllDocuments();
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getSubmissionReport_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/reports/submissions"))
                    .andExpect(status().is3xxRedirection());

            verify(documentService, never()).getAllDocuments();
        }
    }

    @Nested
    @DisplayName("GET /api/admin/reports/evaluations")
    class GetEvaluationReport {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return evaluation report successfully")
        void getEvaluationReport_Success() throws Exception {
            when(evaluationService.getAllEvaluations()).thenReturn(evaluations);

            mockMvc.perform(get("/api/admin/reports/evaluations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEvaluations").value(3))
                    .andExpect(jsonPath("$.generatedAt").exists());

            verify(evaluationService).getAllEvaluations();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return zero when no evaluations exist")
        void getEvaluationReport_NoEvaluations() throws Exception {
            when(evaluationService.getAllEvaluations()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/reports/evaluations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEvaluations").value(0));

            verify(evaluationService).getAllEvaluations();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void getEvaluationReport_ServiceException() throws Exception {
            when(evaluationService.getAllEvaluations()).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/admin/reports/evaluations"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to generate evaluation report")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getEvaluationReport_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/reports/evaluations"))
                    .andExpect(status().isForbidden());

            verify(evaluationService, never()).getAllEvaluations();
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getEvaluationReport_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/reports/evaluations"))
                    .andExpect(status().isForbidden());

            verify(evaluationService, never()).getAllEvaluations();
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getEvaluationReport_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/reports/evaluations"))
                    .andExpect(status().is3xxRedirection());

            verify(evaluationService, never()).getAllEvaluations();
        }
    }

    @Nested
    @DisplayName("GET /api/admin/reports/system-health")
    class GetSystemHealthReport {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return system health report successfully")
        void getSystemHealthReport_Success() throws Exception {
            mockMvc.perform(get("/api/admin/reports/system-health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("healthy"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.uptime").value("Available via actuator endpoints"));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getSystemHealthReport_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/reports/system-health"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getSystemHealthReport_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/reports/system-health"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getSystemHealthReport_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/reports/system-health"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control Tests")
    class RoleBasedAccessTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN should have full access to all report endpoints")
        void adminHasFullAccess() throws Exception {
            when(userService.getAllUsers()).thenReturn(allUsers);
            when(userService.getUsersByRole(any())).thenReturn(Collections.emptyList());
            when(documentService.getAllDocuments()).thenReturn(Collections.emptyList());
            when(evaluationService.getAllEvaluations()).thenReturn(Collections.emptyList());

            // User report
            mockMvc.perform(get("/api/admin/reports/users"))
                    .andExpect(status().isOk());

            // Submission report
            mockMvc.perform(get("/api/admin/reports/submissions"))
                    .andExpect(status().isOk());

            // Evaluation report
            mockMvc.perform(get("/api/admin/reports/evaluations"))
                    .andExpect(status().isOk());

            // System health report
            mockMvc.perform(get("/api/admin/reports/system-health"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("PROFESSOR should be denied access to all admin report endpoints")
        void professorDeniedAccess() throws Exception {
            mockMvc.perform(get("/api/admin/reports/users"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/reports/submissions"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/reports/evaluations"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/reports/system-health"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("STUDENT should be denied access to all admin report endpoints")
        void studentDeniedAccess() throws Exception {
            mockMvc.perform(get("/api/admin/reports/users"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/reports/submissions"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/reports/evaluations"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/reports/system-health"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Report Data Accuracy Tests")
    class ReportDataAccuracyTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("User report should reflect actual user counts by role")
        void userReportReflectsActualCounts() throws Exception {
            // Create specific counts
            List<UserDTO> manyStudents = Arrays.asList(
                    createUserDTO(1L, "s1", Role.STUDENT),
                    createUserDTO(2L, "s2", Role.STUDENT),
                    createUserDTO(3L, "s3", Role.STUDENT),
                    createUserDTO(4L, "s4", Role.STUDENT),
                    createUserDTO(5L, "s5", Role.STUDENT));
            List<UserDTO> twoProfessors = Arrays.asList(
                    createUserDTO(6L, "p1", Role.PROFESSOR),
                    createUserDTO(7L, "p2", Role.PROFESSOR));
            List<UserDTO> oneAdmin = Arrays.asList(createUserDTO(8L, "a1", Role.ADMIN));

            List<UserDTO> all = Arrays.asList(
                    manyStudents.get(0), manyStudents.get(1), manyStudents.get(2),
                    manyStudents.get(3), manyStudents.get(4),
                    twoProfessors.get(0), twoProfessors.get(1),
                    oneAdmin.get(0));

            when(userService.getAllUsers()).thenReturn(all);
            when(userService.getUsersByRole(Role.STUDENT)).thenReturn(manyStudents);
            when(userService.getUsersByRole(Role.PROFESSOR)).thenReturn(twoProfessors);
            when(userService.getUsersByRole(Role.ADMIN)).thenReturn(oneAdmin);

            mockMvc.perform(get("/api/admin/reports/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalUsers").value(8))
                    .andExpect(jsonPath("$.totalStudents").value(5))
                    .andExpect(jsonPath("$.totalProfessors").value(2))
                    .andExpect(jsonPath("$.totalAdmins").value(1));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Submission report should reflect actual document count")
        void submissionReportReflectsActualCount() throws Exception {
            List<SPMPDocument> manyDocs = Arrays.asList(
                    createDocument(1L), createDocument(2L), createDocument(3L),
                    createDocument(4L), createDocument(5L), createDocument(6L),
                    createDocument(7L), createDocument(8L), createDocument(9L),
                    createDocument(10L));

            when(documentService.getAllDocuments()).thenReturn(manyDocs);

            mockMvc.perform(get("/api/admin/reports/submissions"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalSubmissions").value(10));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Evaluation report should reflect actual evaluation count")
        void evaluationReportReflectsActualCount() throws Exception {
            List<ComplianceScore> manyEvals = Arrays.asList(
                    createEvaluation(1L), createEvaluation(2L), createEvaluation(3L),
                    createEvaluation(4L), createEvaluation(5L));

            when(evaluationService.getAllEvaluations()).thenReturn(manyEvals);

            mockMvc.perform(get("/api/admin/reports/evaluations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEvaluations").value(5));
        }
    }

    // Helper methods
    private UserDTO createUserDTO(Long id, String username, Role role) {
        return new UserDTO(id, username, username + "@example.com", "First", "Last", role, true);
    }

    private SPMPDocument createDocument(Long id) {
        SPMPDocument doc = new SPMPDocument();
        doc.setId(id);
        doc.setFileName("doc" + id + ".pdf");
        return doc;
    }

    private ComplianceScore createEvaluation(Long id) {
        ComplianceScore eval = new ComplianceScore();
        eval.setId(id);
        return eval;
    }
}
