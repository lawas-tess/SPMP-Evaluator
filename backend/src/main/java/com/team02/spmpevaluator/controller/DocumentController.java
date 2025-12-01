package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.dto.ComplianceReportDTO;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.SPMPDocument;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.ComplianceEvaluationService;
import com.team02.spmpevaluator.service.SPMPDocumentService;
import com.team02.spmpevaluator.service.UserService;
import com.team02.spmpevaluator.util.DocumentParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    public ResponseEntity<?> evaluateDocument(@PathVariable Long documentId) {
        try {
            // Get document
            SPMPDocument document = documentService.getDocumentById(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("Document not found"));

            // Extract text content
            String documentContent = documentService.getDocumentContent(documentId);

            // Evaluate
            var complianceScore = evaluationService.evaluateDocument(document, documentContent);

            // Update document status
            documentService.updateDocumentEvaluation(documentId, "", true);

            // Return report
            ComplianceReportDTO report = evaluationService.convertToDTO(complianceScore);
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
     * Get a specific document (for professors/PMs to view student submissions).
     */
    @GetMapping("/{documentId}")
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
     */
    @GetMapping("/{documentId}/report")
    public ResponseEntity<?> getEvaluationReport(@PathVariable Long documentId) {
        try {
            SPMPDocument document = documentService.getDocumentById(documentId)
                    .orElseThrow(() -> new IllegalArgumentException("Document not found"));

            if (!document.isEvaluated()) {
                return ResponseEntity.badRequest().body("Document has not been evaluated yet");
            }

            if (document.getComplianceScore() == null) {
                return ResponseEntity.notFound().build();
            }

            ComplianceReportDTO report = evaluationService.convertToDTO(document.getComplianceScore());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve report: " + e.getMessage());
        }
    }

    /**
     * Delete a document (only owners or admins).
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        try {
            String username = getAuthenticatedUsername();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            documentService.deleteDocument(documentId, currentUser.getId());
            return ResponseEntity.ok("Document deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete document: " + e.getMessage());
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
     * Helper method to get authenticated username.
     */
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
}
