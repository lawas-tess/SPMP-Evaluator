package com.team02.spmpevaluator.repository;

import com.team02.spmpevaluator.entity.Notification;
import com.team02.spmpevaluator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Get all notifications for a user, ordered by creation date descending.
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Get all notifications for a user by ID.
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Get unread notifications for a user.
     */
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);
    
    /**
     * Delete all notifications for a user.
     */
    void deleteByUserId(Long userId);
    
    /**
     * Count unread notifications for a user.
     */
    long countByUserIdAndReadFalse(Long userId);
}
