package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.entity.AuditLog;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.AuditLogService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AuditLogController.
 * UC 2.13: Admin Audit Logs
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;

    private User testUser;
    private AuditLog auditLog1;
    private AuditLog auditLog2;
    private AuditLog auditLog3;
    private List<AuditLog> auditLogs;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(Role.STUDENT);
        testUser.setEnabled(true);

        // Create test audit logs
        auditLog1 = new AuditLog();
        auditLog1.setId(1L);
        auditLog1.setUser(testUser);
        auditLog1.setAction(AuditLog.ActionType.LOGIN);
        auditLog1.setResourceType(AuditLog.ResourceType.USER);
        auditLog1.setResourceId(1L);
        auditLog1.setDetails("User logged in");
        auditLog1.setIpAddress("127.0.0.1");
        auditLog1.setCreatedAt(LocalDateTime.now().minusHours(2));

        auditLog2 = new AuditLog();
        auditLog2.setId(2L);
        auditLog2.setUser(testUser);
        auditLog2.setAction(AuditLog.ActionType.UPLOAD);
        auditLog2.setResourceType(AuditLog.ResourceType.DOCUMENT);
        auditLog2.setResourceId(10L);
        auditLog2.setDetails("Document uploaded");
        auditLog2.setIpAddress("127.0.0.1");
        auditLog2.setCreatedAt(LocalDateTime.now().minusHours(1));

        auditLog3 = new AuditLog();
        auditLog3.setId(3L);
        auditLog3.setUser(testUser);
        auditLog3.setAction(AuditLog.ActionType.EVALUATE);
        auditLog3.setResourceType(AuditLog.ResourceType.EVALUATION);
        auditLog3.setResourceId(5L);
        auditLog3.setDetails("Document evaluated");
        auditLog3.setIpAddress("192.168.1.1");
        auditLog3.setCreatedAt(LocalDateTime.now());

        auditLogs = Arrays.asList(auditLog1, auditLog2, auditLog3);
    }

    @Nested
    @DisplayName("GET /api/admin/audit-logs")
    class GetAuditLogs {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return all audit logs when no filters provided")
        void getAuditLogs_NoFilters_Success() throws Exception {
            when(auditLogService.getAllLogs()).thenReturn(auditLogs);

            mockMvc.perform(get("/api/admin/audit-logs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[2].id").value(3));

            verify(auditLogService).getAllLogs();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return empty list when no logs exist")
        void getAuditLogs_EmptyList() throws Exception {
            when(auditLogService.getAllLogs()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/audit-logs"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(auditLogService).getAllLogs();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should filter logs by username")
        void getAuditLogs_FilterByUsername() throws Exception {
            when(auditLogService.getLogsByUsername("testuser")).thenReturn(auditLogs);

            mockMvc.perform(get("/api/admin/audit-logs")
                    .param("username", "testuser"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));

            verify(auditLogService).getLogsByUsername("testuser");
            verify(auditLogService, never()).getAllLogs();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should filter logs by action")
        void getAuditLogs_FilterByAction() throws Exception {
            List<AuditLog> loginLogs = Arrays.asList(auditLog1);
            when(auditLogService.getLogsByAction("LOGIN")).thenReturn(loginLogs);

            mockMvc.perform(get("/api/admin/audit-logs")
                    .param("action", "LOGIN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].action").value("LOGIN"));

            verify(auditLogService).getLogsByAction("LOGIN");
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should filter logs by resource type")
        void getAuditLogs_FilterByResource() throws Exception {
            List<AuditLog> documentLogs = Arrays.asList(auditLog2);
            when(auditLogService.getLogsByResourceType("DOCUMENT")).thenReturn(documentLogs);

            mockMvc.perform(get("/api/admin/audit-logs")
                    .param("resource", "DOCUMENT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].resourceType").value("DOCUMENT"));

            verify(auditLogService).getLogsByResourceType("DOCUMENT");
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should filter logs by date range")
        void getAuditLogs_FilterByDateRange() throws Exception {
            LocalDateTime startDate = LocalDateTime.now().minusDays(1);
            LocalDateTime endDate = LocalDateTime.now();
            when(auditLogService.getLogsBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(auditLogs);

            mockMvc.perform(get("/api/admin/audit-logs")
                    .param("startDate", startDate.toString())
                    .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));

            verify(auditLogService).getLogsBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void getAuditLogs_ServiceException() throws Exception {
            when(auditLogService.getAllLogs()).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/admin/audit-logs"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to retrieve audit logs")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getAuditLogs_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs"))
                    .andExpect(status().isForbidden());

            verify(auditLogService, never()).getAllLogs();
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getAuditLogs_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs"))
                    .andExpect(status().isForbidden());

            verify(auditLogService, never()).getAllLogs();
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getAuditLogs_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs"))
                    .andExpect(status().is3xxRedirection());

            verify(auditLogService, never()).getAllLogs();
        }
    }

    @Nested
    @DisplayName("GET /api/admin/audit-logs/{id}")
    class GetAuditLogById {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return audit log by ID successfully")
        void getAuditLogById_Success() throws Exception {
            when(auditLogService.getLogById(1L)).thenReturn(auditLog1);

            mockMvc.perform(get("/api/admin/audit-logs/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.action").value("LOGIN"))
                    .andExpect(jsonPath("$.resourceType").value("USER"))
                    .andExpect(jsonPath("$.details").value("User logged in"));

            verify(auditLogService).getLogById(1L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when audit log not found")
        void getAuditLogById_NotFound() throws Exception {
            when(auditLogService.getLogById(999L))
                    .thenThrow(new IllegalArgumentException("Audit log not found with id: 999"));

            mockMvc.perform(get("/api/admin/audit-logs/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("Audit log not found")));

            verify(auditLogService).getLogById(999L);
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getAuditLogById_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs/1"))
                    .andExpect(status().isForbidden());

            verify(auditLogService, never()).getLogById(anyLong());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getAuditLogById_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs/1"))
                    .andExpect(status().isForbidden());

            verify(auditLogService, never()).getLogById(anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getAuditLogById_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs/1"))
                    .andExpect(status().is3xxRedirection());

            verify(auditLogService, never()).getLogById(anyLong());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/audit-logs/user/{userId}")
    class GetAuditLogsByUserId {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return audit logs for user successfully")
        void getAuditLogsByUserId_Success() throws Exception {
            when(auditLogService.getLogsByUserId(1L)).thenReturn(auditLogs);

            mockMvc.perform(get("/api/admin/audit-logs/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[2].id").value(3));

            verify(auditLogService).getLogsByUserId(1L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return empty list when user has no logs")
        void getAuditLogsByUserId_EmptyList() throws Exception {
            when(auditLogService.getLogsByUserId(999L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/audit-logs/user/999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(auditLogService).getLogsByUserId(999L);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void getAuditLogsByUserId_ServiceException() throws Exception {
            when(auditLogService.getLogsByUserId(1L)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/admin/audit-logs/user/1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to retrieve user audit logs")));

            verify(auditLogService).getLogsByUserId(1L);
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getAuditLogsByUserId_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs/user/1"))
                    .andExpect(status().isForbidden());

            verify(auditLogService, never()).getLogsByUserId(anyLong());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getAuditLogsByUserId_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs/user/1"))
                    .andExpect(status().isForbidden());

            verify(auditLogService, never()).getLogsByUserId(anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getAuditLogsByUserId_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs/user/1"))
                    .andExpect(status().is3xxRedirection());

            verify(auditLogService, never()).getLogsByUserId(anyLong());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/audit-logs/export")
    class ExportAuditLogs {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should export all audit logs when no date range provided")
        void exportAuditLogs_NoDateRange() throws Exception {
            when(auditLogService.getAllLogs()).thenReturn(auditLogs);

            mockMvc.perform(get("/api/admin/audit-logs/export"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));

            verify(auditLogService).getAllLogs();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should export audit logs within date range")
        void exportAuditLogs_WithDateRange() throws Exception {
            LocalDateTime startDate = LocalDateTime.now().minusDays(1);
            LocalDateTime endDate = LocalDateTime.now();
            when(auditLogService.getLogsBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(auditLogs);

            mockMvc.perform(get("/api/admin/audit-logs/export")
                    .param("startDate", startDate.toString())
                    .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));

            verify(auditLogService).getLogsBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class));
            verify(auditLogService, never()).getAllLogs();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return empty list when no logs in date range")
        void exportAuditLogs_EmptyResult() throws Exception {
            LocalDateTime startDate = LocalDateTime.now().minusYears(10);
            LocalDateTime endDate = LocalDateTime.now().minusYears(9);
            when(auditLogService.getLogsBetweenDates(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/audit-logs/export")
                    .param("startDate", startDate.toString())
                    .param("endDate", endDate.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void exportAuditLogs_ServiceException() throws Exception {
            when(auditLogService.getAllLogs()).thenThrow(new RuntimeException("Export failed"));

            mockMvc.perform(get("/api/admin/audit-logs/export"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to export audit logs")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void exportAuditLogs_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs/export"))
                    .andExpect(status().isForbidden());

            verify(auditLogService, never()).getAllLogs();
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void exportAuditLogs_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs/export"))
                    .andExpect(status().isForbidden());

            verify(auditLogService, never()).getAllLogs();
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void exportAuditLogs_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs/export"))
                    .andExpect(status().is3xxRedirection());

            verify(auditLogService, never()).getAllLogs();
        }
    }

    @Nested
    @DisplayName("Role-Based Access Control Tests")
    class RoleBasedAccessTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("ADMIN should have full access to all audit log endpoints")
        void adminHasFullAccess() throws Exception {
            when(auditLogService.getAllLogs()).thenReturn(Collections.emptyList());
            when(auditLogService.getLogById(1L)).thenReturn(auditLog1);
            when(auditLogService.getLogsByUserId(1L)).thenReturn(Collections.emptyList());

            // GET all logs
            mockMvc.perform(get("/api/admin/audit-logs"))
                    .andExpect(status().isOk());

            // GET log by ID
            mockMvc.perform(get("/api/admin/audit-logs/1"))
                    .andExpect(status().isOk());

            // GET logs by user ID
            mockMvc.perform(get("/api/admin/audit-logs/user/1"))
                    .andExpect(status().isOk());

            // Export logs
            mockMvc.perform(get("/api/admin/audit-logs/export"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("PROFESSOR should be denied access to all audit log endpoints")
        void professorDeniedAccess() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/audit-logs/1"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/audit-logs/user/1"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/audit-logs/export"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("STUDENT should be denied access to all audit log endpoints")
        void studentDeniedAccess() throws Exception {
            mockMvc.perform(get("/api/admin/audit-logs"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/audit-logs/1"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/audit-logs/user/1"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/audit-logs/export"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Filter Priority Tests")
    class FilterPriorityTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Username filter should take priority over action filter")
        void usernameTakesPriorityOverAction() throws Exception {
            when(auditLogService.getLogsByUsername("testuser")).thenReturn(auditLogs);

            mockMvc.perform(get("/api/admin/audit-logs")
                    .param("username", "testuser")
                    .param("action", "LOGIN"))
                    .andExpect(status().isOk());

            verify(auditLogService).getLogsByUsername("testuser");
            verify(auditLogService, never()).getLogsByAction(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Action filter should take priority over resource filter")
        void actionTakesPriorityOverResource() throws Exception {
            when(auditLogService.getLogsByAction("LOGIN")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/audit-logs")
                    .param("action", "LOGIN")
                    .param("resource", "DOCUMENT"))
                    .andExpect(status().isOk());

            verify(auditLogService).getLogsByAction("LOGIN");
            verify(auditLogService, never()).getLogsByResourceType(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Resource filter should take priority over date range")
        void resourceTakesPriorityOverDateRange() throws Exception {
            when(auditLogService.getLogsByResourceType("DOCUMENT")).thenReturn(Collections.emptyList());
            LocalDateTime startDate = LocalDateTime.now().minusDays(1);
            LocalDateTime endDate = LocalDateTime.now();

            mockMvc.perform(get("/api/admin/audit-logs")
                    .param("resource", "DOCUMENT")
                    .param("startDate", startDate.toString())
                    .param("endDate", endDate.toString()))
                    .andExpect(status().isOk());

            verify(auditLogService).getLogsByResourceType("DOCUMENT");
            verify(auditLogService, never()).getLogsBetweenDates(any(), any());
        }
    }
}
