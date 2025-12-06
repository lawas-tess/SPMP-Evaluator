package com.team02.spmpevaluator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Parser Configuration requests and responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParserConfigurationDTO {
    private Long id;
    private String name;
    private String description;
    private String clauseMappings;
    private String customRules;
    private Boolean isActive;
    private Boolean isDefault;
    private Long createdByUserId;
    private String createdByUsername;
    private String createdAt;
    private String updatedAt;
}
