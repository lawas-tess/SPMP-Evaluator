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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType action;

    @Column(nullable = false)
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
        LOGIN, LOGOUT, UPLOAD, DOWNLOAD, EVALUATE, OVERRIDE, CREATE, UPDATE, DELETE
    }

    public enum ResourceType {
        USER, DOCUMENT, TASK, EVALUATION
    }
}
