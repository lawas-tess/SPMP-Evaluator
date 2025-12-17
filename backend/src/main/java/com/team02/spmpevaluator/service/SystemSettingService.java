package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.SystemSetting;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.SystemSettingRepository;
import com.team02.spmpevaluator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing system settings.
 * UC 2.15: Admin System Settings
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SystemSettingService {
    
    private final SystemSettingRepository settingRepository;
    private final UserRepository userRepository;

    public SystemSetting getSettingByKey(String key) {
        return settingRepository.findBySettingKey(key)
                .orElseThrow(() -> new IllegalArgumentException("Setting not found: " + key));
    }

    public List<SystemSetting> getSettingsByCategory(String category) {
        return settingRepository.findByCategory(category);
    }

    public List<SystemSetting> getAllSettings() {
        return settingRepository.findAll();
    }

    public SystemSetting updateSetting(String key, String value, Long updatedById) {
        User updatedBy = userRepository.findById(updatedById)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Try to find existing setting, or create new one
        SystemSetting setting = settingRepository.findBySettingKey(key)
                .orElseGet(() -> {
                    SystemSetting newSetting = new SystemSetting();
                    newSetting.setSettingKey(key);
                    // Extract category from key (e.g., "ALLOW_STUDENT_REGISTRATION" -> "REGISTRATION")
                    String category = extractCategory(key);
                    newSetting.setCategory(category);
                    // Determine data type from value
                    newSetting.setDataType(determineDataType(value));
                    newSetting.setDescription("Auto-generated setting");
                    return newSetting;
                });
        
        setting.setSettingValue(value);
        setting.setUpdatedBy(updatedBy);
        
        return settingRepository.save(setting);
    }

    private String extractCategory(String key) {
        if (key.contains("_")) {
            String[] parts = key.split("_");
            if (parts.length > 1) {
                return parts[parts.length - 1];
            }
        }
        return "GENERAL";
    }

    private String determineDataType(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return "BOOLEAN";
        }
        try {
            Integer.parseInt(value);
            return "NUMBER";
        } catch (NumberFormatException e) {
            return "TEXT";
        }
    }

    public boolean getBooleanSetting(String key) {
        String value = getSettingByKey(key).getSettingValue();
        return Boolean.parseBoolean(value);
    }

    public int getIntegerSetting(String key) {
        String value = getSettingByKey(key).getSettingValue();
        return Integer.parseInt(value);
    }

    public String getStringSetting(String key) {
        return getSettingByKey(key).getSettingValue();
    }
}
