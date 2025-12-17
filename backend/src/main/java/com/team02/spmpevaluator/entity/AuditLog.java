package com.team02.spmpevaluator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ActionType action;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(columnDefinition = "LONGTEXT")
    private String details;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ActionType {
        // Authentication Actions
        LOGIN, LOGOUT, REGISTER,
        // Document Actions
        UPLOAD, DOWNLOAD, VIEW, EVALUATE, OVERRIDE,
        // CRUD Actions
        CREATE, UPDATE, DELETE,
        // Admin Actions
        ASSIGN, RESET_PASSWORD, LOCK_ACCOUNT, UNLOCK_ACCOUNT,
        // System Actions
        EXPORT, IMPORT
    }

    public enum ResourceType {
        USER, DOCUMENT, TASK, EVALUATION, GRADING_CRITERIA, NOTIFICATION, SYSTEM
    }
}
