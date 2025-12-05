package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.dto.ErrorResponse;
import com.team02.spmpevaluator.dto.UserDTO;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for user management operations.
 * Provides endpoints for retrieving user information by role.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    /**
     * Get all students.
     * UC 2.10 - Professors can view class-wide student progress summary.
     * UC 2.6 - Professors can assign tasks to specific students.
     * 
     * @return List of all users with STUDENT role
     */
    @GetMapping("/students")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<UserDTO> students = userService.getUsersByRole(Role.STUDENT);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Failed to retrieve students: " + e.getMessage(), 400));
        }
    }

    /**
     * Get all professors.
     * For admin use - managing professor accounts.
     * 
     * @return List of all users with PROFESSOR role
     */
    @GetMapping("/professors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllProfessors() {
        try {
            List<UserDTO> professors = userService.getUsersByRole(Role.PROFESSOR);
            return ResponseEntity.ok(professors);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Failed to retrieve professors: " + e.getMessage(), 400));
        }
    }

    /**
     * Get all users.
     * Admin only - for user management purposes.
     * 
     * @return List of all users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Failed to retrieve users: " + e.getMessage(), 400));
        }
    }

    /**
     * Get a specific user by ID.
     * 
     * @param id User ID
     * @return User details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return userService.findById(id)
                    .map(user -> ResponseEntity.ok(userService.convertToDTO(user)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Failed to retrieve user: " + e.getMessage(), 400));
        }
    }

    /**
     * Toggle user enabled status.
     * Admin only - for enabling/disabling user accounts.
     * 
     * @param id User ID
     * @param enabled New enabled status
     * @return Success message
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        try {
            userService.toggleUserStatus(id, enabled);
            return ResponseEntity.ok().body("User status updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Failed to update user status: " + e.getMessage(), 400));
        }
    }
}
