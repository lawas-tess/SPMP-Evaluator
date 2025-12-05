package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.Task;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import com.team02.spmpevaluator.repository.SPMPDocumentRepository;
import com.team02.spmpevaluator.service.TaskService;
import com.team02.spmpevaluator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reporting and analytics endpoints for Professors.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportingController {

    private final ComplianceScoreRepository complianceScoreRepository;
    private final SPMPDocumentRepository documentRepository;
    private final UserService userService;
    private final TaskService taskService;

    /**
     * Get compliance statistics for all evaluated documents.
     */
    @GetMapping("/compliance-statistics")
    public ResponseEntity<?> getComplianceStatistics() {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Only professors can view reports
            if (currentUser.getRole() == Role.STUDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only professors can access reports");
            }

            List<SPMPDocument> evaluatedDocs = documentRepository.findByEvaluated(true);

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalEvaluated", evaluatedDocs.size());

            if (!evaluatedDocs.isEmpty()) {
                double avgScore = evaluatedDocs.stream()
                        .mapToDouble(doc -> doc.getComplianceScore() != null ? doc.getComplianceScore().getOverallScore() : 0)
                        .average()
                        .orElse(0.0);

                long compliant = evaluatedDocs.stream()
                        .filter(doc -> doc.getComplianceScore() != null && doc.getComplianceScore().isCompliant())
                        .count();

                statistics.put("averageScore", String.format("%.2f", avgScore));
                statistics.put("compliantDocuments", compliant);
                statistics.put("nonCompliantDocuments", evaluatedDocs.size() - compliant);
            }

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve statistics: " + e.getMessage());
        }
    }

    /**
     * Get performance metrics by student.
     */
    @GetMapping("/student-performance/{userId}")
    public ResponseEntity<?> getStudentPerformance(@PathVariable Long userId) {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (currentUser.getRole() == Role.STUDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only professors can access reports");
            }

            List<SPMPDocument> studentDocs = documentRepository.findByUploadedBy_Id(userId);

            Map<String, Object> performance = new HashMap<>();
            performance.put("studentId", userId);
            performance.put("totalUploads", studentDocs.size());
            performance.put("totalEvaluated", studentDocs.stream().filter(SPMPDocument::isEvaluated).count());

            double avgScore = studentDocs.stream()
                    .filter(SPMPDocument::isEvaluated)
                    .mapToDouble(doc -> doc.getComplianceScore() != null ? doc.getComplianceScore().getOverallScore() : 0)
                    .average()
                    .orElse(0.0);

            performance.put("averageScore", String.format("%.2f", avgScore));

            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve student performance: " + e.getMessage());
        }
    }

    /**
     * Get comprehensive student progress (Use Case 2.10 - Monitor Student Progress).
     * Includes task completion stats and document evaluation progress.
     */
    @GetMapping("/student-progress/{userId}")
    public ResponseEntity<?> getStudentProgress(@PathVariable Long userId) {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Only professors can access, OR student can view their own progress
            if (currentUser.getRole() == Role.STUDENT && !currentUser.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only view your own progress");
            }

            User student = userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));

            // Get documents
            List<SPMPDocument> studentDocs = documentRepository.findByUploadedBy_Id(userId);

            // Get tasks
            List<Task> studentTasks = taskService.getTasksByAssignedUser(userId);

            Map<String, Object> progress = new HashMap<>();
            progress.put("studentId", userId);
            progress.put("studentName", student.getFirstName() + " " + student.getLastName());

            // Document stats
            Map<String, Object> documentStats = new HashMap<>();
            documentStats.put("totalUploads", studentDocs.size());
            documentStats.put("evaluated", studentDocs.stream().filter(SPMPDocument::isEvaluated).count());
            documentStats.put("pending", studentDocs.stream().filter(d -> !d.isEvaluated()).count());

            double avgScore = studentDocs.stream()
                    .filter(SPMPDocument::isEvaluated)
                    .filter(doc -> doc.getComplianceScore() != null)
                    .mapToDouble(doc -> doc.getComplianceScore().getOverallScore())
                    .average()
                    .orElse(0.0);
            documentStats.put("averageScore", String.format("%.2f", avgScore));

            long compliantDocs = studentDocs.stream()
                    .filter(SPMPDocument::isEvaluated)
                    .filter(doc -> doc.getComplianceScore() != null && doc.getComplianceScore().isCompliant())
                    .count();
            documentStats.put("compliantDocuments", compliantDocs);

            progress.put("documents", documentStats);

            // Task stats
            Map<String, Object> taskStats = new HashMap<>();
            taskStats.put("totalTasks", studentTasks.size());
            taskStats.put("completed", studentTasks.stream().filter(Task::isCompleted).count());
            taskStats.put("pending", studentTasks.stream().filter(t -> !t.isCompleted()).count());
            taskStats.put("overdue", studentTasks.stream()
                    .filter(t -> !t.isCompleted() && t.getDeadline() != null && t.getDeadline().isBefore(java.time.LocalDate.now()))
                    .count());

            double completionRate = studentTasks.isEmpty() ? 0 :
                    (studentTasks.stream().filter(Task::isCompleted).count() / (double) studentTasks.size()) * 100;
            taskStats.put("completionRate", String.format("%.2f%%", completionRate));

            progress.put("tasks", taskStats);

            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve student progress: " + e.getMessage());
        }
    }

    /**
     * Get time-based compliance trends.
     */
    @GetMapping("/compliance-trends")
    public ResponseEntity<?> getComplianceTrends(
            @RequestParam(required = false) Integer daysBack) {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (currentUser.getRole() == Role.STUDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only professors can access reports");
            }

            if (daysBack == null) daysBack = 30;

            LocalDateTime startDate = LocalDateTime.now().minusDays(daysBack);
            List<ComplianceScore> scores = complianceScoreRepository.findAll().stream()
                    .filter(score -> score.getEvaluatedAt() != null && score.getEvaluatedAt().isAfter(startDate))
                    .toList();

            Map<String, Object> trends = new HashMap<>();
            trends.put("period", daysBack + " days");
            trends.put("totalEvaluations", scores.size());

            if (!scores.isEmpty()) {
                double avgScore = scores.stream()
                        .mapToDouble(ComplianceScore::getOverallScore)
                        .average()
                        .orElse(0.0);

                long compliant = scores.stream()
                        .filter(ComplianceScore::isCompliant)
                        .count();

                trends.put("averageScore", String.format("%.2f", avgScore));
                trends.put("complianceRate", String.format("%.2f%%", (compliant / (double) scores.size()) * 100));
            }

            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve trends: " + e.getMessage());
        }
    }

    /**
     * Override an evaluation score (Professor only).
     */
    @PutMapping("/{documentId}/override-score")
    public ResponseEntity<?> overrideScore(
            @PathVariable Long documentId,
            @RequestParam Double newScore,
            @RequestBody String notes) {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (currentUser.getRole() == Role.STUDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only professors can override scores");
            }

            if (newScore < 0 || newScore > 100) {
                return ResponseEntity.badRequest().body("Score must be between 0 and 100");
            }

            ComplianceScore score = complianceScoreRepository.findByDocumentId(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("Compliance score not found"));

            score.setProfessorOverride(newScore);
            score.setProfessorNotes(notes);
            score.setReviewedBy(currentUser);
            score.setReviewedAt(LocalDateTime.now());

            complianceScoreRepository.save(score);

            return ResponseEntity.ok("Score overridden successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to override score: " + e.getMessage());
        }
    }

    /**
     * Helper method to get authenticated username.
     */
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}
