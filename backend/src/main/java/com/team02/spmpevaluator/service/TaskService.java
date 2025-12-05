package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.Task;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.TaskRepository;
import com.team02.spmpevaluator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new task assigned to a user.
     */
    public Task createTask(String title, String description, LocalDate deadline,
                          Task.Priority priority, Long assignedToUserId, Long createdByUserId) {
        User assignedTo = userRepository.findById(assignedToUserId)
                .orElseThrow(() -> new IllegalArgumentException("Assigned user not found"));
        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("Creator user not found"));

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setPriority(priority);
        task.setAssignedTo(assignedTo);
        task.setCreatedBy(createdBy);
        task.setStatus(Task.TaskStatus.PENDING);
        task.setCompleted(false);

        return taskRepository.save(task);
    }

    /**
     * Retrieves task by ID.
     */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Gets all tasks assigned to a specific user.
     */
    public List<Task> getTasksByAssignedUser(Long userId) {
        return taskRepository.findByAssignedTo_Id(userId);
    }

    /**
     * Gets all tasks created by a specific user (professor).
     */
    public List<Task> getTasksByCreator(Long createdByUserId) {
        return taskRepository.findByCreatedBy_Id(createdByUserId);
    }

    /**
     * Gets all incomplete tasks for a user.
     */
    public List<Task> getIncompleteTasksForUser(Long userId) {
        return taskRepository.findByAssignedTo_IdAndCompletedFalse(userId);
    }

    /**
     * Gets all tasks with a specific status.
     */
    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    /**
     * Updates task status and completion info.
     */
    public Task updateTaskStatus(Long taskId, Task.TaskStatus status, boolean completed) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setStatus(status);
        task.setCompleted(completed);
        if (completed) {
            task.setCompletionDate(LocalDate.now());
        }
        task.setUpdatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    /**
     * Updates task details.
     */
    public Task updateTask(Long taskId, String title, String description,
                          LocalDate deadline, Task.Priority priority) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setPriority(priority);
        task.setUpdatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    /**
     * Marks a task as completed.
     */
    public Task completeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setCompleted(true);
        task.setStatus(Task.TaskStatus.COMPLETED);
        task.setCompletionDate(LocalDate.now());
        task.setUpdatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    /**
     * Deletes a task.
     */
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        taskRepository.delete(task);
    }

    /**
     * Gets overdue tasks.
     */
    public List<Task> getOverdueTasks() {
        return taskRepository.findByDeadlineBeforeAndCompletedFalse(LocalDate.now());
    }
}
