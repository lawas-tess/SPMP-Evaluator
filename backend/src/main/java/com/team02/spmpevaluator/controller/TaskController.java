package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.dto.TaskDTO;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.Task;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.AuditLogService;
import com.team02.spmpevaluator.service.TaskService;
import com.team02.spmpevaluator.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Task management endpoints for professors.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    /**
     * Create a new task (Professors only).
     */
    @PostMapping("/create")
    public ResponseEntity<?> createTask(@RequestBody TaskDTO taskDTO) {
        try {
            String username = getAuthenticatedUsername();
            User creator = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Only professors can create tasks
            if (creator.getRole() == Role.STUDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only professors can create tasks");
            }

            Task task = taskService.createTask(
                    taskDTO.getTitle(),
                    taskDTO.getDescription(),
                    taskDTO.getDeadline(),
                    Task.Priority.valueOf(taskDTO.getPriority().toUpperCase()),
                    taskDTO.getAssignedToUserId(),
                    creator.getId()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(task));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Task creation failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating task: " + e.getMessage());
        }
    }

    /**
     * Get my assigned tasks.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyTasks() {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<Task> tasks = taskService.getTasksByAssignedUser(currentUser.getId());
            List<TaskDTO> dtos = tasks.stream().map(this::convertToDTO).collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve tasks: " + e.getMessage());
        }
    }

    /**
     * Get incomplete tasks for the current user.
     */
    @GetMapping("/incomplete")
    public ResponseEntity<?> getIncompleteTasks() {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<Task> tasks = taskService.getIncompleteTasksForUser(currentUser.getId());
            List<TaskDTO> dtos = tasks.stream().map(this::convertToDTO).collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve tasks: " + e.getMessage());
        }
    }

    /**
     * Get tasks created by the current user (for professors).
     */
    @GetMapping("/created")
    public ResponseEntity<?> getCreatedTasks() {
        try {
            String username = getAuthenticatedUsername();
            User creator = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<Task> tasks = taskService.getTasksByCreator(creator.getId());
            List<TaskDTO> dtos = tasks.stream().map(this::convertToDTO).collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve tasks: " + e.getMessage());
        }
    }

    /**
     * Get a specific task.
     * UC 2.5: Student Task Tracking
     * Step 5: System updates view activity
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTask(@PathVariable Long taskId, HttpServletRequest request) {
        try {
            // Get current user for logging
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Task task = taskService.getTaskById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found"));

            // UC 2.5 Step 5: System updates view activity
            String ipAddress = request.getRemoteAddr();
            auditLogService.logTaskView(currentUser.getId(), taskId, ipAddress);

            return ResponseEntity.ok(convertToDTO(task));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve task: " + e.getMessage());
        }
    }

    /**
     * Update a task.
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long taskId, @RequestBody TaskDTO taskDTO) {
        try {
            Task task = taskService.updateTask(
                    taskId,
                    taskDTO.getTitle(),
                    taskDTO.getDescription(),
                    taskDTO.getDeadline(),
                    Task.Priority.valueOf(taskDTO.getPriority().toUpperCase()),
                    taskDTO.getAssignedToUserId()
            );

            return ResponseEntity.ok(convertToDTO(task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update task: " + e.getMessage());
        }
    }

    /**
     * Update task status.
     */
    @PutMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam String status,
            @RequestParam(required = false, defaultValue = "false") boolean completed) {
        try {
            Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
            Task task = taskService.updateTaskStatus(taskId, taskStatus, completed);

            return ResponseEntity.ok(convertToDTO(task));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update task: " + e.getMessage());
        }
    }

    /**
     * Mark task as completed.
     */
    @PutMapping("/{taskId}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Long taskId) {
        try {
            Task task = taskService.completeTask(taskId);
            return ResponseEntity.ok(convertToDTO(task));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to complete task: " + e.getMessage());
        }
    }

    /**
     * Delete a task (Professors only).
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (currentUser.getRole() == Role.STUDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only professors can delete tasks");
            }

            taskService.deleteTask(taskId);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete task: " + e.getMessage());
        }
    }

    /**
     * Get overdue tasks.
     */
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueTasks() {
        try {
            List<Task> tasks = taskService.getOverdueTasks();
            List<TaskDTO> dtos = tasks.stream().map(this::convertToDTO).collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve overdue tasks: " + e.getMessage());
        }
    }

    /**
     * Convert Task entity to DTO.
     */
    private TaskDTO convertToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDeadline(),
                task.getPriority().toString(),
                task.getStatus().toString(),
                task.isCompleted(),
                task.getAssignedTo().getId(),
                task.getAssignedTo().getUsername(),
                task.getAssignedTo().getFirstName(),
                task.getAssignedTo().getLastName(),
                task.getCreatedBy().getId(),
                task.getCreatedBy().getUsername(),
                task.getCompletionDate(),
                task.getCreatedAt()
        );
    }

    /**
     * Helper method to get authenticated username.
     */
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}
