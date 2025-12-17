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
        SystemSetting setting = getSettingByKey(key);
        User updatedBy = userRepository.findById(updatedById)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        setting.setSettingValue(value);
        setting.setUpdatedBy(updatedBy);
        
        return settingRepository.save(setting);
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
