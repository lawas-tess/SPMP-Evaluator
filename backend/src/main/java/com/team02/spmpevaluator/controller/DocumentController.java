package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.dto.ComplianceReportDTO;
import com.team02.spmpevaluator.entity.ComplianceScore;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.ComplianceHistoryService;
import com.team02.spmpevaluator.repository.ComplianceScoreRepository;
import com.team02.spmpevaluator.service.AuditLogService;
import com.team02.spmpevaluator.service.ComplianceEvaluationService;
import com.team02.spmpevaluator.service.ReportExportService;
import com.team02.spmpevaluator.service.SPMPDocumentService;
import com.team02.spmpevaluator.service.UserService;
import com.team02.spmpevaluator.dto.ComplianceScoreHistoryDTO;
import com.team02.spmpevaluator.entity.ComplianceScoreHistory;
import com.team02.spmpevaluator.util.DocumentParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Document upload and evaluation endpoints.
 * Handles SPMP document uploads, evaluation, and retrieval.
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentController {

    private final SPMPDocumentService documentService;
    private final ComplianceEvaluationService evaluationService;
    private final DocumentParser documentParser;
    private final UserService userService;
    private final AuditLogService auditLogService;
    private final ComplianceScoreRepository complianceScoreRepository;
    private final ComplianceHistoryService complianceHistoryService;
    private final ReportExportService reportExportService;

    /**
     * Upload an SPMP document.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            // Get current user
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Upload document
            SPMPDocument document = documentService.uploadDocument(file, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Document uploaded successfully. ID: " + document.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload error: " + e.getMessage());
        }
    }

    /**
     * Evaluate an uploaded document against IEEE 1058 standard.
     */
    @PostMapping("/{documentId}/evaluate")
    @Transactional
    public ResponseEntity<?> evaluateDocument(@PathVariable Long documentId) {
        try {
            // Get document
            SPMPDocument document = documentService.getDocumentById(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("Document not found"));

            // Extract text content
            String documentContent = documentService.getDocumentContent(documentId);

            // Evaluate (first time only - no archiving)
            var complianceScore = evaluationService.evaluateDocument(document, documentContent);

            // Update document status
            documentService.updateDocumentEvaluation(documentId, "", true);

            // Return report
            ComplianceReportDTO report = evaluationService.convertToDTO(
                complianceScore, 
                document.getId(), 
                document.getFileName()
            );
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Evaluation failed: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process document: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Evaluation error: " + e.getMessage());
        }
    }

    /**
     * Get my documents (current user's uploads).
     */
    @GetMapping("/my-documents")
    public ResponseEntity<?> getMyDocuments() {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<SPMPDocument> documents = documentService.getDocumentsByUser(currentUser.getId());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve documents: " + e.getMessage());
        }
    }

    /**
     * Get a specific document (for professors to view student submissions).
     */
    @GetMapping("/{documentId}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getDocument(@PathVariable Long documentId) {
        try {
            SPMPDocument document = documentService.getDocumentById(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("Document not found"));

            // Check permissions
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (currentUser.getRole() == Role.STUDENT &&
                !document.getUploadedBy().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Unauthorized: You can only view your own documents");
            }

            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve document: " + e.getMessage());
        }
    }

    /**
     * Get evaluation report for a document.
     * UC 2.4: Student View Feedback
     * Step 5: System tracks view activity
     */
    @GetMapping("/{documentId}/report")
    public ResponseEntity<?> getEvaluationReport(@PathVariable Long documentId, HttpServletRequest request) {
        try {
            // Get current user for logging
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            SPMPDocument document = documentService.getDocumentById(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("Document not found"));

            if (!document.isEvaluated()) {
                return ResponseEntity.badRequest().body("Document has not been evaluated yet");
            }

            // Eagerly fetch compliance score with section analyses to avoid lazy loading issues
            ComplianceScore complianceScore = complianceScoreRepository.findByDocumentIdWithSectionAnalyses(documentId)
                    .orElse(null);
            
            if (complianceScore == null) {
                return ResponseEntity.notFound().build();
            }

            // UC 2.4 Step 5: System tracks view activity (non-blocking)
            try {
                String ipAddress = request.getRemoteAddr();
                auditLogService.logFeedbackView(currentUser.getId(), documentId, ipAddress);
            } catch (Exception auditEx) {
                // Log but don't block the report retrieval
                System.err.println("Failed to log audit: " + auditEx.getMessage());
            }

            ComplianceReportDTO report = evaluationService.convertToDTO(
                complianceScore, 
                document.getId(), 
                document.getFileName()
            );
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve report: " + e.getMessage());
        }
    }

    /**
     * Delete a document.
     */
    @DeleteMapping("/{documentId}")
    @Transactional
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        try {
            System.out.println("DELETE request for document ID: " + documentId);
            String username = getAuthenticatedUsername();
            System.out.println("Authenticated user: " + username);
            
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            System.out.println("Current user ID: " + currentUser.getId());
            System.out.println("Calling deleteDocument service...");
            
            documentService.deleteDocument(documentId, currentUser.getId());
            
            System.out.println("Delete successful!");
            return ResponseEntity.ok("Document deleted successfully");
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete document: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Delete error: " + e.getMessage());
        }
    }

    /**
     * Get evaluated documents for the current user.
     */
    @GetMapping("/evaluated")
    public ResponseEntity<?> getEvaluatedDocuments() {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<SPMPDocument> documents = documentService.getEvaluatedDocuments(currentUser.getId());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve documents: " + e.getMessage());
        }
    }

    /**
     * Get unevaluated documents for the current user.
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingDocuments() {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<SPMPDocument> documents = documentService.getUnevaluatedDocuments(currentUser.getId());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve documents: " + e.getMessage());
        }
    }

    /**
     * Add notes to a document.
     */
    @PutMapping("/{documentId}/notes")
    public ResponseEntity<?> addNotes(@PathVariable Long documentId, @RequestBody String notes) {
        try {
            SPMPDocument document = documentService.addNotes(documentId, notes);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add notes: " + e.getMessage());
        }
    }

    /**
     * Replace an existing document (Use Case 2.2 - File Edit).
     * Students can update/replace previously uploaded files.
     */
    @PutMapping("/{documentId}/replace")
    public ResponseEntity<?> replaceDocument(
            @PathVariable Long documentId,
            @RequestParam("file") MultipartFile file) {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Get existing document
            SPMPDocument existingDoc = documentService.getDocumentById(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("Document not found"));

            // Check ownership
            if (!existingDoc.getUploadedBy().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only replace your own documents");
            }

            // Replace document
            SPMPDocument updatedDoc = documentService.replaceDocument(documentId, file, currentUser);

            return ResponseEntity.ok(updatedDoc);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Replace failed: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Replace error: " + e.getMessage());
        }
    }

    /**
     * Get all student submissions (Use Case 2.7 - Submission Tracker).
     * Professors can view all student submissions.
     */
    @GetMapping("/all-submissions")
    public ResponseEntity<?> getAllSubmissions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long studentId) {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Only professors can view all submissions
            if (currentUser.getRole() == Role.STUDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only professors can view all submissions");
            }

            List<SPMPDocument> submissions = documentService.getAllSubmissions(status, studentId);

            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve submissions: " + e.getMessage());
        }
    }

    /**
     * Override document evaluation score (Use Case 2.8 - Override AI Results).
     * Professors can review AI-generated evaluations and override if necessary.
     */
    @PutMapping("/{documentId}/override-score")
    public ResponseEntity<?> overrideScore(
            @PathVariable Long documentId,
            @RequestBody Map<String, Object> overrideData) {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Only professors can override scores
            if (currentUser.getRole() == Role.STUDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only professors can override scores");
            }

            Double newScore = Double.valueOf(overrideData.get("score").toString());
            String notes = overrideData.get("notes") != null ? overrideData.get("notes").toString() : "";

            if (newScore < 0 || newScore > 100) {
                return ResponseEntity.badRequest().body("Score must be between 0 and 100");
            }

            SPMPDocument document = documentService.overrideScore(documentId, newScore, notes, currentUser);

            return ResponseEntity.ok(document);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Override failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to override score: " + e.getMessage());
        }
    }

    /**
     * Re-evaluate a document even if it was already evaluated.
     */
    @PostMapping("/{documentId}/re-evaluate")
    @Transactional
    public ResponseEntity<?> reEvaluateDocument(@PathVariable Long documentId) {
        try {
            // Get document
            SPMPDocument document = documentService.getDocumentById(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("Document not found"));

            // Archive existing score BEFORE loading it into the evaluation service
            ComplianceScore existingScore = complianceScoreRepository.findByDocumentIdWithDocument(documentId).orElse(null);
            if (existingScore != null) {
                // Create history entry from the existing score (without fetching section analyses)
                complianceHistoryService.archiveScore(existingScore, "RE_EVALUATION", getCurrentUserId());
            }

            // Extract text content
            String documentContent = documentService.getDocumentContent(documentId);

            // Re-evaluate (this will clear old section analyses and create new ones)
            var complianceScore = evaluationService.evaluateDocument(document, documentContent);

            // Update document status
            documentService.updateDocumentEvaluation(documentId, "", true);

            // Return report
            ComplianceReportDTO report = evaluationService.convertToDTO(
                complianceScore, 
                document.getId(), 
                document.getFileName()
            );
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Re-evaluation failed: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process document: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Re-evaluation error: " + e.getMessage());
        }
    }

    /**
     * Get score history for a document.
     */
    @GetMapping("/{documentId}/history")
    public ResponseEntity<?> getScoreHistory(@PathVariable Long documentId) {
        try {
            List<ComplianceScoreHistory> history = complianceHistoryService.getHistoryForDocument(documentId);
            List<ComplianceScoreHistoryDTO> dtos = history.stream().map(this::toHistoryDTO).toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve history: " + e.getMessage());
        }
    }

    /**
     * Export report as PDF.
     */
    @GetMapping("/{documentId}/export/pdf")
    public ResponseEntity<?> exportPdf(@PathVariable Long documentId) {
        try {
            byte[] file = reportExportService.exportPdf(documentId);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=spmp-report-" + documentId + ".pdf")
                    .header("Content-Type", "application/pdf")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to export PDF: " + e.getMessage());
        }
    }

    /**
     * Export report as Excel.
     */
    @GetMapping("/{documentId}/export/excel")
    public ResponseEntity<?> exportExcel(@PathVariable Long documentId) {
        try {
            byte[] file = reportExportService.exportExcel(documentId);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=spmp-report-" + documentId + ".xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to export Excel: " + e.getMessage());
        }
    }

    private ComplianceScoreHistoryDTO toHistoryDTO(ComplianceScoreHistory history) {
        ComplianceScoreHistoryDTO dto = new ComplianceScoreHistoryDTO();
        dto.setId(history.getId());
        dto.setOverallScore(history.getOverallScore());
        dto.setStructureScore(history.getStructureScore());
        dto.setCompletenessScore(history.getCompletenessScore());
        dto.setSectionsFound(history.getSectionsFound());
        dto.setTotalSectionsRequired(history.getTotalSectionsRequired());
        dto.setCompliant(history.isCompliant());
        dto.setProfessorOverride(history.getProfessorOverride());
        dto.setProfessorNotes(history.getProfessorNotes());
        dto.setSummary(history.getSummary());
        dto.setEvaluatedAt(history.getEvaluatedAt());
        dto.setRecordedAt(history.getRecordedAt());
        dto.setVersionNumber(history.getVersionNumber());
        dto.setSource(history.getSource());
        return dto;
    }

    /**
     * Helper method to get authenticated username.
     */
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    private Long getCurrentUserId() {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return null;
        }
        return userService.findByUsername(username)
                .map(User::getId)
                .orElse(null);
    }
}
