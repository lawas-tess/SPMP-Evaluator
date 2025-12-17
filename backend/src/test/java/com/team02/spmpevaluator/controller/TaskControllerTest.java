package com.team02.spmpevaluator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team02.spmpevaluator.dto.TaskDTO;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.Task;
import com.team02.spmpevaluator.entity.User;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TaskController Integration Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @MockBean
    private AuditLogService auditLogService;

    private User professorUser;
    private User studentUser;
    private Task task1;
    private Task task2;
    private Task overdueTask;

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

        // Setup task 1
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Submit SPMP Document");
        task1.setDescription("Submit the Software Project Management Plan");
        task1.setDeadline(LocalDateTime.now().plusDays(7));
        task1.setPriority(Task.Priority.HIGH);
        task1.setStatus(Task.TaskStatus.IN_PROGRESS);
        task1.setCompleted(false);
        task1.setAssignedTo(studentUser);
        task1.setCreatedBy(professorUser);
        task1.setCreatedAt(LocalDateTime.now());

        // Setup task 2
        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Review feedback");
        task2.setDescription("Review professor feedback on document");
        task2.setDeadline(LocalDateTime.now().plusDays(3));
        task2.setPriority(Task.Priority.MEDIUM);
        task2.setStatus(Task.TaskStatus.PENDING);
        task2.setCompleted(false);
        task2.setAssignedTo(studentUser);
        task2.setCreatedBy(professorUser);
        task2.setCreatedAt(LocalDateTime.now());

        // Setup overdue task
        overdueTask = new Task();
        overdueTask.setId(3L);
        overdueTask.setTitle("Overdue task");
        overdueTask.setDescription("This task is overdue");
        overdueTask.setDeadline(LocalDateTime.now().minusDays(2));
        overdueTask.setPriority(Task.Priority.HIGH);
        overdueTask.setStatus(Task.TaskStatus.PENDING);
        overdueTask.setCompleted(false);
        overdueTask.setAssignedTo(studentUser);
        overdueTask.setCreatedBy(professorUser);
        overdueTask.setCreatedAt(LocalDateTime.now().minusDays(10));
    }

    @Nested
    @DisplayName("POST /api/tasks/create - Create Task")
    class CreateTaskTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should create task successfully for professor")
        void createTask_Success() throws Exception {
            TaskDTO requestDTO = new TaskDTO();
            requestDTO.setTitle("New Task");
            requestDTO.setDescription("Task description");
            requestDTO.setDeadline(LocalDateTime.now().plusDays(5));
            requestDTO.setPriority("HIGH");
            requestDTO.setAssignedToUserId(2L);

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(taskService.createTask(anyString(), anyString(), any(LocalDateTime.class),
                    any(Task.Priority.class), anyLong(), anyLong())).thenReturn(task1);

            mockMvc.perform(post("/api/tasks/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Submit SPMP Document"));

            verify(taskService).createTask(anyString(), anyString(), any(LocalDateTime.class),
                    any(Task.Priority.class), anyLong(), anyLong());
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should deny task creation for students")
        void createTask_ForbiddenForStudent() throws Exception {
            TaskDTO requestDTO = new TaskDTO();
            requestDTO.setTitle("New Task");
            requestDTO.setDescription("Task description");
            requestDTO.setPriority("HIGH");
            requestDTO.setAssignedToUserId(2L);

            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));

            mockMvc.perform(post("/api/tasks/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isForbidden())
                    .andExpect(
                            content().string(org.hamcrest.Matchers.containsString("Only professors can create tasks")));
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should fail with invalid priority")
        void createTask_InvalidPriority() throws Exception {
            TaskDTO requestDTO = new TaskDTO();
            requestDTO.setTitle("New Task");
            requestDTO.setDescription("Task description");
            requestDTO.setPriority("INVALID");
            requestDTO.setAssignedToUserId(2L);

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));

            mockMvc.perform(post("/api/tasks/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should require authentication")
        void createTask_RequiresAuth() throws Exception {
            TaskDTO requestDTO = new TaskDTO();
            requestDTO.setTitle("New Task");

            mockMvc.perform(post("/api/tasks/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/tasks/my - Get My Tasks")
    class GetMyTasksTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get my assigned tasks")
        void getMyTasks_Success() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(taskService.getTasksByAssignedUser(2L)).thenReturn(List.of(task1, task2));

            mockMvc.perform(get("/api/tasks/my"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].title").value("Submit SPMP Document"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].title").value("Review feedback"));

            verify(taskService).getTasksByAssignedUser(2L);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should return empty list when no tasks")
        void getMyTasks_Empty() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(taskService.getTasksByAssignedUser(2L)).thenReturn(List.of());

            mockMvc.perform(get("/api/tasks/my"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(username = "unknown")
        @DisplayName("Should fail when user not found")
        void getMyTasks_UserNotFound() throws Exception {
            when(userService.findByUsername("unknown")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/tasks/my"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to retrieve tasks")));
        }
    }

    @Nested
    @DisplayName("GET /api/tasks/incomplete - Get Incomplete Tasks")
    class GetIncompleteTasksTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get incomplete tasks")
        void getIncompleteTasks_Success() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(taskService.getIncompleteTasksForUser(2L)).thenReturn(List.of(task1, task2));

            mockMvc.perform(get("/api/tasks/incomplete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].completed").value(false))
                    .andExpect(jsonPath("$[1].completed").value(false));

            verify(taskService).getIncompleteTasksForUser(2L);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should return empty when all tasks completed")
        void getIncompleteTasks_AllCompleted() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(taskService.getIncompleteTasksForUser(2L)).thenReturn(List.of());

            mockMvc.perform(get("/api/tasks/incomplete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/tasks/created - Get Created Tasks")
    class GetCreatedTasksTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should get tasks created by current user")
        void getCreatedTasks_Success() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(taskService.getTasksByCreator(1L)).thenReturn(List.of(task1, task2));

            mockMvc.perform(get("/api/tasks/created"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].createdByUserId").value(1))
                    .andExpect(jsonPath("$[1].createdByUserId").value(1));

            verify(taskService).getTasksByCreator(1L);
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should return empty when no tasks created")
        void getCreatedTasks_Empty() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(taskService.getTasksByCreator(1L)).thenReturn(List.of());

            mockMvc.perform(get("/api/tasks/created"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/tasks/{taskId} - Get Task By ID")
    class GetTaskByIdTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should get task by ID and log view")
        void getTask_Success() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(taskService.getTaskById(1L)).thenReturn(Optional.of(task1));
            doNothing().when(auditLogService).logTaskView(anyLong(), anyLong(), anyString());

            mockMvc.perform(get("/api/tasks/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("Submit SPMP Document"));

            verify(auditLogService).logTaskView(eq(2L), eq(1L), anyString());
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should fail when task not found")
        void getTask_NotFound() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));
            when(taskService.getTaskById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/tasks/999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Task not found")));
        }

        @Test
        @DisplayName("Should require authentication")
        void getTask_RequiresAuth() throws Exception {
            mockMvc.perform(get("/api/tasks/1"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("PUT /api/tasks/{taskId} - Update Task")
    class UpdateTaskTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should update task successfully")
        void updateTask_Success() throws Exception {
            TaskDTO updateDTO = new TaskDTO();
            updateDTO.setTitle("Updated Task");
            updateDTO.setDescription("Updated description");
            updateDTO.setDeadline(LocalDateTime.now().plusDays(10));
            updateDTO.setPriority("MEDIUM");
            updateDTO.setAssignedToUserId(2L);

            Task updatedTask = new Task();
            updatedTask.setId(1L);
            updatedTask.setTitle("Updated Task");
            updatedTask.setDescription("Updated description");
            updatedTask.setPriority(Task.Priority.MEDIUM);
            updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);
            updatedTask.setCompleted(false);
            updatedTask.setAssignedTo(studentUser);
            updatedTask.setCreatedBy(professorUser);
            updatedTask.setCreatedAt(LocalDateTime.now());

            when(taskService.updateTask(anyLong(), anyString(), anyString(),
                    any(LocalDateTime.class), any(Task.Priority.class), anyLong())).thenReturn(updatedTask);

            mockMvc.perform(put("/api/tasks/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Task"))
                    .andExpect(jsonPath("$.priority").value("MEDIUM"));

            verify(taskService).updateTask(anyLong(), anyString(), anyString(),
                    any(LocalDateTime.class), any(Task.Priority.class), anyLong());
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should fail when task not found")
        void updateTask_NotFound() throws Exception {
            TaskDTO updateDTO = new TaskDTO();
            updateDTO.setTitle("Updated Task");
            updateDTO.setPriority("MEDIUM");
            updateDTO.setAssignedToUserId(2L);

            when(taskService.updateTask(anyLong(), anyString(), any(),
                    any(), any(Task.Priority.class), anyLong()))
                    .thenThrow(new RuntimeException("Task not found"));

            mockMvc.perform(put("/api/tasks/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to update task")));
        }
    }

    @Nested
    @DisplayName("PUT /api/tasks/{taskId}/status - Update Task Status")
    class UpdateTaskStatusTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should update task status")
        void updateTaskStatus_Success() throws Exception {
            Task updatedTask = new Task();
            updatedTask.setId(1L);
            updatedTask.setTitle("Submit SPMP Document");
            updatedTask.setStatus(Task.TaskStatus.COMPLETED);
            updatedTask.setCompleted(true);
            updatedTask.setAssignedTo(studentUser);
            updatedTask.setCreatedBy(professorUser);
            updatedTask.setCreatedAt(LocalDateTime.now());

            when(taskService.updateTaskStatus(1L, Task.TaskStatus.COMPLETED, true)).thenReturn(updatedTask);

            mockMvc.perform(put("/api/tasks/1/status")
                    .param("status", "COMPLETED")
                    .param("completed", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("COMPLETED"))
                    .andExpect(jsonPath("$.completed").value(true));

            verify(taskService).updateTaskStatus(1L, Task.TaskStatus.COMPLETED, true);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should fail with invalid status")
        void updateTaskStatus_InvalidStatus() throws Exception {
            mockMvc.perform(put("/api/tasks/1/status")
                    .param("status", "INVALID_STATUS"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid status")));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should default completed to false")
        void updateTaskStatus_DefaultCompleted() throws Exception {
            Task updatedTask = new Task();
            updatedTask.setId(1L);
            updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);
            updatedTask.setCompleted(false);
            updatedTask.setAssignedTo(studentUser);
            updatedTask.setCreatedBy(professorUser);
            updatedTask.setCreatedAt(LocalDateTime.now());

            when(taskService.updateTaskStatus(1L, Task.TaskStatus.IN_PROGRESS, false)).thenReturn(updatedTask);

            mockMvc.perform(put("/api/tasks/1/status")
                    .param("status", "IN_PROGRESS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.completed").value(false));

            verify(taskService).updateTaskStatus(1L, Task.TaskStatus.IN_PROGRESS, false);
        }
    }

    @Nested
    @DisplayName("PUT /api/tasks/{taskId}/complete - Complete Task")
    class CompleteTaskTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should mark task as completed")
        void completeTask_Success() throws Exception {
            Task completedTask = new Task();
            completedTask.setId(1L);
            completedTask.setTitle("Submit SPMP Document");
            completedTask.setStatus(Task.TaskStatus.COMPLETED);
            completedTask.setCompleted(true);
            completedTask.setCompletionDate(LocalDate.now());
            completedTask.setAssignedTo(studentUser);
            completedTask.setCreatedBy(professorUser);
            completedTask.setCreatedAt(LocalDateTime.now());

            when(taskService.completeTask(1L)).thenReturn(completedTask);

            mockMvc.perform(put("/api/tasks/1/complete"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.completed").value(true))
                    .andExpect(jsonPath("$.status").value("COMPLETED"));

            verify(taskService).completeTask(1L);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should fail when task not found")
        void completeTask_NotFound() throws Exception {
            when(taskService.completeTask(999L)).thenThrow(new RuntimeException("Task not found"));

            mockMvc.perform(put("/api/tasks/999/complete"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to complete task")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/tasks/{taskId} - Delete Task")
    class DeleteTaskTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should delete task successfully for professor")
        void deleteTask_Success() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            doNothing().when(taskService).deleteTask(1L);

            mockMvc.perform(delete("/api/tasks/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Task deleted successfully"));

            verify(taskService).deleteTask(1L);
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should deny task deletion for students")
        void deleteTask_ForbiddenForStudent() throws Exception {
            when(userService.findByUsername("student")).thenReturn(Optional.of(studentUser));

            mockMvc.perform(delete("/api/tasks/1"))
                    .andExpect(status().isForbidden())
                    .andExpect(
                            content().string(org.hamcrest.Matchers.containsString("Only professors can delete tasks")));

            verify(taskService, never()).deleteTask(anyLong());
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should fail when task not found")
        void deleteTask_NotFound() throws Exception {
            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            doThrow(new RuntimeException("Task not found")).when(taskService).deleteTask(999L);

            mockMvc.perform(delete("/api/tasks/999"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to delete task")));
        }

        @Test
        @DisplayName("Should require authentication")
        void deleteTask_RequiresAuth() throws Exception {
            mockMvc.perform(delete("/api/tasks/1"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("GET /api/tasks/overdue - Get Overdue Tasks")
    class GetOverdueTasksTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should get overdue tasks")
        void getOverdueTasks_Success() throws Exception {
            when(taskService.getOverdueTasks()).thenReturn(List.of(overdueTask));

            mockMvc.perform(get("/api/tasks/overdue"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(3))
                    .andExpect(jsonPath("$[0].title").value("Overdue task"));

            verify(taskService).getOverdueTasks();
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should return empty list when no overdue tasks")
        void getOverdueTasks_Empty() throws Exception {
            when(taskService.getOverdueTasks()).thenReturn(List.of());

            mockMvc.perform(get("/api/tasks/overdue"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Student can also view overdue tasks")
        void getOverdueTasks_StudentAccess() throws Exception {
            when(taskService.getOverdueTasks()).thenReturn(List.of(overdueTask));

            mockMvc.perform(get("/api/tasks/overdue"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should handle service exception")
        void getOverdueTasks_Exception() throws Exception {
            when(taskService.getOverdueTasks()).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/tasks/overdue"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(
                            content().string(org.hamcrest.Matchers.containsString("Failed to retrieve overdue tasks")));
        }
    }

    @Nested
    @DisplayName("Task Priority Tests")
    class TaskPriorityTests {

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should handle HIGH priority")
        void createTask_HighPriority() throws Exception {
            TaskDTO requestDTO = new TaskDTO();
            requestDTO.setTitle("High Priority Task");
            requestDTO.setDescription("Urgent task");
            requestDTO.setPriority("HIGH");
            requestDTO.setAssignedToUserId(2L);

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(taskService.createTask(anyString(), anyString(), any(),
                    eq(Task.Priority.HIGH), anyLong(), anyLong())).thenReturn(task1);

            mockMvc.perform(post("/api/tasks/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated());

            verify(taskService).createTask(anyString(), anyString(), any(),
                    eq(Task.Priority.HIGH), anyLong(), anyLong());
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should handle MEDIUM priority")
        void createTask_MediumPriority() throws Exception {
            TaskDTO requestDTO = new TaskDTO();
            requestDTO.setTitle("Medium Priority Task");
            requestDTO.setPriority("MEDIUM");
            requestDTO.setAssignedToUserId(2L);

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(taskService.createTask(anyString(), any(), any(),
                    eq(Task.Priority.MEDIUM), anyLong(), anyLong())).thenReturn(task2);

            mockMvc.perform(post("/api/tasks/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated());

            verify(taskService).createTask(anyString(), any(), any(),
                    eq(Task.Priority.MEDIUM), anyLong(), anyLong());
        }

        @Test
        @WithMockUser(username = "professor")
        @DisplayName("Should handle LOW priority")
        void createTask_LowPriority() throws Exception {
            TaskDTO requestDTO = new TaskDTO();
            requestDTO.setTitle("Low Priority Task");
            requestDTO.setPriority("LOW");
            requestDTO.setAssignedToUserId(2L);

            Task lowPriorityTask = new Task();
            lowPriorityTask.setId(4L);
            lowPriorityTask.setTitle("Low Priority Task");
            lowPriorityTask.setPriority(Task.Priority.LOW);
            lowPriorityTask.setStatus(Task.TaskStatus.PENDING);
            lowPriorityTask.setCompleted(false);
            lowPriorityTask.setAssignedTo(studentUser);
            lowPriorityTask.setCreatedBy(professorUser);
            lowPriorityTask.setCreatedAt(LocalDateTime.now());

            when(userService.findByUsername("professor")).thenReturn(Optional.of(professorUser));
            when(taskService.createTask(anyString(), any(), any(),
                    eq(Task.Priority.LOW), anyLong(), anyLong())).thenReturn(lowPriorityTask);

            mockMvc.perform(post("/api/tasks/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated());

            verify(taskService).createTask(anyString(), any(), any(),
                    eq(Task.Priority.LOW), anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("Task Status Tests")
    class TaskStatusTests {

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should update status to PENDING")
        void updateTaskStatus_Pending() throws Exception {
            Task updatedTask = new Task();
            updatedTask.setId(1L);
            updatedTask.setStatus(Task.TaskStatus.PENDING);
            updatedTask.setCompleted(false);
            updatedTask.setAssignedTo(studentUser);
            updatedTask.setCreatedBy(professorUser);
            updatedTask.setCreatedAt(LocalDateTime.now());

            when(taskService.updateTaskStatus(1L, Task.TaskStatus.PENDING, false)).thenReturn(updatedTask);

            mockMvc.perform(put("/api/tasks/1/status")
                    .param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should update status to IN_PROGRESS")
        void updateTaskStatus_InProgress() throws Exception {
            Task updatedTask = new Task();
            updatedTask.setId(1L);
            updatedTask.setStatus(Task.TaskStatus.IN_PROGRESS);
            updatedTask.setCompleted(false);
            updatedTask.setAssignedTo(studentUser);
            updatedTask.setCreatedBy(professorUser);
            updatedTask.setCreatedAt(LocalDateTime.now());

            when(taskService.updateTaskStatus(1L, Task.TaskStatus.IN_PROGRESS, false)).thenReturn(updatedTask);

            mockMvc.perform(put("/api/tasks/1/status")
                    .param("status", "IN_PROGRESS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        }

        @Test
        @WithMockUser(username = "student")
        @DisplayName("Should update status to COMPLETED")
        void updateTaskStatus_Completed() throws Exception {
            Task updatedTask = new Task();
            updatedTask.setId(1L);
            updatedTask.setStatus(Task.TaskStatus.COMPLETED);
            updatedTask.setCompleted(true);
            updatedTask.setAssignedTo(studentUser);
            updatedTask.setCreatedBy(professorUser);
            updatedTask.setCreatedAt(LocalDateTime.now());

            when(taskService.updateTaskStatus(1L, Task.TaskStatus.COMPLETED, true)).thenReturn(updatedTask);

            mockMvc.perform(put("/api/tasks/1/status")
                    .param("status", "COMPLETED")
                    .param("completed", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("COMPLETED"));
        }
    }
}
