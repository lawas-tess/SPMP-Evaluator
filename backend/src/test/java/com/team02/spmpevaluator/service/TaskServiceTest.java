package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.Task;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.TaskRepository;
import com.team02.spmpevaluator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService.
 * Tests task management and assignment functionality (UC 2.6, 2.9).
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TaskService taskService;

    private User testStudent;
    private User testProfessor;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setEmail("student@test.com");

        testProfessor = new User();
        testProfessor.setId(2L);
        testProfessor.setEmail("professor@test.com");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Complete SPMP Document");
        testTask.setDescription("Submit SPMP document for review");
        testTask.setDeadline(LocalDateTime.now().plusDays(7));
        testTask.setPriority(Task.Priority.HIGH);
        testTask.setAssignedTo(testStudent);
        testTask.setCreatedBy(testProfessor);
        testTask.setStatus(Task.TaskStatus.PENDING);
        testTask.setCompleted(false);
    }

    @Nested
    @DisplayName("Create Task Tests (UC 2.6)")
    class CreateTaskTests {

        @Test
        @DisplayName("Should create task successfully")
        void createTask_ValidInput_CreatesTask() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(taskRepository.save(any(Task.class))).thenReturn(testTask);

            Task result = taskService.createTask(
                    "Complete SPMP Document",
                    "Submit SPMP document for review",
                    LocalDateTime.now().plusDays(7),
                    Task.Priority.HIGH,
                    1L,
                    2L);

            assertNotNull(result);
            assertEquals("Complete SPMP Document", result.getTitle());
            assertEquals(Task.TaskStatus.PENDING, result.getStatus());
            assertFalse(result.isCompleted());
        }

        @Test
        @DisplayName("Should notify student on task creation")
        void createTask_ValidInput_NotifiesStudent() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(taskRepository.save(any(Task.class))).thenReturn(testTask);

            taskService.createTask(
                    "Task Title",
                    "Description",
                    LocalDateTime.now().plusDays(7),
                    Task.Priority.MEDIUM,
                    1L,
                    2L);

            verify(notificationService).notifyTaskAssigned(testTask);
        }

        @Test
        @DisplayName("Should throw exception when assigned user not found")
        void createTask_AssignedUserNotFound_ThrowsException() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> taskService.createTask("Title", "Desc", LocalDateTime.now(), Task.Priority.LOW, 999L, 2L));
        }

        @Test
        @DisplayName("Should throw exception when creator user not found")
        void createTask_CreatorNotFound_ThrowsException() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> taskService.createTask("Title", "Desc", LocalDateTime.now(), Task.Priority.LOW, 1L, 999L));
        }
    }

    @Nested
    @DisplayName("Get Task by ID Tests")
    class GetTaskByIdTests {

        @Test
        @DisplayName("Should return task when found")
        void getTaskById_ExistingTask_ReturnsTask() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

            Optional<Task> result = taskService.getTaskById(1L);

            assertTrue(result.isPresent());
            assertEquals(testTask.getId(), result.get().getId());
        }

        @Test
        @DisplayName("Should return empty when task not found")
        void getTaskById_NonExistentTask_ReturnsEmpty() {
            when(taskRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<Task> result = taskService.getTaskById(999L);

            assertFalse(result.isPresent());
        }
    }

    @Nested
    @DisplayName("Get Tasks by User Tests")
    class GetTasksByUserTests {

        @Test
        @DisplayName("Should return tasks assigned to user")
        void getTasksByAssignedUser_ValidUser_ReturnsTasks() {
            when(taskRepository.findByAssignedTo_Id(1L)).thenReturn(Arrays.asList(testTask));

            List<Task> result = taskService.getTasksByAssignedUser(1L);

            assertEquals(1, result.size());
            assertEquals(testStudent.getId(), result.get(0).getAssignedTo().getId());
        }

        @Test
        @DisplayName("Should return tasks created by user")
        void getTasksByCreator_ValidUser_ReturnsTasks() {
            when(taskRepository.findByCreatedBy_Id(2L)).thenReturn(Arrays.asList(testTask));

            List<Task> result = taskService.getTasksByCreator(2L);

            assertEquals(1, result.size());
            assertEquals(testProfessor.getId(), result.get(0).getCreatedBy().getId());
        }

        @Test
        @DisplayName("Should return incomplete tasks for user")
        void getIncompleteTasksForUser_ValidUser_ReturnsIncompleteTasks() {
            when(taskRepository.findByAssignedTo_IdAndCompletedFalse(1L))
                    .thenReturn(Arrays.asList(testTask));

            List<Task> result = taskService.getIncompleteTasksForUser(1L);

            assertEquals(1, result.size());
            assertFalse(result.get(0).isCompleted());
        }
    }

    @Nested
    @DisplayName("Get Tasks by Status Tests")
    class GetTasksByStatusTests {

        @Test
        @DisplayName("Should return pending tasks")
        void getTasksByStatus_Pending_ReturnsPendingTasks() {
            when(taskRepository.findByStatus(Task.TaskStatus.PENDING))
                    .thenReturn(Arrays.asList(testTask));

            List<Task> result = taskService.getTasksByStatus(Task.TaskStatus.PENDING);

            assertEquals(1, result.size());
            assertEquals(Task.TaskStatus.PENDING, result.get(0).getStatus());
        }

        @Test
        @DisplayName("Should return completed tasks")
        void getTasksByStatus_Completed_ReturnsCompletedTasks() {
            testTask.setStatus(Task.TaskStatus.COMPLETED);
            when(taskRepository.findByStatus(Task.TaskStatus.COMPLETED))
                    .thenReturn(Arrays.asList(testTask));

            List<Task> result = taskService.getTasksByStatus(Task.TaskStatus.COMPLETED);

            assertEquals(1, result.size());
            assertEquals(Task.TaskStatus.COMPLETED, result.get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("Update Task Status Tests")
    class UpdateTaskStatusTests {

        @Test
        @DisplayName("Should update task status")
        void updateTaskStatus_ValidTask_UpdatesStatus() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
            when(taskRepository.save(any(Task.class))).thenReturn(testTask);

            Task result = taskService.updateTaskStatus(1L, Task.TaskStatus.IN_PROGRESS, false);

            assertEquals(Task.TaskStatus.IN_PROGRESS, result.getStatus());
            assertFalse(result.isCompleted());
        }

        @Test
        @DisplayName("Should set completion date when completed")
        void updateTaskStatus_MarkCompleted_SetsCompletionDate() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
            when(taskRepository.save(any(Task.class))).thenReturn(testTask);

            Task result = taskService.updateTaskStatus(1L, Task.TaskStatus.COMPLETED, true);

            assertTrue(result.isCompleted());
            assertNotNull(result.getCompletionDate());
            assertEquals(LocalDate.now(), result.getCompletionDate());
        }

        @Test
        @DisplayName("Should throw exception when task not found")
        void updateTaskStatus_TaskNotFound_ThrowsException() {
            when(taskRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> taskService.updateTaskStatus(999L, Task.TaskStatus.COMPLETED, true));
        }
    }

    @Nested
    @DisplayName("Update Task Tests (UC 2.9)")
    class UpdateTaskTests {

        @Test
        @DisplayName("Should update task details")
        void updateTask_ValidInput_UpdatesTask() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
            when(taskRepository.save(any(Task.class))).thenReturn(testTask);

            Task result = taskService.updateTask(
                    1L,
                    "Updated Title",
                    "Updated Description",
                    LocalDateTime.now().plusDays(14),
                    Task.Priority.MEDIUM,
                    null);

            assertEquals("Updated Title", result.getTitle());
            assertEquals("Updated Description", result.getDescription());
            assertEquals(Task.Priority.MEDIUM, result.getPriority());
        }

        @Test
        @DisplayName("Should update assigned user if provided")
        void updateTask_WithNewAssignee_UpdatesAssignee() {
            User newStudent = new User();
            newStudent.setId(3L);
            newStudent.setEmail("newstudent@test.com");

            when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
            when(userRepository.findById(3L)).thenReturn(Optional.of(newStudent));
            when(taskRepository.save(any(Task.class))).thenReturn(testTask);

            Task result = taskService.updateTask(1L, "Title", "Desc", LocalDateTime.now(), Task.Priority.LOW, 3L);

            assertEquals(newStudent, result.getAssignedTo());
        }

        @Test
        @DisplayName("Should notify student on task update")
        void updateTask_ValidInput_NotifiesStudent() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
            when(taskRepository.save(any(Task.class))).thenReturn(testTask);

            taskService.updateTask(1L, "Title", "Desc", LocalDateTime.now(), Task.Priority.LOW, null);

            verify(notificationService).notifyTaskUpdated(testTask);
        }

        @Test
        @DisplayName("Should throw exception when task not found")
        void updateTask_TaskNotFound_ThrowsException() {
            when(taskRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> taskService.updateTask(999L, "Title", "Desc", LocalDateTime.now(), Task.Priority.LOW, null));
        }
    }

    @Nested
    @DisplayName("Complete Task Tests")
    class CompleteTaskTests {

        @Test
        @DisplayName("Should mark task as completed")
        void completeTask_ValidTask_CompletesTask() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
            when(taskRepository.save(any(Task.class))).thenReturn(testTask);

            Task result = taskService.completeTask(1L);

            assertTrue(result.isCompleted());
            assertEquals(Task.TaskStatus.COMPLETED, result.getStatus());
            assertEquals(LocalDate.now(), result.getCompletionDate());
        }

        @Test
        @DisplayName("Should throw exception when task not found")
        void completeTask_TaskNotFound_ThrowsException() {
            when(taskRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> taskService.completeTask(999L));
        }
    }

    @Nested
    @DisplayName("Delete Task Tests")
    class DeleteTaskTests {

        @Test
        @DisplayName("Should delete task successfully")
        void deleteTask_ExistingTask_DeletesTask() {
            when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
            doNothing().when(taskRepository).delete(testTask);

            assertDoesNotThrow(() -> taskService.deleteTask(1L));
            verify(taskRepository).delete(testTask);
        }

        @Test
        @DisplayName("Should throw exception when task not found")
        void deleteTask_TaskNotFound_ThrowsException() {
            when(taskRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(999L));
        }
    }

    @Nested
    @DisplayName("Get Overdue Tasks Tests")
    class GetOverdueTasksTests {

        @Test
        @DisplayName("Should return overdue tasks")
        void getOverdueTasks_HasOverdue_ReturnsOverdueTasks() {
            testTask.setDeadline(LocalDateTime.now().minusDays(1));
            when(taskRepository.findByDeadlineBeforeAndCompletedFalse(any(LocalDate.class)))
                    .thenReturn(Arrays.asList(testTask));

            List<Task> result = taskService.getOverdueTasks();

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should return empty list when no overdue tasks")
        void getOverdueTasks_NoOverdue_ReturnsEmptyList() {
            when(taskRepository.findByDeadlineBeforeAndCompletedFalse(any(LocalDate.class)))
                    .thenReturn(List.of());

            List<Task> result = taskService.getOverdueTasks();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Task Priority Tests")
    class TaskPriorityTests {

        @Test
        @DisplayName("Should handle HIGH priority")
        void createTask_HighPriority_SetsCorrectPriority() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task result = taskService.createTask(
                    "Urgent Task", "Description", LocalDateTime.now(), Task.Priority.HIGH, 1L, 2L);

            assertEquals(Task.Priority.HIGH, result.getPriority());
        }

        @Test
        @DisplayName("Should handle MEDIUM priority")
        void createTask_MediumPriority_SetsCorrectPriority() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task result = taskService.createTask(
                    "Normal Task", "Description", LocalDateTime.now(), Task.Priority.MEDIUM, 1L, 2L);

            assertEquals(Task.Priority.MEDIUM, result.getPriority());
        }

        @Test
        @DisplayName("Should handle LOW priority")
        void createTask_LowPriority_SetsCorrectPriority() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

            Task result = taskService.createTask(
                    "Low Priority Task", "Description", LocalDateTime.now(), Task.Priority.LOW, 1L, 2L);

            assertEquals(Task.Priority.LOW, result.getPriority());
        }
    }
}
