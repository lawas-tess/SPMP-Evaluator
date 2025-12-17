package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.AuditLog;
import com.team02.spmpevaluator.entity.AuditLog.ActionType;
import com.team02.spmpevaluator.entity.AuditLog.ResourceType;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.AuditLogRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuditLogService.
 * UC 2.4, UC 2.5, UC 2.10: Activity tracking and logging
 */
@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private User testStudent;
    private User testProfessor;
    private User testAdmin;
    private AuditLog testLog;
    private AuditLog testLog2;

    @BeforeEach
    void setUp() {
        // Setup test student
        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setUsername("student1");
        testStudent.setEmail("student@example.com");
        testStudent.setRole(Role.STUDENT);
        testStudent.setEnabled(true);

        // Setup test professor
        testProfessor = new User();
        testProfessor.setId(2L);
        testProfessor.setUsername("professor1");
        testProfessor.setEmail("professor@example.com");
        testProfessor.setRole(Role.PROFESSOR);
        testProfessor.setEnabled(true);

        // Setup test admin
        testAdmin = new User();
        testAdmin.setId(3L);
        testAdmin.setUsername("admin1");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setEnabled(true);

        // Setup test audit log
        testLog = new AuditLog();
        testLog.setId(1L);
        testLog.setUser(testStudent);
        testLog.setAction(ActionType.VIEW);
        testLog.setResourceType(ResourceType.DOCUMENT);
        testLog.setResourceId(100L);
        testLog.setDetails("Viewed document");
        testLog.setIpAddress("192.168.1.1");
        testLog.setCreatedAt(LocalDateTime.now());

        // Setup second test log
        testLog2 = new AuditLog();
        testLog2.setId(2L);
        testLog2.setUser(testStudent);
        testLog2.setAction(ActionType.UPLOAD);
        testLog2.setResourceType(ResourceType.DOCUMENT);
        testLog2.setResourceId(101L);
        testLog2.setDetails("Uploaded document");
        testLog2.setIpAddress("192.168.1.1");
        testLog2.setCreatedAt(LocalDateTime.now().minusHours(1));
    }

    @Nested
    @DisplayName("Log View Activity Tests")
    class LogViewActivityTests {

        @Test
        @DisplayName("Should log view activity successfully")
        void logViewActivity_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
                AuditLog log = invocation.getArgument(0);
                log.setId(1L);
                return log;
            });

            // Act
            auditLogService.logViewActivity(1L, ResourceType.DOCUMENT, 100L,
                    "Viewed document", "192.168.1.1");

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());

            AuditLog savedLog = captor.getValue();
            assertEquals(testStudent, savedLog.getUser());
            assertEquals(ActionType.VIEW, savedLog.getAction());
            assertEquals(ResourceType.DOCUMENT, savedLog.getResourceType());
            assertEquals(100L, savedLog.getResourceId());
            assertEquals("Viewed document", savedLog.getDetails());
            assertEquals("192.168.1.1", savedLog.getIpAddress());
        }

        @Test
        @DisplayName("Should log view activity without IP address")
        void logViewActivity_NoIpAddress() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testLog);

            // Act
            auditLogService.logViewActivity(1L, ResourceType.DOCUMENT, 100L,
                    "Viewed document", null);

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());
            assertNull(captor.getValue().getIpAddress());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void logViewActivity_UserNotFound() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> auditLogService.logViewActivity(99L, ResourceType.DOCUMENT, 100L,
                            "Viewed document", "192.168.1.1"));
            assertEquals("User not found", exception.getMessage());
            verify(auditLogRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Log Action Tests")
    class LogActionTests {

        @Test
        @DisplayName("Should log action successfully")
        void logAction_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
                AuditLog log = invocation.getArgument(0);
                log.setId(1L);
                return log;
            });

            // Act
            auditLogService.logAction(1L, ActionType.UPLOAD, ResourceType.DOCUMENT,
                    100L, "Uploaded document", "192.168.1.1");

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());

            AuditLog savedLog = captor.getValue();
            assertEquals(ActionType.UPLOAD, savedLog.getAction());
            assertEquals(ResourceType.DOCUMENT, savedLog.getResourceType());
        }

        @Test
        @DisplayName("Should log CREATE action")
        void logAction_Create() {
            // Arrange
            when(userRepository.findById(3L)).thenReturn(Optional.of(testAdmin));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testLog);

            // Act
            auditLogService.logAction(3L, ActionType.CREATE, ResourceType.USER,
                    5L, "Created new user", "192.168.1.1");

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());
            assertEquals(ActionType.CREATE, captor.getValue().getAction());
        }

        @Test
        @DisplayName("Should log DELETE action")
        void logAction_Delete() {
            // Arrange
            when(userRepository.findById(3L)).thenReturn(Optional.of(testAdmin));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testLog);

            // Act
            auditLogService.logAction(3L, ActionType.DELETE, ResourceType.DOCUMENT,
                    100L, "Deleted document", "192.168.1.1");

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());
            assertEquals(ActionType.DELETE, captor.getValue().getAction());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void logAction_UserNotFound() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> auditLogService.logAction(99L, ActionType.CREATE, ResourceType.USER,
                            5L, "Created user", "192.168.1.1"));
            assertEquals("User not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Log Feedback View Tests (UC 2.4)")
    class LogFeedbackViewTests {

        @Test
        @DisplayName("Should log feedback view successfully")
        void logFeedbackView_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testLog);

            // Act
            auditLogService.logFeedbackView(1L, 100L, "192.168.1.1");

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());

            AuditLog savedLog = captor.getValue();
            assertEquals(ActionType.VIEW, savedLog.getAction());
            assertEquals(ResourceType.EVALUATION, savedLog.getResourceType());
            assertEquals(100L, savedLog.getResourceId());
            assertEquals("Student viewed feedback for document", savedLog.getDetails());
        }

        @Test
        @DisplayName("Should log feedback view without IP address")
        void logFeedbackView_NoIp() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testLog);

            // Act
            auditLogService.logFeedbackView(1L, 100L, null);

            // Assert
            verify(auditLogRepository).save(any(AuditLog.class));
        }
    }

    @Nested
    @DisplayName("Log Task View Tests (UC 2.5)")
    class LogTaskViewTests {

        @Test
        @DisplayName("Should log task view successfully")
        void logTaskView_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testLog);

            // Act
            auditLogService.logTaskView(1L, 50L, "192.168.1.1");

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());

            AuditLog savedLog = captor.getValue();
            assertEquals(ActionType.VIEW, savedLog.getAction());
            assertEquals(ResourceType.TASK, savedLog.getResourceType());
            assertEquals(50L, savedLog.getResourceId());
            assertEquals("Student viewed task details", savedLog.getDetails());
        }
    }

    @Nested
    @DisplayName("Log Student Progress View Tests (UC 2.10)")
    class LogStudentProgressViewTests {

        @Test
        @DisplayName("Should log student progress view successfully")
        void logStudentProgressView_Success() {
            // Arrange
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testLog);

            // Act
            auditLogService.logStudentProgressView(2L, 1L, "192.168.1.1");

            // Assert
            ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
            verify(auditLogRepository).save(captor.capture());

            AuditLog savedLog = captor.getValue();
            assertEquals(ActionType.VIEW, savedLog.getAction());
            assertEquals(ResourceType.USER, savedLog.getResourceType());
            assertEquals(1L, savedLog.getResourceId());
            assertEquals("Professor viewed student progress", savedLog.getDetails());
        }
    }

    @Nested
    @DisplayName("Get View Logs Tests")
    class GetViewLogsTests {

        @Test
        @DisplayName("Should return view logs for user")
        void getViewLogs_Success() {
            // Arrange
            List<AuditLog> viewLogs = Arrays.asList(testLog);
            when(auditLogRepository.findByUserIdAndActionOrderByCreatedAtDesc(1L, ActionType.VIEW))
                    .thenReturn(viewLogs);

            // Act
            List<AuditLog> result = auditLogService.getViewLogs(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(ActionType.VIEW, result.get(0).getAction());
        }

        @Test
        @DisplayName("Should return empty list when no view logs")
        void getViewLogs_Empty() {
            // Arrange
            when(auditLogRepository.findByUserIdAndActionOrderByCreatedAtDesc(1L, ActionType.VIEW))
                    .thenReturn(Collections.emptyList());

            // Act
            List<AuditLog> result = auditLogService.getViewLogs(1L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get User Logs Tests")
    class GetUserLogsTests {

        @Test
        @DisplayName("Should return all logs for user")
        void getUserLogs_Success() {
            // Arrange
            List<AuditLog> userLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findByUserId(1L)).thenReturn(userLogs);

            // Act
            List<AuditLog> result = auditLogService.getUserLogs(1L);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when user has no logs")
        void getUserLogs_Empty() {
            // Arrange
            when(auditLogRepository.findByUserId(99L)).thenReturn(Collections.emptyList());

            // Act
            List<AuditLog> result = auditLogService.getUserLogs(99L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get All Logs Tests")
    class GetAllLogsTests {

        @Test
        @DisplayName("Should return all logs")
        void getAllLogs_Success() {
            // Arrange
            AuditLog adminLog = new AuditLog();
            adminLog.setId(3L);
            adminLog.setUser(testAdmin);
            adminLog.setAction(ActionType.CREATE);
            adminLog.setResourceType(ResourceType.USER);

            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2, adminLog);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getAllLogs();

            // Assert
            assertNotNull(result);
            assertEquals(3, result.size());
            verify(auditLogRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no logs exist")
        void getAllLogs_Empty() {
            // Arrange
            when(auditLogRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<AuditLog> result = auditLogService.getAllLogs();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Logs by Username Tests")
    class GetLogsByUsernameTests {

        @Test
        @DisplayName("Should return logs for specific username")
        void getLogsByUsername_Success() {
            // Arrange
            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsByUsername("student1");

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.stream().allMatch(log -> log.getUser().getUsername().equals("student1")));
        }

        @Test
        @DisplayName("Should return empty list for non-existent username")
        void getLogsByUsername_NotFound() {
            // Arrange
            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsByUsername("nonexistent");

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle multiple users with different logs")
        void getLogsByUsername_MultipleUsers() {
            // Arrange
            AuditLog profLog = new AuditLog();
            profLog.setId(3L);
            profLog.setUser(testProfessor);
            profLog.setAction(ActionType.VIEW);
            profLog.setResourceType(ResourceType.USER);
            profLog.setCreatedAt(LocalDateTime.now());

            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2, profLog);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsByUsername("professor1");

            // Assert
            assertEquals(1, result.size());
            assertEquals("professor1", result.get(0).getUser().getUsername());
        }
    }

    @Nested
    @DisplayName("Get Logs by Action Tests")
    class GetLogsByActionTests {

        @Test
        @DisplayName("Should return logs for VIEW action")
        void getLogsByAction_View() {
            // Arrange
            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsByAction("VIEW");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(ActionType.VIEW, result.get(0).getAction());
        }

        @Test
        @DisplayName("Should return logs for UPLOAD action")
        void getLogsByAction_Upload() {
            // Arrange
            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsByAction("UPLOAD");

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(ActionType.UPLOAD, result.get(0).getAction());
        }

        @Test
        @DisplayName("Should throw exception for invalid action")
        void getLogsByAction_InvalidAction() {
            // Act & Assert - Exception thrown during enum valueOf, before findAll is called
            assertThrows(IllegalArgumentException.class,
                    () -> auditLogService.getLogsByAction("INVALID_ACTION"));
        }
    }

    @Nested
    @DisplayName("Get Logs by Resource Type Tests")
    class GetLogsByResourceTypeTests {

        @Test
        @DisplayName("Should return logs for DOCUMENT resource type")
        void getLogsByResourceType_Document() {
            // Arrange
            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsByResourceType("DOCUMENT");

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.stream().allMatch(log -> log.getResourceType() == ResourceType.DOCUMENT));
        }

        @Test
        @DisplayName("Should return empty list for unused resource type")
        void getLogsByResourceType_Empty() {
            // Arrange
            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsByResourceType("SYSTEM");

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw exception for invalid resource type")
        void getLogsByResourceType_InvalidType() {
            // Act & Assert - Exception thrown during enum valueOf, before findAll is called
            assertThrows(IllegalArgumentException.class,
                    () -> auditLogService.getLogsByResourceType("INVALID_TYPE"));
        }
    }

    @Nested
    @DisplayName("Get Logs Between Dates Tests")
    class GetLogsBetweenDatesTests {

        @Test
        @DisplayName("Should return logs within date range")
        void getLogsBetweenDates_Success() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            testLog.setCreatedAt(now);
            testLog2.setCreatedAt(now.minusHours(1));

            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsBetweenDates(
                    now.minusHours(2), now.plusHours(1));

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should exclude logs outside date range")
        void getLogsBetweenDates_Partial() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            testLog.setCreatedAt(now);
            testLog2.setCreatedAt(now.minusDays(2));

            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsBetweenDates(
                    now.minusHours(1), now.plusHours(1));

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no logs in range")
        void getLogsBetweenDates_Empty() {
            // Arrange
            LocalDateTime now = LocalDateTime.now();
            testLog.setCreatedAt(now.minusDays(10));
            testLog2.setCreatedAt(now.minusDays(11));

            List<AuditLog> allLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findAll()).thenReturn(allLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsBetweenDates(
                    now.minusDays(1), now);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Log by ID Tests")
    class GetLogByIdTests {

        @Test
        @DisplayName("Should return log by ID")
        void getLogById_Success() {
            // Arrange
            when(auditLogRepository.findById(1L)).thenReturn(Optional.of(testLog));

            // Act
            AuditLog result = auditLogService.getLogById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(testStudent, result.getUser());
            assertEquals(ActionType.VIEW, result.getAction());
        }

        @Test
        @DisplayName("Should throw exception when log not found")
        void getLogById_NotFound() {
            // Arrange
            when(auditLogRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> auditLogService.getLogById(999L));
            assertEquals("Audit log not found with id: 999", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Get Logs by User ID Tests")
    class GetLogsByUserIdTests {

        @Test
        @DisplayName("Should return logs for user ID")
        void getLogsByUserId_Success() {
            // Arrange
            List<AuditLog> userLogs = Arrays.asList(testLog, testLog2);
            when(auditLogRepository.findByUserId(1L)).thenReturn(userLogs);

            // Act
            List<AuditLog> result = auditLogService.getLogsByUserId(1L);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(auditLogRepository).findByUserId(1L);
        }

        @Test
        @DisplayName("Should return empty list for user with no logs")
        void getLogsByUserId_Empty() {
            // Arrange
            when(auditLogRepository.findByUserId(999L)).thenReturn(Collections.emptyList());

            // Act
            List<AuditLog> result = auditLogService.getLogsByUserId(999L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
