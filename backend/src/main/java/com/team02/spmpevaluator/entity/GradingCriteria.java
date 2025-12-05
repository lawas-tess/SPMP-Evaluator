package com.team02.spmpevaluator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing grading criteria configuration for SPMP document evaluation.
 * UC 2.7 - Professors can set and customize grading criteria.
 * 
 * Stores section weights based on IEEE 1058 standard sections for SPMP documents.
 */
@Entity
@Table(name = "grading_criteria")
public class GradingCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of this grading criteria configuration.
     * e.g., "Default", "Midterm Evaluation", "Final Submission"
     */
    @Column(nullable = false)
    private String name;

    /**
     * Optional description of this grading criteria.
     */
    @Column(length = 500)
    private String description;

    /**
     * Professor who created this grading criteria.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * Whether this is the active/default criteria for the professor.
     */
    @Column(nullable = false)
    private boolean isActive = false;

    // IEEE 1058 Section Weights (should sum to 100)
    
    /**
     * Section 1: Overview - Project overview and summary
     */
    @Column(nullable = false)
    private Integer overviewWeight = 10;

    /**
     * Section 2: References - Referenced documents and standards
     */
    @Column(nullable = false)
    private Integer referencesWeight = 5;

    /**
     * Section 3: Definitions - Terms and abbreviations
     */
    @Column(nullable = false)
    private Integer definitionsWeight = 5;

    /**
     * Section 4: Project Organization - Team structure and responsibilities
     */
    @Column(nullable = false)
    private Integer organizationWeight = 15;

    /**
     * Section 5: Managerial Process - Management objectives and priorities
     */
    @Column(nullable = false)
    private Integer managerialProcessWeight = 20;

    /**
     * Section 6: Technical Process - Development methods and tools
     */
    @Column(nullable = false)
    private Integer technicalProcessWeight = 20;

    /**
     * Section 7: Supporting Process - Configuration management, QA, etc.
     */
    @Column(nullable = false)
    private Integer supportingProcessWeight = 15;

    /**
     * Section 8: Additional Plans - Risk management, training, etc.
     */
    @Column(nullable = false)
    private Integer additionalPlansWeight = 10;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public GradingCriteria() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate total weight - should sum to 100.
     */
    public int getTotalWeight() {
        return overviewWeight + referencesWeight + definitionsWeight + organizationWeight +
               managerialProcessWeight + technicalProcessWeight + supportingProcessWeight + additionalPlansWeight;
    }

    /**
     * Validate that weights sum to 100.
     */
    public boolean isValidWeights() {
        return getTotalWeight() == 100;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getOverviewWeight() {
        return overviewWeight;
    }

    public void setOverviewWeight(Integer overviewWeight) {
        this.overviewWeight = overviewWeight;
    }

    public Integer getReferencesWeight() {
        return referencesWeight;
    }

    public void setReferencesWeight(Integer referencesWeight) {
        this.referencesWeight = referencesWeight;
    }

    public Integer getDefinitionsWeight() {
        return definitionsWeight;
    }

    public void setDefinitionsWeight(Integer definitionsWeight) {
        this.definitionsWeight = definitionsWeight;
    }

    public Integer getOrganizationWeight() {
        return organizationWeight;
    }

    public void setOrganizationWeight(Integer organizationWeight) {
        this.organizationWeight = organizationWeight;
    }

    public Integer getManagerialProcessWeight() {
        return managerialProcessWeight;
    }

    public void setManagerialProcessWeight(Integer managerialProcessWeight) {
        this.managerialProcessWeight = managerialProcessWeight;
    }

    public Integer getTechnicalProcessWeight() {
        return technicalProcessWeight;
    }

    public void setTechnicalProcessWeight(Integer technicalProcessWeight) {
        this.technicalProcessWeight = technicalProcessWeight;
    }

    public Integer getSupportingProcessWeight() {
        return supportingProcessWeight;
    }

    public void setSupportingProcessWeight(Integer supportingProcessWeight) {
        this.supportingProcessWeight = supportingProcessWeight;
    }

    public Integer getAdditionalPlansWeight() {
        return additionalPlansWeight;
    }

    public void setAdditionalPlansWeight(Integer additionalPlansWeight) {
        this.additionalPlansWeight = additionalPlansWeight;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
