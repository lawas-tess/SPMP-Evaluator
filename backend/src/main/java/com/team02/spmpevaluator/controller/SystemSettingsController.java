package com.team02.spmpevaluator.controller;

import com.team02.spmpevaluator.dto.SystemSettingDTO;
import com.team02.spmpevaluator.entity.SystemSetting;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.SystemSettingService;
import com.team02.spmpevaluator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for admin system settings management.
 * UC 2.15: Admin System Settings
 */
@RestController
@RequestMapping("/api/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class SystemSettingsController {
    
    private final SystemSettingService settingService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllSettings(@RequestParam(required = false) String category) {
        try {
            List<SystemSetting> settings = category != null ?
                    settingService.getSettingsByCategory(category) :
                    settingService.getAllSettings();
            
            List<SystemSettingDTO> dtos = settings.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve settings: " + e.getMessage());
        }
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> getSettingByKey(@PathVariable String key) {
        try {
            SystemSetting setting = settingService.getSettingByKey(key);
            return ResponseEntity.ok(convertToDTO(setting));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Setting not found: " + e.getMessage());
        }
    }

    @PutMapping("/{key}")
    public ResponseEntity<?> updateSetting(@PathVariable String key, 
                                          @RequestBody Map<String, String> request,
                                          Authentication authentication) {
        try {
            String value = request.get("value");
            String username = authentication.getName();
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            SystemSetting updated = settingService.updateSetting(key, value, admin.getId());
            return ResponseEntity.ok(convertToDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to update setting: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> updateMultipleSettings(@RequestBody Map<String, Object> request,
                                                     Authentication authentication) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> settingsList = (List<Map<String, String>>) request.get("settings");
            
            String username = authentication.getName();
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            List<SystemSetting> updatedSettings = settingsList.stream()
                    .map(setting -> settingService.updateSetting(
                            setting.get("key"), 
                            setting.get("value"), 
                            admin.getId()))
                    .collect(Collectors.toList());
            
            List<SystemSettingDTO> dtos = updatedSettings.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to update settings: " + e.getMessage());
        }
    }

    @PostMapping("/maintenance")
    public ResponseEntity<?> toggleMaintenanceMode(@RequestBody Map<String, Boolean> request,
                                                   Authentication authentication) {
        try {
            boolean enabled = request.get("enabled");
            String username = authentication.getName();
            User admin = userService.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

            SystemSetting setting = settingService.updateSetting("maintenance.mode", 
                    String.valueOf(enabled), admin.getId());
            
            return ResponseEntity.ok(Map.of(
                    "maintenanceMode", enabled,
                    "updatedBy", admin.getFirstName() + " " + admin.getLastName(),
                    "updatedAt", setting.getUpdatedAt().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to toggle maintenance mode: " + e.getMessage());
        }
    }

    private SystemSettingDTO convertToDTO(SystemSetting setting) {
        SystemSettingDTO dto = new SystemSettingDTO();
        dto.setId(setting.getId());
        dto.setSettingKey(setting.getSettingKey());
        dto.setSettingValue(setting.getSettingValue());
        dto.setCategory(setting.getCategory());
        dto.setDescription(setting.getDescription());
        dto.setDataType(setting.getDataType());
        if (setting.getUpdatedBy() != null) {
            dto.setUpdatedBy(setting.getUpdatedBy().getFirstName() + " " + setting.getUpdatedBy().getLastName());
        }
        dto.setUpdatedAt(setting.getUpdatedAt().toString());
        return dto;
    }
}
