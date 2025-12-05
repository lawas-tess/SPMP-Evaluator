package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.dto.ErrorResponse;
import com.team02.spmpevaluator.dto.GradingCriteriaDTO;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.GradingCriteriaService;
import com.team02.spmpevaluator.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for grading criteria management.
 * UC 2.7 - Professors can set and customize grading criteria.
 */
@RestController
@RequestMapping("/api/grading-criteria")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class GradingCriteriaController {

    private final GradingCriteriaService gradingCriteriaService;
    private final UserService userService;

    /**
     * Create a new grading criteria.
     * UC 2.7 Step 1-4: Professor accesses, views, sets, and system confirms.
     * 
     * @param dto Grading criteria data
     * @param authentication Current user authentication
     * @return Created GradingCriteriaDTO
     */
    @PostMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> createGradingCriteria(
            @Valid @RequestBody GradingCriteriaDTO dto,
            Authentication authentication) {
        try {
            Long professorId = getCurrentUserId(authentication);
            GradingCriteriaDTO created = gradingCriteriaService.createGradingCriteria(dto, professorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to create grading criteria: " + e.getMessage(), 500));
        }
    }

    /**
     * Update an existing grading criteria.
     * UC 2.7 Step 3: Professor adjusts weights.
     * 
     * @param id Criteria ID
     * @param dto Updated data
     * @param authentication Current user authentication
     * @return Updated GradingCriteriaDTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> updateGradingCriteria(
            @PathVariable Long id,
            @Valid @RequestBody GradingCriteriaDTO dto,
            Authentication authentication) {
        try {
            Long professorId = getCurrentUserId(authentication);
            GradingCriteriaDTO updated = gradingCriteriaService.updateGradingCriteria(id, dto, professorId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update grading criteria: " + e.getMessage(), 500));
        }
    }

    /**
     * Delete a grading criteria.
     * 
     * @param id Criteria ID
     * @param authentication Current user authentication
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> deleteGradingCriteria(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long professorId = getCurrentUserId(authentication);
            gradingCriteriaService.deleteGradingCriteria(id, professorId);
            return ResponseEntity.ok().body("Grading criteria deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to delete grading criteria: " + e.getMessage(), 500));
        }
    }

    /**
     * Get all grading criteria for the current professor.
     * UC 2.7 Step 2: System displays current weights.
     * 
     * @param authentication Current user authentication
     * @return List of GradingCriteriaDTO
     */
    @GetMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> getMyGradingCriteria(Authentication authentication) {
        try {
            Long professorId = getCurrentUserId(authentication);
            List<GradingCriteriaDTO> criteria = gradingCriteriaService.getGradingCriteriaByProfessor(professorId);
            return ResponseEntity.ok(criteria);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve grading criteria: " + e.getMessage(), 500));
        }
    }

    /**
     * Get the active grading criteria for the current professor.
     * UC 2.7 Step 2: System displays current weights (active one).
     * 
     * @param authentication Current user authentication
     * @return Active GradingCriteriaDTO or default
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('STUDENT')")
    public ResponseEntity<?> getActiveGradingCriteria(Authentication authentication) {
        try {
            Long userId = getCurrentUserId(authentication);
            // For students, we might want to get the professor's criteria
            // For now, return the user's own active criteria or default
            GradingCriteriaDTO activeCriteria = gradingCriteriaService.getActiveCriteriaByProfessor(userId);
            return ResponseEntity.ok(activeCriteria);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve active grading criteria: " + e.getMessage(), 500));
        }
    }

    /**
     * Get a specific grading criteria by ID.
     * 
     * @param id Criteria ID
     * @return GradingCriteriaDTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> getGradingCriteriaById(@PathVariable Long id) {
        try {
            GradingCriteriaDTO criteria = gradingCriteriaService.getGradingCriteriaById(id);
            return ResponseEntity.ok(criteria);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve grading criteria: " + e.getMessage(), 500));
        }
    }

    /**
     * Set a grading criteria as active.
     * UC 2.7 Step 4: System confirms new weights.
     * 
     * @param id Criteria ID
     * @param authentication Current user authentication
     * @return Updated GradingCriteriaDTO
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> setActiveGradingCriteria(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long professorId = getCurrentUserId(authentication);
            GradingCriteriaDTO activated = gradingCriteriaService.setActiveGradingCriteria(id, professorId);
            return ResponseEntity.ok(activated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to activate grading criteria: " + e.getMessage(), 500));
        }
    }

    /**
     * Get default grading criteria (IEEE 1058 standard weights).
     * 
     * @return Default GradingCriteriaDTO
     */
    @GetMapping("/default")
    public ResponseEntity<?> getDefaultGradingCriteria() {
        try {
            GradingCriteriaDTO defaultCriteria = gradingCriteriaService.getDefaultCriteria();
            return ResponseEntity.ok(defaultCriteria);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve default grading criteria: " + e.getMessage(), 500));
        }
    }

    /**
     * Helper method to get current user ID from authentication.
     */
    private Long getCurrentUserId(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }
}
