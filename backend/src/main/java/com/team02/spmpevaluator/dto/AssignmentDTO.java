package com.team02.spmpevaluator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long professorId;
    private String professorName;
    private String professorEmail;
    private String assignedByName;
    private String assignedAt;
    private String notes;
}
