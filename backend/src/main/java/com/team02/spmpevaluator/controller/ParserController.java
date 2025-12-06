package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.dto.ParserConfigurationDTO;
import com.team02.spmpevaluator.dto.ParserFeedbackDTO;
import com.team02.spmpevaluator.entity.*;
import com.team02.spmpevaluator.repository.SPMPDocumentRepository;
import com.team02.spmpevaluator.security.CustomUserDetails;
import com.team02.spmpevaluator.service.ParserConfigurationService;
import com.team02.spmpevaluator.service.ParserFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for parser configuration and feedback management.
 * Handles IEEE 1058 clause mappings, custom rules, and AI parser feedback.
 * Only accessible to professors.
 */
@RestController
@RequestMapping("/api/parser")
@RequiredArgsConstructor
public class ParserController {

    private final ParserConfigurationService parserConfigurationService;
    private final ParserFeedbackService parserFeedbackService;
    private final SPMPDocumentRepository documentRepository;

    // ============= Parser Configuration Endpoints =============

    /**
     * Create a new parser configuration
     * Only professors can create configurations
     */
    @PostMapping("/config")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> createConfiguration(
            @RequestBody ParserConfigurationDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        try {
            User user = userDetails.getUser();
            
            ParserConfiguration config = new ParserConfiguration();
            config.setName(dto.getName());
            config.setDescription(dto.getDescription());
            config.setClauseMappings(dto.getClauseMappings());
            config.setCustomRules(dto.getCustomRules());
            config.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
            config.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
            
            ParserConfiguration created = parserConfigurationService.createConfiguration(config, user);
            
            return ResponseEntity.ok(convertToDTO(created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create parser configuration: " + e.getMessage());
        }
    }

    /**
     * Update an existing parser configuration
     */
    @PutMapping("/config/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> updateConfiguration(
            @PathVariable Long id,
            @RequestBody ParserConfigurationDTO dto) {
        
        try {
            ParserConfiguration config = new ParserConfiguration();
            config.setName(dto.getName());
            config.setDescription(dto.getDescription());
            config.setClauseMappings(dto.getClauseMappings());
            config.setCustomRules(dto.getCustomRules());
            config.setIsActive(dto.getIsActive());
            config.setIsDefault(dto.getIsDefault());
            
            ParserConfiguration updated = parserConfigurationService.updateConfiguration(id, config);
            
            return ResponseEntity.ok(convertToDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update parser configuration: " + e.getMessage());
        }
    }

    /**
     * Get all active parser configurations
     */
    @GetMapping("/config")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<List<ParserConfigurationDTO>> getActiveConfigurations() {
        List<ParserConfiguration> configs = parserConfigurationService.getActiveConfigurations();
        List<ParserConfigurationDTO> dtos = configs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get parser configuration by ID
     */
    @GetMapping("/config/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> getConfigurationById(@PathVariable Long id) {
        return parserConfigurationService.getConfigurationById(id)
                .map(config -> ResponseEntity.ok(convertToDTO(config)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get default parser configuration
     */
    @GetMapping("/config/default")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> getDefaultConfiguration() {
        return parserConfigurationService.getDefaultConfiguration()
                .map(config -> ResponseEntity.ok(convertToDTO(config)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Set a configuration as default
     */
    @PutMapping("/config/{id}/set-default")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> setAsDefault(@PathVariable Long id) {
        try {
            ParserConfiguration config = parserConfigurationService.setAsDefault(id);
            return ResponseEntity.ok(convertToDTO(config));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to set as default: " + e.getMessage());
        }
    }

    /**
     * Delete a parser configuration
     */
    @DeleteMapping("/config/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> deleteConfiguration(@PathVariable Long id) {
        try {
            parserConfigurationService.deleteConfiguration(id);
            return ResponseEntity.ok("Parser configuration deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete configuration: " + e.getMessage());
        }
    }

    /**
     * Create default IEEE 1058 configuration
     */
    @PostMapping("/config/create-default")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> createDefaultConfiguration(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            ParserConfiguration config = parserConfigurationService.createDefaultConfiguration(user);
            return ResponseEntity.ok(convertToDTO(config));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create default configuration: " + e.getMessage());
        }
    }

    // ============= Parser Feedback Endpoints =============

    /**
     * Generate mock feedback for a document (for demonstration)
     * TODO: Replace with actual AI parser integration
     */
    @PostMapping("/feedback/{documentId}/generate-mock")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> generateMockFeedback(@PathVariable Long documentId) {
        try {
            SPMPDocument document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));
            
            ParserConfiguration config = parserConfigurationService.getDefaultConfiguration()
                    .orElseThrow(() -> new RuntimeException("No default configuration found"));
            
            ParserFeedback feedback = parserFeedbackService.generateMockFeedback(document, config);
            
            return ResponseEntity.ok(convertToFeedbackDTO(feedback));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate feedback: " + e.getMessage());
        }
    }

    /**
     * Get feedback for a specific document
     */
    @GetMapping("/feedback/document/{documentId}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'STUDENT')")
    public ResponseEntity<List<ParserFeedbackDTO>> getFeedbackByDocument(@PathVariable Long documentId) {
        List<ParserFeedback> feedbacks = parserFeedbackService.getFeedbackByDocumentId(documentId);
        List<ParserFeedbackDTO> dtos = feedbacks.stream()
                .map(this::convertToFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get feedback by ID
     */
    @GetMapping("/feedback/{id}")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'STUDENT')")
    public ResponseEntity<?> getFeedbackById(@PathVariable Long id) {
        return parserFeedbackService.getFeedbackById(id)
                .map(feedback -> ResponseEntity.ok(convertToFeedbackDTO(feedback)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all feedback for current user's documents
     */
    @GetMapping("/feedback/my-documents")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ParserFeedbackDTO>> getMyDocumentsFeedback(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        User user = userDetails.getUser();
        List<ParserFeedback> feedbacks = parserFeedbackService.getFeedbackByUserId(user.getId());
        List<ParserFeedbackDTO> dtos = feedbacks.stream()
                .map(this::convertToFeedbackDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ============= Helper Methods =============

    private ParserConfigurationDTO convertToDTO(ParserConfiguration config) {
        ParserConfigurationDTO dto = new ParserConfigurationDTO();
        dto.setId(config.getId());
        dto.setName(config.getName());
        dto.setDescription(config.getDescription());
        dto.setClauseMappings(config.getClauseMappings());
        dto.setCustomRules(config.getCustomRules());
        dto.setIsActive(config.getIsActive());
        dto.setIsDefault(config.getIsDefault());
        
        if (config.getCreatedBy() != null) {
            dto.setCreatedByUserId(config.getCreatedBy().getId());
            dto.setCreatedByUsername(config.getCreatedBy().getUsername());
        }
        
        if (config.getCreatedAt() != null) {
            dto.setCreatedAt(config.getCreatedAt().toString());
        }
        if (config.getUpdatedAt() != null) {
            dto.setUpdatedAt(config.getUpdatedAt().toString());
        }
        
        return dto;
    }

    private ParserFeedbackDTO convertToFeedbackDTO(ParserFeedback feedback) {
        ParserFeedbackDTO dto = new ParserFeedbackDTO();
        dto.setId(feedback.getId());
        
        if (feedback.getDocument() != null) {
            dto.setDocumentId(feedback.getDocument().getId());
            dto.setDocumentName(feedback.getDocument().getFileName());
        }
        
        if (feedback.getParserConfiguration() != null) {
            dto.setParserConfigId(feedback.getParserConfiguration().getId());
            dto.setParserConfigName(feedback.getParserConfiguration().getName());
        }
        
        dto.setComplianceScore(feedback.getComplianceScore());
        dto.setDetectedClauses(feedback.getDetectedClauses());
        dto.setMissingClauses(feedback.getMissingClauses());
        dto.setRecommendations(feedback.getRecommendations());
        dto.setAnalysisReport(feedback.getAnalysisReport());
        dto.setParserVersion(feedback.getParserVersion());
        dto.setStatus(feedback.getStatus() != null ? feedback.getStatus().toString() : null);
        dto.setErrorMessage(feedback.getErrorMessage());
        
        if (feedback.getAnalyzedAt() != null) {
            dto.setAnalyzedAt(feedback.getAnalyzedAt().toString());
        }
        
        return dto;
    }
}
