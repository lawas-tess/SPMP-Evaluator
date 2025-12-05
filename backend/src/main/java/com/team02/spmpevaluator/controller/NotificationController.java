package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.entity.Notification;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.NotificationService;
import com.team02.spmpevaluator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for notification management.
 * Students can view and manage their notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * Get all notifications for the current user.
     */
    @GetMapping
    public ResponseEntity<?> getMyNotifications() {
        try {
            Long userId = getCurrentUserId();
            List<Notification> notifications = notificationService.getNotificationsForUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get notifications: " + e.getMessage());
        }
    }

    /**
     * Get unread notifications for the current user.
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications() {
        try {
            Long userId = getCurrentUserId();
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get notifications: " + e.getMessage());
        }
    }

    /**
     * Get notification count (unread).
     */
    @GetMapping("/count")
    public ResponseEntity<?> getNotificationCount() {
        try {
            Long userId = getCurrentUserId();
            long count = notificationService.countUnreadNotifications(userId);
            Map<String, Long> response = new HashMap<>();
            response.put("unreadCount", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get notification count: " + e.getMessage());
        }
    }

    /**
     * Mark a notification as read.
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok().body("Notification marked as read");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to mark notification: " + e.getMessage());
        }
    }

    /**
     * Mark all notifications as read.
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        try {
            Long userId = getCurrentUserId();
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok().body("All notifications marked as read");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to mark notifications: " + e.getMessage());
        }
    }

    /**
     * Helper method to get current user ID.
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }
}
