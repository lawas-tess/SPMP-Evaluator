package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Task entity.
 */
@DisplayName("Task Entity Tests")
class TaskTest {

    private Task task;
    private User student;
    private User professor;

    @BeforeEach
    void setUp() {
        task = new Task();

        student = new User();
        student.setId(1L);
        student.setUsername("student");
        student.setRole(Role.STUDENT);

        professor = new User();
        professor.setId(2L);
        professor.setUsername("professor");
        professor.setRole(Role.PROFESSOR);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            Task entity = new Task();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getTitle());
            assertFalse(entity.isCompleted());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime now = LocalDateTime.now();
            LocalDate today = LocalDate.now();
            Task entity = new Task(
                    1L, "Submit SPMP Document", "Complete and submit final SPMP",
                    now.plusDays(7), true, today, student, professor,
                    Task.Priority.HIGH, Task.TaskStatus.COMPLETED, now, now);

            assertEquals(1L, entity.getId());
            assertEquals("Submit SPMP Document", entity.getTitle());
            assertEquals("Complete and submit final SPMP", entity.getDescription());
            assertTrue(entity.isCompleted());
            assertEquals(Task.Priority.HIGH, entity.getPriority());
            assertEquals(Task.TaskStatus.COMPLETED, entity.getStatus());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testId() {
            task.setId(100L);
            assertEquals(100L, task.getId());
        }

        @Test
        @DisplayName("Should set and get title")
        void testTitle() {
            task.setTitle("Review SPMP Document");
            assertEquals("Review SPMP Document", task.getTitle());
        }

        @Test
        @DisplayName("Should set and get description")
        void testDescription() {
            task.setDescription("Review and provide feedback on the submitted SPMP document");
            assertEquals("Review and provide feedback on the submitted SPMP document", task.getDescription());
        }

        @Test
        @DisplayName("Should set and get deadline")
        void testDeadline() {
            LocalDateTime deadline = LocalDateTime.now().plusDays(7);
            task.setDeadline(deadline);
            assertEquals(deadline, task.getDeadline());
        }

        @Test
        @DisplayName("Should set and get completed")
        void testCompleted() {
            task.setCompleted(true);
            assertTrue(task.isCompleted());
            task.setCompleted(false);
            assertFalse(task.isCompleted());
        }

        @Test
        @DisplayName("Should set and get completionDate")
        void testCompletionDate() {
            LocalDate today = LocalDate.now();
            task.setCompletionDate(today);
            assertEquals(today, task.getCompletionDate());
        }

        @Test
        @DisplayName("Should set and get assignedTo")
        void testAssignedTo() {
            task.setAssignedTo(student);
            assertEquals(student, task.getAssignedTo());
        }

        @Test
        @DisplayName("Should set and get createdBy")
        void testCreatedBy() {
            task.setCreatedBy(professor);
            assertEquals(professor, task.getCreatedBy());
        }

        @Test
        @DisplayName("Should set and get priority")
        void testPriority() {
            task.setPriority(Task.Priority.CRITICAL);
            assertEquals(Task.Priority.CRITICAL, task.getPriority());
        }

