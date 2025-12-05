package com.team02.spmpevaluator.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for GradingCriteria entity.
 * UC 2.7 - Data transfer object for grading criteria configuration.
 */
public class GradingCriteriaDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private Long createdById;
    private String createdByName;

    private boolean isActive;

    // IEEE 1058 Section Weights (should sum to 100)
    @Min(value = 0, message = "Weight must be at least 0")
    @Max(value = 100, message = "Weight cannot exceed 100")
    private Integer overviewWeight = 10;

    @Min(value = 0, message = "Weight must be at least 0")
    @Max(value = 100, message = "Weight cannot exceed 100")
    private Integer referencesWeight = 5;

    @Min(value = 0, message = "Weight must be at least 0")
    @Max(value = 100, message = "Weight cannot exceed 100")
    private Integer definitionsWeight = 5;

    @Min(value = 0, message = "Weight must be at least 0")
    @Max(value = 100, message = "Weight cannot exceed 100")
    private Integer organizationWeight = 15;

    @Min(value = 0, message = "Weight must be at least 0")
    @Max(value = 100, message = "Weight cannot exceed 100")
    private Integer managerialProcessWeight = 20;

    @Min(value = 0, message = "Weight must be at least 0")
    @Max(value = 100, message = "Weight cannot exceed 100")
    private Integer technicalProcessWeight = 20;

    @Min(value = 0, message = "Weight must be at least 0")
    @Max(value = 100, message = "Weight cannot exceed 100")
    private Integer supportingProcessWeight = 15;

    @Min(value = 0, message = "Weight must be at least 0")
    @Max(value = 100, message = "Weight cannot exceed 100")
    private Integer additionalPlansWeight = 10;

    private String createdAt;
    private String updatedAt;

    public GradingCriteriaDTO() {
    }

    public GradingCriteriaDTO(Long id, String name, String description, Long createdById, 
                              String createdByName, boolean isActive,
                              Integer overviewWeight, Integer referencesWeight, 
                              Integer definitionsWeight, Integer organizationWeight,
                              Integer managerialProcessWeight, Integer technicalProcessWeight,
                              Integer supportingProcessWeight, Integer additionalPlansWeight,
                              String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdById = createdById;
        this.createdByName = createdByName;
        this.isActive = isActive;
        this.overviewWeight = overviewWeight;
        this.referencesWeight = referencesWeight;
        this.definitionsWeight = definitionsWeight;
        this.organizationWeight = organizationWeight;
        this.managerialProcessWeight = managerialProcessWeight;
        this.technicalProcessWeight = technicalProcessWeight;
        this.supportingProcessWeight = supportingProcessWeight;
        this.additionalPlansWeight = additionalPlansWeight;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Calculate total weight - should sum to 100.
     */
    public int getTotalWeight() {
        return (overviewWeight != null ? overviewWeight : 0) + 
               (referencesWeight != null ? referencesWeight : 0) + 
               (definitionsWeight != null ? definitionsWeight : 0) + 
               (organizationWeight != null ? organizationWeight : 0) +
               (managerialProcessWeight != null ? managerialProcessWeight : 0) + 
               (technicalProcessWeight != null ? technicalProcessWeight : 0) + 
               (supportingProcessWeight != null ? supportingProcessWeight : 0) + 
               (additionalPlansWeight != null ? additionalPlansWeight : 0);
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

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
