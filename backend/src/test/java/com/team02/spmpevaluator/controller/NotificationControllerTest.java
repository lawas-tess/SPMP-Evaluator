package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.entity.Notification;
import com.team02.spmpevaluator.entity.Notification.NotificationType;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.NotificationService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("NotificationController Integration Tests")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserService userService;

    private User studentUser;
    private Notification testNotification;
    private Notification unreadNotification;

    @BeforeEach
    void setUp() {
        // Setup student user
        studentUser = new User();
        studentUser.setId(1L);
        studentUser.setUsername("student");
        studentUser.setEmail("student@test.com");
        studentUser.setRole(Role.STUDENT);

        // Setup test notification (read)
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(studentUser);
        testNotification.setTitle("Task Assigned");
        testNotification.setMessage("You have been assigned a new task: Submit SPMP Document");
        testNotification.setType(NotificationType.TASK_ASSIGNED);
        testNotification.setResourceId(100L);
        testNotification.setRead(true);
        testNotification.setCreatedAt(LocalDateTime.now().minusHours(1));

        // Setup unread notification
        unreadNotification = new Notification();
        unreadNotification.setId(2L);
        unreadNotification.setUser(studentUser);
        unreadNotification.setTitle("Evaluation Complete");
        unreadNotification.setMessage("Your document has been evaluated");
        unreadNotification.setType(NotificationType.EVALUATION_COMPLETE);
        unreadNotification.setResourceId(200L);
        unreadNotification.setRead(false);
        unreadNotification.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("GET /api/notifications - Get My Notifications")
    class GetMyNotificationsTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get all notifications for current user")
        void getMyNotifications_Success() throws Exception {
            List<Notification> notifications = List.of(testNotification, unreadNotification);

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.getNotificationsForUser(1L)).thenReturn(notifications);

            mockMvc.perform(get("/api/notifications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].title").value("Task Assigned"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].title").value("Evaluation Complete"));

            verify(notificationService).getNotificationsForUser(1L);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should return empty list when no notifications")
        void getMyNotifications_EmptyList() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.getNotificationsForUser(1L)).thenReturn(List.of());

            mockMvc.perform(get("/api/notifications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(username = "unknown")
        @DisplayName("Should fail when user not found")
        void getMyNotifications_UserNotFound() throws Exception {
            when(userService.findByUsername("unknown")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/notifications"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("User not found")));
        }

        @Test
        @DisplayName("Should require authentication")
        void getMyNotifications_RequiresAuth() throws Exception {
            mockMvc.perform(get("/api/notifications"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/notifications/unread - Get Unread Notifications")
    class GetUnreadNotificationsTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get unread notifications for current user")
        void getUnreadNotifications_Success() throws Exception {
            List<Notification> unreadNotifications = List.of(unreadNotification);

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.getUnreadNotifications(1L)).thenReturn(unreadNotifications);

            mockMvc.perform(get("/api/notifications/unread"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(2))
                    .andExpect(jsonPath("$[0].title").value("Evaluation Complete"))
                    .andExpect(jsonPath("$[0].read").value(false));

            verify(notificationService).getUnreadNotifications(1L);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should return empty list when all notifications are read")
        void getUnreadNotifications_AllRead() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.getUnreadNotifications(1L)).thenReturn(List.of());

            mockMvc.perform(get("/api/notifications/unread"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle service exception")
        void getUnreadNotifications_Exception() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.getUnreadNotifications(1L))
                    .thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/notifications/unread"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to get notifications")));
        }
    }

    @Nested
    @DisplayName("GET /api/notifications/count - Get Notification Count")
    class GetNotificationCountTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get unread notification count")
        void getNotificationCount_Success() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.countUnreadNotifications(1L)).thenReturn(5L);

            mockMvc.perform(get("/api/notifications/count"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.unreadCount").value(5));

            verify(notificationService).countUnreadNotifications(1L);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should return zero when no unread notifications")
        void getNotificationCount_Zero() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.countUnreadNotifications(1L)).thenReturn(0L);

            mockMvc.perform(get("/api/notifications/count"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.unreadCount").value(0));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle service exception")
        void getNotificationCount_Exception() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.countUnreadNotifications(1L))
                    .thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/notifications/count"))
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            content().string(org.hamcrest.Matchers.containsString("Failed to get notification count")));
        }
    }

    @Nested
    @DisplayName("PUT /api/notifications/{notificationId}/read - Mark as Read")
    class MarkAsReadTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should mark notification as read successfully")
        void markAsRead_Success() throws Exception {
            doNothing().when(notificationService).markAsRead(1L);

            mockMvc.perform(put("/api/notifications/1/read"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Notification marked as read"));

            verify(notificationService).markAsRead(1L);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should fail when notification not found")
        void markAsRead_NotFound() throws Exception {
            doThrow(new RuntimeException("Notification not found"))
                    .when(notificationService).markAsRead(999L);

            mockMvc.perform(put("/api/notifications/999/read"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to mark notification")));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle service exception")
        void markAsRead_Exception() throws Exception {
            doThrow(new RuntimeException("Database error"))
                    .when(notificationService).markAsRead(1L);

            mockMvc.perform(put("/api/notifications/1/read"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to mark notification")));
        }

        @Test
        @DisplayName("Should require authentication")
        void markAsRead_RequiresAuth() throws Exception {
            mockMvc.perform(put("/api/notifications/1/read"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("PUT /api/notifications/read-all - Mark All as Read")
    class MarkAllAsReadTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should mark all notifications as read successfully")
        void markAllAsRead_Success() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            doNothing().when(notificationService).markAllAsRead(1L);

            mockMvc.perform(put("/api/notifications/read-all"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("All notifications marked as read"));

            verify(notificationService).markAllAsRead(1L);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should succeed even when no notifications exist")
        void markAllAsRead_NoNotifications() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            doNothing().when(notificationService).markAllAsRead(1L);

            mockMvc.perform(put("/api/notifications/read-all"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("All notifications marked as read"));
        }

        @Test
        @WithMockUser(username = "unknown")
        @DisplayName("Should fail when user not found")
        void markAllAsRead_UserNotFound() throws Exception {
            when(userService.findByUsername("unknown")).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/notifications/read-all"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("User not found")));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle service exception")
        void markAllAsRead_Exception() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            doThrow(new RuntimeException("Database error"))
                    .when(notificationService).markAllAsRead(1L);

            mockMvc.perform(put("/api/notifications/read-all"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to mark notifications")));
        }

        @Test
        @DisplayName("Should require authentication")
        void markAllAsRead_RequiresAuth() throws Exception {
            mockMvc.perform(put("/api/notifications/read-all"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("Notification Type Tests")
    class NotificationTypeTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle TASK_ASSIGNED notification type")
        void getNotifications_TaskAssigned() throws Exception {
            Notification taskNotification = new Notification();
            taskNotification.setId(1L);
            taskNotification.setUser(studentUser);
            taskNotification.setTitle("New Task");
            taskNotification.setMessage("Task assigned");
            taskNotification.setType(NotificationType.TASK_ASSIGNED);
            taskNotification.setRead(false);
            taskNotification.setCreatedAt(LocalDateTime.now());

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.getNotificationsForUser(1L)).thenReturn(List.of(taskNotification));

            mockMvc.perform(get("/api/notifications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].type").value("TASK_ASSIGNED"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle SCORE_OVERRIDE notification type")
        void getNotifications_ScoreOverride() throws Exception {
            Notification scoreNotification = new Notification();
            scoreNotification.setId(1L);
            scoreNotification.setUser(studentUser);
            scoreNotification.setTitle("Score Updated");
            scoreNotification.setMessage("Your score has been overridden by the professor");
            scoreNotification.setType(NotificationType.SCORE_OVERRIDE);
            scoreNotification.setResourceId(100L);
            scoreNotification.setRead(false);
            scoreNotification.setCreatedAt(LocalDateTime.now());

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.getNotificationsForUser(1L)).thenReturn(List.of(scoreNotification));

            mockMvc.perform(get("/api/notifications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].type").value("SCORE_OVERRIDE"))
                    .andExpect(jsonPath("$[0].resourceId").value(100));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle TASK_UPDATED notification type")
        void getNotifications_TaskUpdated() throws Exception {
            Notification updateNotification = new Notification();
            updateNotification.setId(1L);
            updateNotification.setUser(studentUser);
            updateNotification.setTitle("Task Updated");
            updateNotification.setMessage("A task you're assigned to has been updated");
            updateNotification.setType(NotificationType.TASK_UPDATED);
            updateNotification.setRead(false);
            updateNotification.setCreatedAt(LocalDateTime.now());

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.getNotificationsForUser(1L)).thenReturn(List.of(updateNotification));

            mockMvc.perform(get("/api/notifications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].type").value("TASK_UPDATED"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should handle EVALUATION_COMPLETE notification type")
        void getNotifications_EvaluationComplete() throws Exception {
            Notification evalNotification = new Notification();
            evalNotification.setId(1L);
            evalNotification.setUser(studentUser);
            evalNotification.setTitle("Evaluation Complete");
            evalNotification.setMessage("Your document evaluation is ready");
            evalNotification.setType(NotificationType.EVALUATION_COMPLETE);
            evalNotification.setResourceId(200L);
            evalNotification.setRead(false);
            evalNotification.setCreatedAt(LocalDateTime.now());

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(notificationService.getNotificationsForUser(1L)).thenReturn(List.of(evalNotification));

            mockMvc.perform(get("/api/notifications"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].type").value("EVALUATION_COMPLETE"))
                    .andExpect(jsonPath("$[0].resourceId").value(200));
        }
    }
}
