package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.service.UserService;
import com.team02.spmpevaluator.service.SPMPDocumentService;
import com.team02.spmpevaluator.service.ComplianceEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for admin reports and analytics.
 * UC 2.14: Admin Reports
 */
@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminReportController {
    
    private final UserService userService;
    private final SPMPDocumentService documentService;
    private final ComplianceEvaluationService evaluationService;

    @GetMapping("/users")
    public ResponseEntity<?> getUserReport() {
        try {
            Map<String, Object> report = new HashMap<>();
            report.put("totalUsers", userService.getAllUsers().size());
            report.put("totalStudents", userService.getUsersByRole(com.team02.spmpevaluator.entity.Role.STUDENT).size());
            report.put("totalProfessors", userService.getUsersByRole(com.team02.spmpevaluator.entity.Role.PROFESSOR).size());
            report.put("totalAdmins", userService.getUsersByRole(com.team02.spmpevaluator.entity.Role.ADMIN).size());
            report.put("generatedAt", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate user report: " + e.getMessage());
        }
    }

    @GetMapping("/submissions")
    public ResponseEntity<?> getSubmissionReport() {
        try {
            Map<String, Object> report = new HashMap<>();
            long totalDocs = documentService.getAllDocuments().size();
            report.put("totalSubmissions", totalDocs);
            report.put("generatedAt", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate submission report: " + e.getMessage());
        }
    }

    @GetMapping("/evaluations")
    public ResponseEntity<?> getEvaluationReport() {
        try {
            Map<String, Object> report = new HashMap<>();
            long totalEvals = evaluationService.getAllEvaluations().size();
            report.put("totalEvaluations", totalEvals);
            report.put("generatedAt", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate evaluation report: " + e.getMessage());
        }
    }

    @GetMapping("/system-health")
    public ResponseEntity<?> getSystemHealthReport() {
        try {
            Map<String, Object> report = new HashMap<>();
            report.put("status", "healthy");
            report.put("timestamp", java.time.LocalDateTime.now().toString());
            report.put("uptime", "Available via actuator endpoints");
            
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate system health report: " + e.getMessage());
        }
    }
}
