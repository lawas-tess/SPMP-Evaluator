package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.dto.AssignmentDTO;
import com.team02.spmpevaluator.entity.StudentProfessorAssignment;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.AssignmentService;
import com.team02.spmpevaluator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for admin student-professor assignment management.
 * UC 2.12: Admin Assign Students to Professors
 */
@RestController
@RequestMapping("/api/admin/assignments")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AssignmentController {
    
    private final AssignmentService assignmentService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllAssignments() {
        try {
            List<StudentProfessorAssignment> assignments = assignmentService.getAllAssignments();
            List<AssignmentDTO> dtos = assignments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve assignments: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            // Validate required fields
            if (request.get("professorId") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing required field: professorId");
            }

            Long professorId = Long.valueOf(request.get("professorId").toString());
            String notes = request.getOrDefault("notes", "").toString();

            String username = authentication.getName();
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            // Handle bulk assignment (studentIds array) or single assignment (studentId)
            if (request.get("studentIds") != null) {
                // Bulk assignment
                List<?> studentIds = (List<?>) request.get("studentIds");
                List<AssignmentDTO> assignments = studentIds.stream()
                        .map(id -> {
                            try {
                                Long studentId = Long.valueOf(id.toString());
                                StudentProfessorAssignment assignment = assignmentService.assignStudentToProfessor(
                                        studentId, professorId, admin.getId(), notes);
                                return convertToDTO(assignment);
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(dto -> dto != null)
                        .collect(Collectors.toList());
                
                return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("success", true, "message", "Assigned " + assignments.size() + " student(s)", 
                           "assignments", assignments));
            } else if (request.get("studentId") != null) {
                // Single assignment
                Long studentId = Long.valueOf(request.get("studentId").toString());
                StudentProfessorAssignment assignment = assignmentService.assignStudentToProfessor(
                        studentId, professorId, admin.getId(), notes);
                return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(assignment));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing required field: either studentId or studentIds");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create assignment: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id) {
        try {
            assignmentService.removeAssignment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete assignment: " + e.getMessage());
        }
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<?> getProfessorStudents(@PathVariable Long professorId) {
        try {
            List<User> students = assignmentService.getStudentsByProfessor(professorId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve professor's students: " + e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentProfessor(@PathVariable Long studentId) {
        try {
            User professor = assignmentService.getProfessorByStudent(studentId);
            if (professor == null) {
                return ResponseEntity.ok(Map.of("message", "No professor assigned"));
            }
            return ResponseEntity.ok(professor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve student's professor: " + e.getMessage());
        }
    }

    private AssignmentDTO convertToDTO(StudentProfessorAssignment assignment) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setStudentId(assignment.getStudent().getId());
        dto.setStudentName(assignment.getStudent().getFirstName() + " " + assignment.getStudent().getLastName());
        dto.setStudentEmail(assignment.getStudent().getEmail());
        dto.setProfessorId(assignment.getProfessor().getId());
        dto.setProfessorName(assignment.getProfessor().getFirstName() + " " + assignment.getProfessor().getLastName());
        dto.setProfessorEmail(assignment.getProfessor().getEmail());
        dto.setAssignedByName(assignment.getAssignedBy().getFirstName() + " " + assignment.getAssignedBy().getLastName());
        dto.setAssignedAt(assignment.getAssignedAt().toString());
        dto.setNotes(assignment.getNotes());
        return dto;
    }
}
