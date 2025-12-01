package com.team02.spmpevaluator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate deadline;
    private String priority;
    private String status;
    private boolean completed;
    private Long assignedToUserId;
    private String assignedToUsername;
    private Long createdByUserId;
    private String createdByUsername;
    private LocalDate completionDate;
    private LocalDateTime createdAt;
}
