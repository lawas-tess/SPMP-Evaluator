package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.Notification;
import com.team02.spmpevaluator.entity.Notification.NotificationType;
import com.team02.spmpevaluator.entity.Task;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.NotificationRepository;
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
 * Unit tests for NotificationService.
 * UC 2.6 Step 5: System saves task and notifies students
 * UC 2.8 Step 5: System saves override and notifies student
 * UC 2.9 Step 5: System saves and notifies affected students
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private User testProfessor;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        // Setup test user (student)
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("student@test.com");
        testUser.setFirstName("Test");
        testUser.setLastName("Student");

        // Setup test professor
        testProfessor = new User();
        testProfessor.setId(2L);
        testProfessor.setEmail("professor@test.com");
        testProfessor.setFirstName("Test");
        testProfessor.setLastName("Professor");

        // Setup test notification
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(testUser);
        testNotification.setTitle("Test Notification");
        testNotification.setMessage("Test Message");
        testNotification.setType(NotificationType.TASK_ASSIGNED);
        testNotification.setResourceId(100L);
        testNotification.setRead(false);
    }

    @Nested
    @DisplayName("Create Notification Tests")
    class CreateNotificationTests {

        @Test
        @DisplayName("Should create notification successfully")
        void createNotification_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            // Act
            Notification result = notificationService.createNotification(
                    1L, "Title", "Message", NotificationType.TASK_ASSIGNED, 100L);

            // Assert
            assertNotNull(result);
            verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void createNotification_UserNotFound() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> notificationService.createNotification(
                            99L, "Title", "Message", NotificationType.TASK_ASSIGNED, 100L));
            assertEquals("User not found", exception.getMessage());
        }

        @Test
        @DisplayName("Should set notification as unread by default")
        void createNotification_DefaultUnread() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Notification result = notificationService.createNotification(
                    1L, "Title", "Message", NotificationType.TASK_ASSIGNED, 100L);

            // Assert
            assertFalse(result.isRead());
        }

        @Test
        @DisplayName("Should set all notification fields correctly")
        void createNotification_AllFieldsSet() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Notification result = notificationService.createNotification(
                    1L, "Test Title", "Test Message", NotificationType.SCORE_OVERRIDE, 200L);

            // Assert
            assertEquals(testUser, result.getUser());
            assertEquals("Test Title", result.getTitle());
            assertEquals("Test Message", result.getMessage());
            assertEquals(NotificationType.SCORE_OVERRIDE, result.getType());
            assertEquals(200L, result.getResourceId());
        }
    }

    @Nested
    @DisplayName("Notify Task Assigned Tests")
    class NotifyTaskAssignedTests {

        @Test
        @DisplayName("Should notify student when task is assigned")
        void notifyTaskAssigned_Success() {
            // Arrange
            Task task = new Task();
            task.setId(1L);
            task.setTitle("Test Task");
            task.setAssignedTo(testUser);
            task.setCreatedBy(testProfessor);
            task.setDeadline(LocalDateTime.now().plusDays(7));

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            // Act
            notificationService.notifyTaskAssigned(task);

            // Assert
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());

            Notification saved = captor.getValue();
            assertEquals(NotificationType.TASK_ASSIGNED, saved.getType());
            assertTrue(saved.getTitle().contains("Test Task"));
        }

        @Test
        @DisplayName("Should not notify when no assignee")
        void notifyTaskAssigned_NoAssignee() {
            // Arrange
            Task task = new Task();
            task.setId(1L);
            task.setTitle("Test Task");
            task.setAssignedTo(null);

            // Act
            notificationService.notifyTaskAssigned(task);

            // Assert
            verify(notificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should include deadline in message")
        void notifyTaskAssigned_IncludesDeadline() {
            // Arrange
            LocalDateTime deadline = LocalDateTime.of(2024, 12, 31, 23, 59);
            Task task = new Task();
            task.setId(1L);
            task.setTitle("Test Task");
            task.setAssignedTo(testUser);
            task.setCreatedBy(testProfessor);
            task.setDeadline(deadline);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            // Act
            notificationService.notifyTaskAssigned(task);

            // Assert
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            assertTrue(captor.getValue().getMessage().contains("2024"));
        }

        @Test
        @DisplayName("Should handle null deadline")
        void notifyTaskAssigned_NullDeadline() {
            // Arrange
            Task task = new Task();
            task.setId(1L);
            task.setTitle("Test Task");
            task.setAssignedTo(testUser);
            task.setCreatedBy(testProfessor);
            task.setDeadline(null);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            // Act
            notificationService.notifyTaskAssigned(task);

            // Assert
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            assertTrue(captor.getValue().getMessage().contains("No deadline"));
        }
    }

    @Nested
    @DisplayName("Notify Task Updated Tests")
    class NotifyTaskUpdatedTests {

        @Test
        @DisplayName("Should notify student when task is updated")
        void notifyTaskUpdated_Success() {
            // Arrange
            Task task = new Task();
            task.setId(1L);
            task.setTitle("Updated Task");
            task.setAssignedTo(testUser);

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            // Act
            notificationService.notifyTaskUpdated(task);

            // Assert
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());

            Notification saved = captor.getValue();
            assertEquals(NotificationType.TASK_UPDATED, saved.getType());
            assertTrue(saved.getTitle().contains("Updated Task"));
        }

        @Test
        @DisplayName("Should not notify when no assignee")
        void notifyTaskUpdated_NoAssignee() {
            // Arrange
            Task task = new Task();
            task.setId(1L);
            task.setTitle("Test Task");
            task.setAssignedTo(null);

            // Act
            notificationService.notifyTaskUpdated(task);

            // Assert
            verify(notificationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Notify Score Override Tests")
    class NotifyScoreOverrideTests {

        @Test
        @DisplayName("Should notify student of score override")
        void notifyScoreOverride_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            // Act
            notificationService.notifyScoreOverride(1L, 100L, 85.5, "Good improvement");

            // Assert
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());

            Notification saved = captor.getValue();
            assertEquals(NotificationType.SCORE_OVERRIDE, saved.getType());
            assertTrue(saved.getMessage().contains("85.50"));
            assertTrue(saved.getMessage().contains("Good improvement"));
        }

        @Test
        @DisplayName("Should handle null justification")
        void notifyScoreOverride_NullJustification() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            // Act
            notificationService.notifyScoreOverride(1L, 100L, 90.0, null);

            // Assert
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            assertFalse(captor.getValue().getMessage().contains("Notes:"));
        }

        @Test
        @DisplayName("Should handle empty justification")
        void notifyScoreOverride_EmptyJustification() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            // Act
            notificationService.notifyScoreOverride(1L, 100L, 90.0, "");

            // Assert
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            assertFalse(captor.getValue().getMessage().contains("Notes:"));
        }
    }

    @Nested
    @DisplayName("Get Notifications Tests")
    class GetNotificationsTests {

        @Test
        @DisplayName("Should get all notifications for user")
        void getNotificationsForUser_Success() {
            // Arrange
            Notification notification2 = new Notification();
            notification2.setId(2L);
            when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                    .thenReturn(Arrays.asList(testNotification, notification2));

            // Act
            List<Notification> result = notificationService.getNotificationsForUser(1L);

            // Assert
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no notifications")
        void getNotificationsForUser_Empty() {
            // Arrange
            when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                    .thenReturn(Collections.emptyList());

            // Act
            List<Notification> result = notificationService.getNotificationsForUser(1L);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should get unread notifications")
        void getUnreadNotifications_Success() {
            // Arrange
            when(notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(1L))
                    .thenReturn(Arrays.asList(testNotification));

            // Act
            List<Notification> result = notificationService.getUnreadNotifications(1L);

            // Assert
            assertEquals(1, result.size());
            assertFalse(result.get(0).isRead());
        }

        @Test
        @DisplayName("Should count unread notifications")
        void countUnreadNotifications_Success() {
            // Arrange
            when(notificationRepository.countByUserIdAndReadFalse(1L)).thenReturn(5L);

            // Act
            long count = notificationService.countUnreadNotifications(1L);

            // Assert
            assertEquals(5L, count);
        }

        @Test
        @DisplayName("Should return zero when no unread notifications")
        void countUnreadNotifications_Zero() {
            // Arrange
            when(notificationRepository.countByUserIdAndReadFalse(1L)).thenReturn(0L);

            // Act
            long count = notificationService.countUnreadNotifications(1L);

            // Assert
            assertEquals(0L, count);
        }
    }

    @Nested
    @DisplayName("Mark As Read Tests")
    class MarkAsReadTests {

        @Test
        @DisplayName("Should mark notification as read")
        void markAsRead_Success() {
            // Arrange
            when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
            when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

            // Act
            notificationService.markAsRead(1L);

            // Assert
            ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
            verify(notificationRepository).save(captor.capture());
            assertTrue(captor.getValue().isRead());
        }

        @Test
        @DisplayName("Should throw exception when notification not found")
        void markAsRead_NotFound() {
            // Arrange
            when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> notificationService.markAsRead(99L));
            assertEquals("Notification not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Mark All As Read Tests")
    class MarkAllAsReadTests {

        @Test
        @DisplayName("Should mark all notifications as read")
        void markAllAsRead_Success() {
            // Arrange
            Notification notification2 = new Notification();
            notification2.setId(2L);
            notification2.setRead(false);

            List<Notification> unread = Arrays.asList(testNotification, notification2);
            when(notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(1L))
                    .thenReturn(unread);

            // Act
            notificationService.markAllAsRead(1L);

            // Assert
            verify(notificationRepository).saveAll(unread);
            assertTrue(testNotification.isRead());
            assertTrue(notification2.isRead());
        }

        @Test
        @DisplayName("Should handle no unread notifications")
        void markAllAsRead_NoUnread() {
            // Arrange
            when(notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(1L))
                    .thenReturn(Collections.emptyList());

            // Act
            notificationService.markAllAsRead(1L);

            // Assert
            verify(notificationRepository).saveAll(Collections.emptyList());
        }
    }

    @Nested
    @DisplayName("Notification Type Tests")
    class NotificationTypeTests {

        @Test
        @DisplayName("Should create notification with TASK_ASSIGNED type")
        void createNotification_TaskAssignedType() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Notification result = notificationService.createNotification(
                    1L, "Task", "Message", NotificationType.TASK_ASSIGNED, 1L);

            // Assert
            assertEquals(NotificationType.TASK_ASSIGNED, result.getType());
        }

        @Test
        @DisplayName("Should create notification with TASK_UPDATED type")
        void createNotification_TaskUpdatedType() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Notification result = notificationService.createNotification(
                    1L, "Task", "Message", NotificationType.TASK_UPDATED, 1L);

            // Assert
            assertEquals(NotificationType.TASK_UPDATED, result.getType());
        }

        @Test
        @DisplayName("Should create notification with SCORE_OVERRIDE type")
        void createNotification_ScoreOverrideType() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Notification result = notificationService.createNotification(
                    1L, "Score", "Message", NotificationType.SCORE_OVERRIDE, 1L);

            // Assert
            assertEquals(NotificationType.SCORE_OVERRIDE, result.getType());
        }

        @Test
        @DisplayName("Should create notification with EVALUATION_COMPLETE type")
        void createNotification_EvaluationCompleteType() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Notification result = notificationService.createNotification(
                    1L, "Evaluation", "Message", NotificationType.EVALUATION_COMPLETE, 1L);

            // Assert
            assertEquals(NotificationType.EVALUATION_COMPLETE, result.getType());
        }
    }
}
