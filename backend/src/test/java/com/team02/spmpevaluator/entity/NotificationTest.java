package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Notification entity.
 */
@DisplayName("Notification Entity Tests")
class NotificationTest {

    private Notification notification;
    private User user;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        user = new User();
        user.setId(1L);
        user.setUsername("student");
        user.setRole(Role.STUDENT);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            Notification entity = new Notification();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getUser());
            assertNull(entity.getTitle());
            assertFalse(entity.isRead());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime now = LocalDateTime.now();
            Notification entity = new Notification(
                    1L, user, "Task Assigned", "You have a new task",
                    Notification.NotificationType.TASK_ASSIGNED, 10L, false, now);

            assertEquals(1L, entity.getId());
            assertEquals(user, entity.getUser());
            assertEquals("Task Assigned", entity.getTitle());
            assertEquals("You have a new task", entity.getMessage());
            assertEquals(Notification.NotificationType.TASK_ASSIGNED, entity.getType());
            assertEquals(10L, entity.getResourceId());
            assertFalse(entity.isRead());
            assertEquals(now, entity.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            notification.setId(100L);
            assertEquals(100L, notification.getId());
        }

        @Test
        @DisplayName("Should set and get user")
        void setAndGetUser() {
            notification.setUser(user);
            assertEquals(user, notification.getUser());
        }

        @Test
        @DisplayName("Should set and get title")
        void setAndGetTitle() {
            notification.setTitle("Score Override");
            assertEquals("Score Override", notification.getTitle());
        }

        @Test
        @DisplayName("Should set and get message")
        void setAndGetMessage() {
            notification.setMessage("Your score has been updated");
            assertEquals("Your score has been updated", notification.getMessage());
        }

        @Test
        @DisplayName("Should set and get type")
        void setAndGetType() {
            notification.setType(Notification.NotificationType.SCORE_OVERRIDE);
            assertEquals(Notification.NotificationType.SCORE_OVERRIDE, notification.getType());
        }

        @Test
        @DisplayName("Should set and get resourceId")
        void setAndGetResourceId() {
            notification.setResourceId(50L);
            assertEquals(50L, notification.getResourceId());
        }

        @Test
        @DisplayName("Should set and get read status")
        void setAndGetRead() {
            notification.setRead(true);
            assertTrue(notification.isRead());

            notification.setRead(false);
            assertFalse(notification.isRead());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void setAndGetCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            notification.setCreatedAt(now);
            assertEquals(now, notification.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have read default to false")
        void defaultReadFalse() {
            Notification newNotification = new Notification();
            assertFalse(newNotification.isRead());
        }
    }

    @Nested
    @DisplayName("NotificationType Enum Tests")
    class NotificationTypeEnumTests {

        @Test
        @DisplayName("Should have TASK_ASSIGNED type")
        void taskAssignedType() {
            assertEquals("TASK_ASSIGNED", Notification.NotificationType.TASK_ASSIGNED.name());
        }

        @Test
        @DisplayName("Should have TASK_UPDATED type")
        void taskUpdatedType() {
            assertEquals("TASK_UPDATED", Notification.NotificationType.TASK_UPDATED.name());
        }

        @Test
        @DisplayName("Should have SCORE_OVERRIDE type")
        void scoreOverrideType() {
            assertEquals("SCORE_OVERRIDE", Notification.NotificationType.SCORE_OVERRIDE.name());
        }

        @Test
        @DisplayName("Should have EVALUATION_COMPLETE type")
        void evaluationCompleteType() {
            assertEquals("EVALUATION_COMPLETE", Notification.NotificationType.EVALUATION_COMPLETE.name());
        }

        @Test
        @DisplayName("Should have correct number of notification types")
        void notificationTypeCount() {
            assertEquals(4, Notification.NotificationType.values().length);
        }
    }

    @Nested
    @DisplayName("Use Case Tests")
    class UseCaseTests {

        @Test
        @DisplayName("UC 2.6: Should create task assigned notification")
        void taskAssignedNotification() {
            notification.setUser(user);
            notification.setTitle("New Task Assigned");
            notification.setMessage("You have been assigned: Review SPMP Document");
            notification.setType(Notification.NotificationType.TASK_ASSIGNED);
            notification.setResourceId(1L);

            assertEquals(Notification.NotificationType.TASK_ASSIGNED, notification.getType());
            assertNotNull(notification.getResourceId());
        }

        @Test
        @DisplayName("UC 2.8: Should create score override notification")
        void scoreOverrideNotification() {
            notification.setUser(user);
            notification.setTitle("Score Updated");
            notification.setMessage("Your document score has been overridden by professor");
            notification.setType(Notification.NotificationType.SCORE_OVERRIDE);
            notification.setResourceId(5L);

            assertEquals(Notification.NotificationType.SCORE_OVERRIDE, notification.getType());
        }

        @Test
        @DisplayName("UC 2.9: Should create task updated notification")
        void taskUpdatedNotification() {
            notification.setUser(user);
            notification.setTitle("Task Updated");
            notification.setMessage("Task deadline has been changed");
            notification.setType(Notification.NotificationType.TASK_UPDATED);
            notification.setResourceId(3L);

            assertEquals(Notification.NotificationType.TASK_UPDATED, notification.getType());
        }

        @Test
        @DisplayName("UC 2.4: Should create evaluation complete notification")
        void evaluationCompleteNotification() {
            notification.setUser(user);
            notification.setTitle("Evaluation Complete");
            notification.setMessage("Your document has been evaluated. Score: 85%");
            notification.setType(Notification.NotificationType.EVALUATION_COMPLETE);
            notification.setResourceId(2L);

            assertEquals(Notification.NotificationType.EVALUATION_COMPLETE, notification.getType());
        }
    }

    @Nested
    @DisplayName("Read Status Tests")
    class ReadStatusTests {

        @Test
        @DisplayName("Should mark notification as read")
        void markAsRead() {
            notification.setRead(false);
            assertFalse(notification.isRead());

            notification.setRead(true);
            assertTrue(notification.isRead());
        }

        @Test
        @DisplayName("Should mark notification as unread")
        void markAsUnread() {
            notification.setRead(true);
            assertTrue(notification.isRead());

            notification.setRead(false);
            assertFalse(notification.isRead());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            LocalDateTime now = LocalDateTime.now();
            Notification n1 = new Notification(1L, user, "Title", "Message",
                    Notification.NotificationType.TASK_ASSIGNED, 1L, false, now);
            Notification n2 = new Notification(1L, user, "Title", "Message",
                    Notification.NotificationType.TASK_ASSIGNED, 1L, false, now);

            assertEquals(n1, n2);
            assertEquals(n1.hashCode(), n2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different IDs")
        void equals_DifferentIds_ReturnsFalse() {
            Notification n1 = new Notification();
            n1.setId(1L);
            Notification n2 = new Notification();
            n2.setId(2L);

            assertNotEquals(n1, n2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with fields")
        void toString_ContainsFields() {
            notification.setId(1L);
            notification.setTitle("Test Notification");
            notification.setType(Notification.NotificationType.TASK_ASSIGNED);
            notification.setRead(false);

            String result = notification.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("title=Test Notification"));
            assertTrue(result.contains("type=TASK_ASSIGNED"));
            assertTrue(result.contains("read=false"));
        }
    }
}
