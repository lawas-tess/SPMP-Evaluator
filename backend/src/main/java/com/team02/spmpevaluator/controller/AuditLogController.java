package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.entity.AuditLog;
import com.team02.spmpevaluator.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for admin audit log viewing and analysis.
 * UC 2.13: Admin Audit Logs
 */
@RestController
@RequestMapping("/api/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AuditLogController {
    
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        try {
            List<AuditLog> logs;
            
            if (username != null && !username.isEmpty()) {
                logs = auditLogService.getLogsByUsername(username);
            } else if (action != null && !action.isEmpty()) {
                logs = auditLogService.getLogsByAction(action);
            } else if (resource != null && !resource.isEmpty()) {
                logs = auditLogService.getLogsByResourceType(resource);
            } else if (startDate != null && endDate != null) {
                logs = auditLogService.getLogsBetweenDates(startDate, endDate);
            } else {
                logs = auditLogService.getAllLogs();
            }
            
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve audit logs: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuditLogById(@PathVariable Long id) {
        try {
            AuditLog log = auditLogService.getLogById(id);
            return ResponseEntity.ok(log);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Audit log not found: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAuditLogsByUserId(@PathVariable Long userId) {
        try {
            List<AuditLog> logs = auditLogService.getLogsByUserId(userId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve user audit logs: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportAuditLogs(@RequestParam(required = false) LocalDateTime startDate,
                                            @RequestParam(required = false) LocalDateTime endDate) {
        try {
            // For now, return logs as JSON - can be enhanced with CSV export
            List<AuditLog> logs = startDate != null && endDate != null ?
                    auditLogService.getLogsBetweenDates(startDate, endDate) :
                    auditLogService.getAllLogs();
            
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to export audit logs: " + e.getMessage());
        }
    }
}