        @Test
        @DisplayName("Should set and get status")
        void testStatus() {
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
            assertEquals(Task.TaskStatus.IN_PROGRESS, task.getStatus());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void testCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            task.setCreatedAt(now);
            assertEquals(now, task.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get updatedAt")
        void testUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            task.setUpdatedAt(now);
            assertEquals(now, task.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have completed default to false")
        void completedDefaultFalse() {
            Task entity = new Task();
            assertFalse(entity.isCompleted());
        }

        @Test
        @DisplayName("Should have priority default to MEDIUM")
        void priorityDefaultMedium() {
            Task entity = new Task();
            assertEquals(Task.Priority.MEDIUM, entity.getPriority());
        }

        @Test
        @DisplayName("Should have status default to PENDING")
        void statusDefaultPending() {
            Task entity = new Task();
            assertEquals(Task.TaskStatus.PENDING, entity.getStatus());
        }
    }

    @Nested
    @DisplayName("Priority Enum Tests")
    class PriorityEnumTests {

        @Test
        @DisplayName("Should have LOW priority")
        void lowPriority() {
            assertEquals("LOW", Task.Priority.LOW.name());
        }

        @Test
        @DisplayName("Should have MEDIUM priority")
        void mediumPriority() {
            assertEquals("MEDIUM", Task.Priority.MEDIUM.name());
        }

        @Test
        @DisplayName("Should have HIGH priority")
        void highPriority() {
            assertEquals("HIGH", Task.Priority.HIGH.name());
        }

        @Test
        @DisplayName("Should have CRITICAL priority")
        void criticalPriority() {
            assertEquals("CRITICAL", Task.Priority.CRITICAL.name());
        }

        @Test
        @DisplayName("Should have exactly 4 priorities")
        void priorityCount() {
            assertEquals(4, Task.Priority.values().length);
        }
    }

    @Nested
    @DisplayName("TaskStatus Enum Tests")
    class TaskStatusEnumTests {

        @Test
        @DisplayName("Should have PENDING status")
        void pendingStatus() {
            assertEquals("PENDING", Task.TaskStatus.PENDING.name());
        }

        @Test
        @DisplayName("Should have IN_PROGRESS status")
        void inProgressStatus() {
            assertEquals("IN_PROGRESS", Task.TaskStatus.IN_PROGRESS.name());
        }

        @Test
        @DisplayName("Should have COMPLETED status")
        void completedStatus() {
            assertEquals("COMPLETED", Task.TaskStatus.COMPLETED.name());
        }

        @Test
        @DisplayName("Should have CANCELLED status")
        void cancelledStatus() {
            assertEquals("CANCELLED", Task.TaskStatus.CANCELLED.name());
        }

        @Test
        @DisplayName("Should have exactly 4 statuses")
        void statusCount() {
            assertEquals(4, Task.TaskStatus.values().length);
        }
    }

    @Nested
    @DisplayName("Task Workflow Tests")
    class TaskWorkflowTests {

        @Test
        @DisplayName("Should transition from PENDING to IN_PROGRESS")
        void pendingToInProgress() {
            task.setStatus(Task.TaskStatus.PENDING);
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
            assertEquals(Task.TaskStatus.IN_PROGRESS, task.getStatus());
        }

        @Test
        @DisplayName("Should transition from IN_PROGRESS to COMPLETED")
        void inProgressToCompleted() {
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setCompleted(true);
            task.setCompletionDate(LocalDate.now());

            assertEquals(Task.TaskStatus.COMPLETED, task.getStatus());
            assertTrue(task.isCompleted());
            assertNotNull(task.getCompletionDate());
        }

        @Test
        @DisplayName("Should transition to CANCELLED")
        void taskCancelled() {
            task.setStatus(Task.TaskStatus.PENDING);
            task.setStatus(Task.TaskStatus.CANCELLED);
            assertEquals(Task.TaskStatus.CANCELLED, task.getStatus());
        }
    }

    @Nested
    @DisplayName("Task Use Case Tests (UC 2.4)")
    class UseCaseTests {

        @Test
        @DisplayName("Professor assigns task to student")
        void professorAssignsTask() {
            task.setTitle("Complete IEEE 1058 Sections");
            task.setDescription("Add all required IEEE 1058 sections to your SPMP document");
            task.setDeadline(LocalDateTime.now().plusDays(3));
            task.setAssignedTo(student);
            task.setCreatedBy(professor);
            task.setPriority(Task.Priority.HIGH);
            task.setStatus(Task.TaskStatus.PENDING);

            assertEquals(student, task.getAssignedTo());
            assertEquals(professor, task.getCreatedBy());
            assertEquals(Task.Priority.HIGH, task.getPriority());
        }

        @Test
        @DisplayName("Student completes task")
        void studentCompletesTask() {
            task.setAssignedTo(student);
            task.setStatus(Task.TaskStatus.IN_PROGRESS);

            // Student completes
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setCompleted(true);
            task.setCompletionDate(LocalDate.now());

            assertTrue(task.isCompleted());
            assertEquals(Task.TaskStatus.COMPLETED, task.getStatus());
        }
    }

    @Nested
    @DisplayName("Deadline Tests")
    class DeadlineTests {

        @Test
        @DisplayName("Should handle future deadline")
        void futureDeadline() {
            LocalDateTime future = LocalDateTime.now().plusWeeks(2);
            task.setDeadline(future);
            assertTrue(task.getDeadline().isAfter(LocalDateTime.now()));
        }

        @Test
        @DisplayName("Should handle past deadline (overdue)")
        void pastDeadline() {
            LocalDateTime past = LocalDateTime.now().minusDays(1);
            task.setDeadline(past);
            assertTrue(task.getDeadline().isBefore(LocalDateTime.now()));
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same id")
        void equalsWithSameId() {
            Task task1 = new Task();
            task1.setId(1L);
            task1.setTitle("Task 1");

            Task task2 = new Task();
            task2.setId(1L);
            task2.setTitle("Task 1");

            assertEquals(task1, task2);
            assertEquals(task1.hashCode(), task2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different ids")
        void notEqualsWithDifferentIds() {
            Task task1 = new Task();
            task1.setId(1L);

            Task task2 = new Task();
            task2.setId(2L);

            assertNotEquals(task1, task2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void toStringContainsFields() {
            task.setId(1L);
            task.setTitle("Test Task");
            task.setPriority(Task.Priority.HIGH);

            String str = task.toString();
            assertNotNull(str);
            assertTrue(str.contains("1"));
        }
    }
}
