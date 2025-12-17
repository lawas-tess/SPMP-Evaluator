package com.team02.spmpevaluator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingDTO {
    private Long id;
    
    @JsonProperty("key")
    private String settingKey;
    
    @JsonProperty("value")
    private String settingValue;
    
    private String category;
    private String description;
    
    @JsonProperty("type")
    private String dataType;
    
    private String updatedBy;
    private String updatedAt;
}
