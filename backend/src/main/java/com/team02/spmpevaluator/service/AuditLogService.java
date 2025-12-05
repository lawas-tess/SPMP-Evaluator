package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.AuditLog;
import com.team02.spmpevaluator.entity.AuditLog.ActionType;
import com.team02.spmpevaluator.entity.AuditLog.ResourceType;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.AuditLogRepository;
import com.team02.spmpevaluator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for audit logging operations.
 * UC 2.4 Step 5: System tracks view activity (feedback)
 * UC 2.5 Step 5: System updates view activity (task tracking)
 * UC 2.10 Step 5: System logs viewing activity (student progress)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    /**
     * Log a view action for tracking activity.
     * 
     * @param userId User who performed the view
     * @param resourceType Type of resource viewed
     * @param resourceId ID of the resource viewed
     * @param details Additional details about the view
     * @param ipAddress IP address of the viewer (optional)
     */
    public void logViewActivity(Long userId, ResourceType resourceType, Long resourceId, 
                                String details, String ipAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(ActionType.VIEW);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        
        auditLogRepository.save(log);
    }

    /**
     * Log a generic action.
     */
    public void logAction(Long userId, ActionType action, ResourceType resourceType, 
                         Long resourceId, String details, String ipAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        
        auditLogRepository.save(log);
    }

    /**
     * Log feedback view (UC 2.4 Step 5).
     */
    public void logFeedbackView(Long userId, Long documentId, String ipAddress) {
        logViewActivity(userId, ResourceType.EVALUATION, documentId, 
                "Student viewed feedback for document", ipAddress);
    }

    /**
     * Log task tracking view (UC 2.5 Step 5).
     */
    public void logTaskView(Long userId, Long taskId, String ipAddress) {
        logViewActivity(userId, ResourceType.TASK, taskId, 
                "Student viewed task details", ipAddress);
    }

    /**
     * Log student progress view (UC 2.10 Step 5).
     */
    public void logStudentProgressView(Long professorId, Long studentId, String ipAddress) {
        logViewActivity(professorId, ResourceType.USER, studentId, 
                "Professor viewed student progress", ipAddress);
    }

    /**
     * Get all view logs for a user.
     */
    public List<AuditLog> getViewLogs(Long userId) {
        return auditLogRepository.findByUserIdAndActionOrderByCreatedAtDesc(userId, ActionType.VIEW);
    }

    /**
     * Get all logs for a user.
     */
    public List<AuditLog> getUserLogs(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }
}
