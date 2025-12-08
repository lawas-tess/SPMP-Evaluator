package com.team02.spmpevaluator.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data transfer object for tasks.
 * <p>
 * Does not use Lombok so IDEs without annotation processing
 * can still resolve getters, setters and constructors.
 */
public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String priority;
    private String status;
    private boolean completed;
    private Long assignedToUserId;
    private String assignedToUsername;
    private String assignedToFirstName;
    private String assignedToLastName;
    private Long createdByUserId;
    private String createdByUsername;
    private LocalDate completionDate;
    private LocalDateTime createdAt;

    public TaskDTO() {
    }

    public TaskDTO(Long id,
                   String title,
                   String description,
                   LocalDateTime deadline,
                   String priority,
                   String status,
                   boolean completed,
                   Long assignedToUserId,
                   String assignedToUsername,
                   String assignedToFirstName,
                   String assignedToLastName,
                   Long createdByUserId,
                   String createdByUsername,
                   LocalDate completionDate,
                   LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.status = status;
        this.completed = completed;
        this.assignedToUserId = assignedToUserId;
        this.assignedToUsername = assignedToUsername;
        this.assignedToFirstName = assignedToFirstName;
        this.assignedToLastName = assignedToLastName;
        this.createdByUserId = createdByUserId;
        this.createdByUsername = createdByUsername;
        this.completionDate = completionDate;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getAssignedToUserId() {
        return assignedToUserId;
    }

    public void setAssignedToUserId(Long assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }

    public String getAssignedToUsername() {
        return assignedToUsername;
    }

    public void setAssignedToUsername(String assignedToUsername) {
        this.assignedToUsername = assignedToUsername;
    }

    public String getAssignedToFirstName() {
        return assignedToFirstName;
    }

    public void setAssignedToFirstName(String assignedToFirstName) {
        this.assignedToFirstName = assignedToFirstName;
    }

    public String getAssignedToLastName() {
        return assignedToLastName;
    }

    public void setAssignedToLastName(String assignedToLastName) {
        this.assignedToLastName = assignedToLastName;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
