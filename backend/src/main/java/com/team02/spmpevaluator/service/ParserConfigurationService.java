package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.ParserConfiguration;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.ParserConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing parser configurations including IEEE 1058 clause mappings
 * and custom evaluation rules.
 */
@Service
@RequiredArgsConstructor
public class ParserConfigurationService {

    private final ParserConfigurationRepository parserConfigurationRepository;

    /**
     * Create a new parser configuration
     */
    @Transactional
    public ParserConfiguration createConfiguration(ParserConfiguration configuration, User createdBy) {
        configuration.setCreatedBy(createdBy);
        
        // If this is set as default, unset other defaults
        if (Boolean.TRUE.equals(configuration.getIsDefault())) {
            unsetAllDefaults();
        }
        
        return parserConfigurationRepository.save(configuration);
    }

    /**
     * Update an existing parser configuration
     */
    @Transactional
    public ParserConfiguration updateConfiguration(Long id, ParserConfiguration updatedConfig) {
        ParserConfiguration existing = parserConfigurationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parser configuration not found with id: " + id));
        
        existing.setName(updatedConfig.getName());
        existing.setDescription(updatedConfig.getDescription());
        existing.setClauseMappings(updatedConfig.getClauseMappings());
        existing.setCustomRules(updatedConfig.getCustomRules());
        existing.setIsActive(updatedConfig.getIsActive());
        
        // If this is being set as default, unset other defaults
        if (Boolean.TRUE.equals(updatedConfig.getIsDefault()) && !Boolean.TRUE.equals(existing.getIsDefault())) {
            unsetAllDefaults();
        }
        existing.setIsDefault(updatedConfig.getIsDefault());
        
        return parserConfigurationRepository.save(existing);
    }

    /**
     * Get a parser configuration by ID
     */
    public Optional<ParserConfiguration> getConfigurationById(Long id) {
        return parserConfigurationRepository.findById(id);
    }

    /**
     * Get all active parser configurations
     */
    public List<ParserConfiguration> getActiveConfigurations() {
        return parserConfigurationRepository.findByIsActiveTrue();
    }

    /**
     * Get all parser configurations created by a specific user
     */
    public List<ParserConfiguration> getConfigurationsByUser(User user) {
        return parserConfigurationRepository.findByCreatedByOrderByCreatedAtDesc(user);
    }

    /**
     * Get the default parser configuration
     */
    public Optional<ParserConfiguration> getDefaultConfiguration() {
        return parserConfigurationRepository.findByIsDefaultTrue();
    }

    /**
     * Delete a parser configuration
     */
    @Transactional
    public void deleteConfiguration(Long id) {
        ParserConfiguration config = parserConfigurationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parser configuration not found with id: " + id));
        
        if (Boolean.TRUE.equals(config.getIsDefault())) {
            throw new RuntimeException("Cannot delete the default parser configuration");
        }
        
        parserConfigurationRepository.deleteById(id);
    }

    /**
     * Set a configuration as default
     */
    @Transactional
    public ParserConfiguration setAsDefault(Long id) {
        ParserConfiguration config = parserConfigurationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parser configuration not found with id: " + id));
        
        unsetAllDefaults();
        config.setIsDefault(true);
        return parserConfigurationRepository.save(config);
    }

    /**
     * Create a default IEEE 1058 configuration if none exists
     */
    @Transactional
    public ParserConfiguration createDefaultConfiguration(User createdBy) {
        Optional<ParserConfiguration> existing = getDefaultConfiguration();
        if (existing.isPresent()) {
            return existing.get();
        }

        ParserConfiguration defaultConfig = new ParserConfiguration();
        defaultConfig.setName("IEEE 1058 Standard Configuration");
        defaultConfig.setDescription("Default configuration based on IEEE 1058-1998 standard for SPMP documents");
        defaultConfig.setClauseMappings(getDefaultClauseMappings());
        defaultConfig.setCustomRules(getDefaultCustomRules());
        defaultConfig.setIsActive(true);
        defaultConfig.setIsDefault(true);
        defaultConfig.setCreatedBy(createdBy);

        return parserConfigurationRepository.save(defaultConfig);
    }

    /**
     * Unset all default configurations
     */
    private void unsetAllDefaults() {
        Optional<ParserConfiguration> currentDefault = parserConfigurationRepository.findByIsDefaultTrue();
        currentDefault.ifPresent(config -> {
            config.setIsDefault(false);
            parserConfigurationRepository.save(config);
        });
    }

    /**
     * Get default IEEE 1058 clause mappings as JSON string
     */
    private String getDefaultClauseMappings() {
        return """
                [
                    {"clauseId": "1", "clauseName": "Overview", "weight": 10, "keywords": ["overview", "introduction", "purpose"]},
                    {"clauseId": "1.1", "clauseName": "Project Overview", "weight": 5, "keywords": ["project overview", "summary"]},
                    {"clauseId": "1.2", "clauseName": "Project Deliverables", "weight": 5, "keywords": ["deliverables", "outputs", "products"]},
                    {"clauseId": "2", "clauseName": "References", "weight": 5, "keywords": ["references", "bibliography", "citations"]},
                    {"clauseId": "3", "clauseName": "Definitions", "weight": 5, "keywords": ["definitions", "terminology", "glossary"]},
                    {"clauseId": "4", "clauseName": "Project Organization", "weight": 15, "keywords": ["organization", "structure", "roles", "responsibilities"]},
                    {"clauseId": "5", "clauseName": "Managerial Process Plans", "weight": 20, "keywords": ["management", "planning", "processes"]},
                    {"clauseId": "5.1", "clauseName": "Project Startup Plan", "weight": 5, "keywords": ["startup", "initiation", "kickoff"]},
                    {"clauseId": "5.2", "clauseName": "Work Plan", "weight": 10, "keywords": ["work plan", "schedule", "timeline", "milestones"]},
                    {"clauseId": "5.3", "clauseName": "Control Plan", "weight": 5, "keywords": ["control", "monitoring", "tracking"]},
                    {"clauseId": "6", "clauseName": "Technical Process Plans", "weight": 20, "keywords": ["technical", "development", "methodology"]},
                    {"clauseId": "7", "clauseName": "Supporting Process Plans", "weight": 15, "keywords": ["supporting", "quality", "configuration", "documentation"]},
                    {"clauseId": "8", "clauseName": "Additional Plans", "weight": 5, "keywords": ["additional", "appendices", "supplements"]}
                ]
                """;
    }

    /**
     * Get default custom evaluation rules as JSON string
     */
    private String getDefaultCustomRules() {
        return """
                [
                    {"ruleId": "R1", "description": "Check clause completeness", "criteria": "All required clauses must be present", "severity": "high"},
                    {"ruleId": "R2", "description": "Verify section depth", "criteria": "Key sections must have detailed sub-sections", "severity": "medium"},
                    {"ruleId": "R3", "description": "Check formatting consistency", "criteria": "Document formatting should follow IEEE standards", "severity": "low"}
                ]
                """;
    }
}
