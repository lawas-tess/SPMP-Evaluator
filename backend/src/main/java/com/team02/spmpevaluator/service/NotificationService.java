package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.Notification;
import com.team02.spmpevaluator.entity.Notification.NotificationType;
import com.team02.spmpevaluator.entity.Task;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.NotificationRepository;
import com.team02.spmpevaluator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing notifications.
 * UC 2.6 Step 5: System saves task and notifies students
 * UC 2.8 Step 5: System saves override and notifies student
 * UC 2.9 Step 5: System saves and notifies affected students
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Create a notification for a user.
     */
    public Notification createNotification(Long userId, String title, String message, 
                                           NotificationType type, Long resourceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setResourceId(resourceId);
        notification.setRead(false);

        return notificationRepository.save(notification);
    }

    /**
     * UC 2.6: Notify student of new task assignment.
     */
    public void notifyTaskAssigned(Task task) {
        if (task.getAssignedTo() == null) return;
        
        String title = "New Task Assigned: " + task.getTitle();
        String message = String.format(
            "A new task '%s' has been assigned to you by %s. Due: %s",
            task.getTitle(),
            task.getCreatedBy().getFirstName() + " " + task.getCreatedBy().getLastName(),
            task.getDeadline() != null ? task.getDeadline().toString() : "No deadline"
        );

        createNotification(
            task.getAssignedTo().getId(),
            title,
            message,
            NotificationType.TASK_ASSIGNED,
            task.getId()
        );
    }

    /**
     * UC 2.9: Notify student of task update.
     */
    public void notifyTaskUpdated(Task task) {
        if (task.getAssignedTo() == null) return;

        String title = "Task Updated: " + task.getTitle();
        String message = String.format(
            "The task '%s' has been updated. Please check for new instructions or deadlines.",
            task.getTitle()
        );

        createNotification(
            task.getAssignedTo().getId(),
            title,
            message,
            NotificationType.TASK_UPDATED,
            task.getId()
        );
    }

    /**
     * UC 2.8: Notify student of score override.
     */
    public void notifyScoreOverride(Long studentId, Long documentId, Double newScore, String justification) {
        String title = "Score Updated";
        String message = String.format(
            "Your document evaluation score has been updated to %.2f by a professor.%s",
            newScore,
            justification != null && !justification.isEmpty() ? " Notes: " + justification : ""
        );

        createNotification(
            studentId,
            title,
            message,
            NotificationType.SCORE_OVERRIDE,
            documentId
        );
    }

    /**
     * Get all notifications for a user.
     */
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get unread notifications for a user.
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * Count unread notifications for a user.
     */
    public long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    /**
     * Mark a notification as read.
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read for a user.
     */
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
