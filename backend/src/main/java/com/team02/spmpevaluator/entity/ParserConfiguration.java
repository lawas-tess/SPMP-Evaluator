package com.team02.spmpevaluator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing parser configuration for IEEE 1058 clause detection
 * and custom rule mappings for SPMP document evaluation.
 */
@Entity
@Table(name = "parser_configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParserConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    /**
     * IEEE 1058 clauses mapped to their detection rules
     * Stored as JSON string for flexibility
     * Format: [{"clauseId": "1.1", "clauseName": "Purpose", "weight": 10, "keywords": ["purpose", "objective"]}]
     */
    @Column(columnDefinition = "TEXT")
    private String clauseMappings;

    /**
     * Custom evaluation rules
     * Format: [{"ruleId": "R1", "description": "Check completeness", "criteria": "..."}]
     */
    @Column(columnDefinition = "TEXT")
    private String customRules;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * Whether this is the default configuration for new documents
     */
    @Column(nullable = false)
    private Boolean isDefault = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
