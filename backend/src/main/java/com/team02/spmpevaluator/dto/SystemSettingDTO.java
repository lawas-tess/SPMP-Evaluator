package com.team02.spmpevaluator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingDTO {
    private Long id;
    private String settingKey;
    private String settingValue;
    private String category;
    private String description;
    private String dataType;
    private String updatedBy;
    private String updatedAt;
}
