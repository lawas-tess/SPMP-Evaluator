package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.Task;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import com.team02.spmpevaluator.repository.SPMPDocumentRepository;
import com.team02.spmpevaluator.service.AuditLogService;
import com.team02.spmpevaluator.service.TaskService;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ReportingController Integration Tests")
class ReportingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComplianceScoreRepository complianceScoreRepository;

    @MockBean
    private SPMPDocumentRepository documentRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private TaskService taskService;

    @MockBean
    private AuditLogService auditLogService;

    private User professorUser;
    private User studentUser;
    private SPMPDocument evaluatedDocument;
    private SPMPDocument unevaluatedDocument;
    private ComplianceScore complianceScore;
    private Task completedTask;
    private Task pendingTask;

    @BeforeEach
    void setUp() {
        // Setup professor user
        professorUser = new User();
        professorUser.setId(1L);
        professorUser.setUsername("professor");
        professorUser.setEmail("professor@test.com");
        professorUser.setFirstName("John");
        professorUser.setLastName("Doe");
        professorUser.setRole(Role.PROFESSOR);

        // Setup student user
        studentUser = new User();
        studentUser.setId(2L);
        studentUser.setUsername("student");
        studentUser.setEmail("student@test.com");
        studentUser.setFirstName("Jane");
        studentUser.setLastName("Smith");
        studentUser.setRole(Role.STUDENT);

        // Setup compliance score
        complianceScore = new ComplianceScore();
        complianceScore.setId(1L);
        complianceScore.setOverallScore(85.0);
        complianceScore.setCompliant(true);
        complianceScore.setEvaluatedAt(LocalDateTime.now());

        // Setup evaluated document
        evaluatedDocument = new SPMPDocument();
        evaluatedDocument.setId(1L);
        evaluatedDocument.setFileName("SPMP_Document.docx");
        evaluatedDocument.setUploadedBy(studentUser);
        evaluatedDocument.setEvaluated(true);
        evaluatedDocument.setComplianceScore(complianceScore);

        // Setup unevaluated document
        unevaluatedDocument = new SPMPDocument();
        unevaluatedDocument.setId(2L);
        unevaluatedDocument.setFileName("Draft_Document.docx");
        unevaluatedDocument.setUploadedBy(studentUser);
        unevaluatedDocument.setEvaluated(false);

        // Setup completed task
        completedTask = new Task();
        completedTask.setId(1L);
        completedTask.setTitle("Submit SPMP");
        completedTask.setCompleted(true);
        completedTask.setDeadline(LocalDateTime.now().plusDays(7));

        // Setup pending task
        pendingTask = new Task();
        pendingTask.setId(2L);
        pendingTask.setTitle("Review feedback");
        pendingTask.setCompleted(false);
        pendingTask.setDeadline(LocalDateTime.now().minusDays(1)); // Overdue
    }

    @Nested
    @DisplayName("GET /api/reports/compliance-statistics - Get Compliance Statistics")
    class GetComplianceStatisticsTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should get compliance statistics for professor")
        void getComplianceStatistics_Success() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(documentRepository.findByEvaluated(true)).thenReturn(List.of(evaluatedDocument));

            mockMvc.perform(get("/api/reports/compliance-statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEvaluated").value(1))
                    .andExpect(jsonPath("$.averageScore").value("85.00"))
                    .andExpect(jsonPath("$.compliantDocuments").value(1))
                    .andExpect(jsonPath("$.nonCompliantDocuments").value(0));

            verify(documentRepository).findByEvaluated(true);
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should return empty stats when no evaluated documents")
        void getComplianceStatistics_NoDocuments() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(documentRepository.findByEvaluated(true)).thenReturn(List.of());

            mockMvc.perform(get("/api/reports/compliance-statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEvaluated").value(0));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should deny access to students")
        void getComplianceStatistics_ForbiddenForStudent() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));

            mockMvc.perform(get("/api/reports/compliance-statistics"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Only professors can access")));
        }

        @Test
        @DisplayName("Should require authentication")
        void getComplianceStatistics_RequiresAuth() throws Exception {
            mockMvc.perform(get("/api/reports/compliance-statistics"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should calculate stats for multiple documents")
        void getComplianceStatistics_MultipleDocuments() throws Exception {
            ComplianceScore nonCompliantScore = new ComplianceScore();
            nonCompliantScore.setOverallScore(50.0);
            nonCompliantScore.setCompliant(false);

            SPMPDocument nonCompliantDoc = new SPMPDocument();
            nonCompliantDoc.setId(3L);
            nonCompliantDoc.setEvaluated(true);
            nonCompliantDoc.setComplianceScore(nonCompliantScore);

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(documentRepository.findByEvaluated(true)).thenReturn(List.of(evaluatedDocument, nonCompliantDoc));

            mockMvc.perform(get("/api/reports/compliance-statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEvaluated").value(2))
                    .andExpect(jsonPath("$.compliantDocuments").value(1))
                    .andExpect(jsonPath("$.nonCompliantDocuments").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/reports/student-performance/{userId} - Get Student Performance")
    class GetStudentPerformanceTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should get student performance for professor")
        void getStudentPerformance_Success() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(documentRepository.findByUploadedBy_Id(2L))
                    .thenReturn(List.of(evaluatedDocument, unevaluatedDocument));

            mockMvc.perform(get("/api/reports/student-performance/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.studentId").value(2))
                    .andExpect(jsonPath("$.totalUploads").value(2))
                    .andExpect(jsonPath("$.totalEvaluated").value(1))
                    .andExpect(jsonPath("$.averageScore").value("85.00"));

            verify(documentRepository).findByUploadedBy_Id(2L);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should deny access to students")
        void getStudentPerformance_ForbiddenForStudent() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));

            mockMvc.perform(get("/api/reports/student-performance/2"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Only professors can access")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should return zero average when no evaluated documents")
        void getStudentPerformance_NoEvaluatedDocs() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(documentRepository.findByUploadedBy_Id(2L)).thenReturn(List.of(unevaluatedDocument));

            mockMvc.perform(get("/api/reports/student-performance/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalUploads").value(1))
                    .andExpect(jsonPath("$.totalEvaluated").value(0))
                    .andExpect(jsonPath("$.averageScore").value("0.00"));
        }

        @Test
        @WithMockUser(username = "unknown")
        @DisplayName("Should fail when user not found")
        void getStudentPerformance_UserNotFound() throws Exception {
            when(userService.findByUsername("unknown")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/reports/student-performance/2"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/reports/student-progress/{userId} - Get Student Progress")
    class GetStudentProgressTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should get comprehensive student progress for professor")
        void getStudentProgress_AsProfessor() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(userService.findById(2L)).thenReturn(Optional.of(studentUser));
            when(documentRepository.findByUploadedBy_Id(2L))
                    .thenReturn(List.of(evaluatedDocument, unevaluatedDocument));
            when(taskService.getTasksByAssignedUser(2L)).thenReturn(List.of(completedTask, pendingTask));
            doNothing().when(auditLogService).logStudentProgressView(anyLong(), anyLong(), anyString());

            mockMvc.perform(get("/api/reports/student-progress/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.studentId").value(2))
                    .andExpect(jsonPath("$.studentName").value("Jane Smith"))
                    .andExpect(jsonPath("$.documents.totalUploads").value(2))
                    .andExpect(jsonPath("$.documents.evaluated").value(1))
                    .andExpect(jsonPath("$.documents.pending").value(1))
                    .andExpect(jsonPath("$.tasks.totalTasks").value(2))
                    .andExpect(jsonPath("$.tasks.completed").value(1))
                    .andExpect(jsonPath("$.tasks.pending").value(1))
                    .andExpect(jsonPath("$.tasks.overdue").value(1));

            verify(auditLogService).logStudentProgressView(1L, 2L, "127.0.0.1");
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should allow student to view their own progress")
        void getStudentProgress_OwnProgress() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(userService.findById(2L)).thenReturn(Optional.of(studentUser));
            when(documentRepository.findByUploadedBy_Id(2L)).thenReturn(List.of(evaluatedDocument));
            when(taskService.getTasksByAssignedUser(2L)).thenReturn(List.of(completedTask));

            mockMvc.perform(get("/api/reports/student-progress/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.studentId").value(2))
                    .andExpect(jsonPath("$.studentName").value("Jane Smith"));

            // Students viewing their own progress should NOT log audit
            verify(auditLogService, never()).logStudentProgressView(anyLong(), anyLong(), anyString());
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should deny student from viewing other student's progress")
        void getStudentProgress_ForbiddenOtherStudent() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));

            mockMvc.perform(get("/api/reports/student-progress/999"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("only view your own progress")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should fail when student not found")
        void getStudentProgress_StudentNotFound() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(userService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/reports/student-progress/999"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Student not found")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should calculate completion rate correctly")
        void getStudentProgress_CompletionRate() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(userService.findById(2L)).thenReturn(Optional.of(studentUser));
            when(documentRepository.findByUploadedBy_Id(2L)).thenReturn(List.of());
            when(taskService.getTasksByAssignedUser(2L)).thenReturn(List.of(completedTask, pendingTask));
            doNothing().when(auditLogService).logStudentProgressView(anyLong(), anyLong(), anyString());

            mockMvc.perform(get("/api/reports/student-progress/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tasks.completionRate").value("50.00%"));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should handle zero tasks")
        void getStudentProgress_NoTasks() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(userService.findById(2L)).thenReturn(Optional.of(studentUser));
            when(documentRepository.findByUploadedBy_Id(2L)).thenReturn(List.of());
            when(taskService.getTasksByAssignedUser(2L)).thenReturn(List.of());
            doNothing().when(auditLogService).logStudentProgressView(anyLong(), anyLong(), anyString());

            mockMvc.perform(get("/api/reports/student-progress/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tasks.totalTasks").value(0))
                    .andExpect(jsonPath("$.tasks.completionRate").value("0.00%"));
        }
    }

    @Nested
    @DisplayName("GET /api/reports/compliance-trends - Get Compliance Trends")
    class GetComplianceTrendsTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should get compliance trends with default period")
        void getComplianceTrends_DefaultPeriod() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(complianceScoreRepository.findAll()).thenReturn(List.of(complianceScore));

            mockMvc.perform(get("/api/reports/compliance-trends"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.period").value("30 days"))
                    .andExpect(jsonPath("$.totalEvaluations").value(1))
                    .andExpect(jsonPath("$.averageScore").value("85.00"));

            verify(complianceScoreRepository).findAll();
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should get compliance trends with custom period")
        void getComplianceTrends_CustomPeriod() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(complianceScoreRepository.findAll()).thenReturn(List.of(complianceScore));

            mockMvc.perform(get("/api/reports/compliance-trends")
                    .param("daysBack", "7"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.period").value("7 days"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should deny access to students")
        void getComplianceTrends_ForbiddenForStudent() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));

            mockMvc.perform(get("/api/reports/compliance-trends"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Only professors can access")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should filter out old scores")
        void getComplianceTrends_FilterOldScores() throws Exception {
            ComplianceScore oldScore = new ComplianceScore();
            oldScore.setOverallScore(70.0);
            oldScore.setEvaluatedAt(LocalDateTime.now().minusDays(60));

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(complianceScoreRepository.findAll()).thenReturn(List.of(complianceScore, oldScore));

            mockMvc.perform(get("/api/reports/compliance-trends")
                    .param("daysBack", "30"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEvaluations").value(1)); // Only recent score
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should calculate compliance rate")
        void getComplianceTrends_ComplianceRate() throws Exception {
            ComplianceScore nonCompliantScore = new ComplianceScore();
            nonCompliantScore.setOverallScore(50.0);
            nonCompliantScore.setCompliant(false);
            nonCompliantScore.setEvaluatedAt(LocalDateTime.now());

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(complianceScoreRepository.findAll()).thenReturn(List.of(complianceScore, nonCompliantScore));

            mockMvc.perform(get("/api/reports/compliance-trends"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalEvaluations").value(2))
                    .andExpect(jsonPath("$.complianceRate").value("50.00%"));
        }
    }

    @Nested
    @DisplayName("PUT /api/reports/{documentId}/override-score - Override Score")
    class OverrideScoreTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should override score successfully")
        void overrideScore_Success() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(complianceScoreRepository.findByDocumentId(1L)).thenReturn(Optional.of(complianceScore));
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenReturn(complianceScore);

            mockMvc.perform(put("/api/reports/1/override-score")
                    .param("newScore", "90.0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("Excellent work, adjusted for format"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Score overridden successfully"));

            verify(complianceScoreRepository).save(any(ComplianceScore.class));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should deny score override for students")
        void overrideScore_ForbiddenForStudent() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));

            mockMvc.perform(put("/api/reports/1/override-score")
                    .param("newScore", "90.0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("Override notes"))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Only professors can override")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should reject invalid score below 0")
        void overrideScore_ScoreTooLow() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));

            mockMvc.perform(put("/api/reports/1/override-score")
                    .param("newScore", "-10.0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("Notes"))
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            content().string(org.hamcrest.Matchers.containsString("Score must be between 0 and 100")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should reject invalid score above 100")
        void overrideScore_ScoreTooHigh() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));

            mockMvc.perform(put("/api/reports/1/override-score")
                    .param("newScore", "150.0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("Notes"))
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            content().string(org.hamcrest.Matchers.containsString("Score must be between 0 and 100")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should fail when compliance score not found")
        void overrideScore_ScoreNotFound() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(complianceScoreRepository.findByDocumentId(999L)).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/reports/999/override-score")
                    .param("newScore", "90.0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("Notes"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Compliance score not found")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should accept minimum valid score")
        void overrideScore_MinimumScore() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(complianceScoreRepository.findByDocumentId(1L)).thenReturn(Optional.of(complianceScore));
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenReturn(complianceScore);

            mockMvc.perform(put("/api/reports/1/override-score")
                    .param("newScore", "0.0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("Severe issues found"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Score overridden successfully"));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should accept maximum valid score")
        void overrideScore_MaximumScore() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(complianceScoreRepository.findByDocumentId(1L)).thenReturn(Optional.of(complianceScore));
            when(complianceScoreRepository.save(any(ComplianceScore.class))).thenReturn(complianceScore);

            mockMvc.perform(put("/api/reports/1/override-score")
                    .param("newScore", "100.0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("Perfect submission"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Score overridden successfully"));
        }

        @Test
        @DisplayName("Should require authentication for override")
        void overrideScore_RequiresAuth() throws Exception {
            mockMvc.perform(put("/api/reports/1/override-score")
                    .param("newScore", "90.0")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("Notes"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control Tests")
    class RoleBasedAccessTests {

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Admin should access compliance statistics")
        void complianceStats_AdminAccess() throws Exception {
            User adminUser = new User();
            adminUser.setId(3L);
            adminUser.setUsername("admin");
            adminUser.setRole(Role.ADMIN);

            when(userService.findByUsername("admin")).thenReturn(Optional.of(adminUser));
            when(documentRepository.findByEvaluated(true)).thenReturn(List.of());

            mockMvc.perform(get("/api/reports/compliance-statistics"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Admin should access student performance")
        void studentPerformance_AdminAccess() throws Exception {
            User adminUser = new User();
            adminUser.setId(3L);
            adminUser.setUsername("admin");
            adminUser.setRole(Role.ADMIN);

            when(userService.findByUsername("admin")).thenReturn(Optional.of(adminUser));
            when(documentRepository.findByUploadedBy_Id(2L)).thenReturn(List.of());

            mockMvc.perform(get("/api/reports/student-performance/2"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "admin", roles = { "ADMIN" })
        @DisplayName("Admin should access student progress")
        void studentProgress_AdminAccess() throws Exception {
            User adminUser = new User();
            adminUser.setId(3L);
            adminUser.setUsername("admin");
            adminUser.setRole(Role.ADMIN);

            when(userService.findByUsername("admin")).thenReturn(Optional.of(adminUser));
            when(userService.findById(2L)).thenReturn(Optional.of(studentUser));
            when(documentRepository.findByUploadedBy_Id(2L)).thenReturn(List.of());
            when(taskService.getTasksByAssignedUser(2L)).thenReturn(List.of());

            mockMvc.perform(get("/api/reports/student-progress/2"))
                    .andExpect(status().isOk());
        }
    }
}
