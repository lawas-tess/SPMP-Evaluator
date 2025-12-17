package com.team02.spmpevaluator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.SystemSetting;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.service.SystemSettingService;
import com.team02.spmpevaluator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for SystemSettingsController.
 * UC 2.15: Admin System Settings
 */
@SpringBootTest
@AutoConfigureMockMvc
class SystemSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SystemSettingService settingService;

    @MockBean
    private UserService userService;

    private User admin;
    private SystemSetting setting1;
    private SystemSetting setting2;
    private SystemSetting maintenanceSetting;
    private List<SystemSetting> allSettings;
    private List<SystemSetting> securitySettings;

    @BeforeEach
    void setUp() {
        // Create test admin
        admin = new User();
        admin.setId(1L);
        admin.setUsername("admin1");
        admin.setEmail("admin1@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);

        // Create test settings
        setting1 = new SystemSetting();
        setting1.setId(1L);
        setting1.setSettingKey("system.name");
        setting1.setSettingValue("SPMP Evaluator");
        setting1.setCategory("GENERAL");
        setting1.setDescription("System name displayed in UI");
        setting1.setDataType("STRING");
        setting1.setUpdatedBy(admin);
        setting1.setUpdatedAt(LocalDateTime.now());

        setting2 = new SystemSetting();
        setting2.setId(2L);
        setting2.setSettingKey("max.upload.size");
        setting2.setSettingValue("10485760");
        setting2.setCategory("UPLOAD");
        setting2.setDescription("Maximum file upload size in bytes");
        setting2.setDataType("INTEGER");
        setting2.setUpdatedBy(admin);
        setting2.setUpdatedAt(LocalDateTime.now());

        SystemSetting securitySetting = new SystemSetting();
        securitySetting.setId(3L);
        securitySetting.setSettingKey("session.timeout");
        securitySetting.setSettingValue("3600");
        securitySetting.setCategory("SECURITY");
        securitySetting.setDescription("Session timeout in seconds");
        securitySetting.setDataType("INTEGER");
        securitySetting.setUpdatedBy(admin);
        securitySetting.setUpdatedAt(LocalDateTime.now());

        maintenanceSetting = new SystemSetting();
        maintenanceSetting.setId(4L);
        maintenanceSetting.setSettingKey("maintenance.mode");
        maintenanceSetting.setSettingValue("false");
        maintenanceSetting.setCategory("SYSTEM");
        maintenanceSetting.setDescription("Enable/disable maintenance mode");
        maintenanceSetting.setDataType("BOOLEAN");
        maintenanceSetting.setUpdatedBy(admin);
        maintenanceSetting.setUpdatedAt(LocalDateTime.now());

        allSettings = Arrays.asList(setting1, setting2, securitySetting, maintenanceSetting);
        securitySettings = Arrays.asList(securitySetting);
    }

    // ============= GET All Settings Tests =============

    @Nested
    @DisplayName("GET /api/admin/settings")
    class GetAllSettings {

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return all settings successfully")
        void getAllSettings_Success() throws Exception {
            when(settingService.getAllSettings()).thenReturn(allSettings);

            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(4)))
                    .andExpect(jsonPath("$[0].key").value("system.name"))
                    .andExpect(jsonPath("$[0].value").value("SPMP Evaluator"))
                    .andExpect(jsonPath("$[0].category").value("GENERAL"));

            verify(settingService).getAllSettings();
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should filter settings by category")
        void getAllSettings_FilterByCategory() throws Exception {
            when(settingService.getSettingsByCategory("SECURITY")).thenReturn(securitySettings);

            mockMvc.perform(get("/api/admin/settings")
                    .param("category", "SECURITY"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].category").value("SECURITY"));

            verify(settingService).getSettingsByCategory("SECURITY");
            verify(settingService, never()).getAllSettings();
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return empty list when no settings exist")
        void getAllSettings_EmptyList() throws Exception {
            when(settingService.getAllSettings()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(settingService).getAllSettings();
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 500 when service throws exception")
        void getAllSettings_ServiceException() throws Exception {
            when(settingService.getAllSettings()).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to retrieve settings")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getAllSettings_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().isForbidden());

            verify(settingService, never()).getAllSettings();
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getAllSettings_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().isForbidden());

            verify(settingService, never()).getAllSettings();
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getAllSettings_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().is3xxRedirection());

            verify(settingService, never()).getAllSettings();
        }
    }

    // ============= GET Setting by Key Tests =============

    @Nested
    @DisplayName("GET /api/admin/settings/{key}")
    class GetSettingByKey {

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return setting by key successfully")
        void getSettingByKey_Success() throws Exception {
            when(settingService.getSettingByKey("system.name")).thenReturn(setting1);

            mockMvc.perform(get("/api/admin/settings/system.name"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.key").value("system.name"))
                    .andExpect(jsonPath("$.value").value("SPMP Evaluator"))
                    .andExpect(jsonPath("$.category").value("GENERAL"))
                    .andExpect(jsonPath("$.description").value("System name displayed in UI"))
                    .andExpect(jsonPath("$.type").value("STRING"));

            verify(settingService).getSettingByKey("system.name");
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 404 when setting not found")
        void getSettingByKey_NotFound() throws Exception {
            when(settingService.getSettingByKey("nonexistent.key"))
                    .thenThrow(new IllegalArgumentException("Setting not found: nonexistent.key"));

            mockMvc.perform(get("/api/admin/settings/nonexistent.key"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(containsString("Setting not found")));

            verify(settingService).getSettingByKey("nonexistent.key");
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void getSettingByKey_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(get("/api/admin/settings/system.name"))
                    .andExpect(status().isForbidden());

            verify(settingService, never()).getSettingByKey(anyString());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void getSettingByKey_ForbiddenForStudent() throws Exception {
            mockMvc.perform(get("/api/admin/settings/system.name"))
                    .andExpect(status().isForbidden());

            verify(settingService, never()).getSettingByKey(anyString());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void getSettingByKey_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/admin/settings/system.name"))
                    .andExpect(status().is3xxRedirection());

            verify(settingService, never()).getSettingByKey(anyString());
        }
    }

    // ============= PUT Update Single Setting Tests =============

    @Nested
    @DisplayName("PUT /api/admin/settings/{key}")
    class UpdateSetting {

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should update setting successfully")
        void updateSetting_Success() throws Exception {
            setting1.setSettingValue("New System Name");

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(settingService.updateSetting(eq("system.name"), eq("New System Name"), eq(1L)))
                    .thenReturn(setting1);

            mockMvc.perform(put("/api/admin/settings/system.name")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("value", "New System Name"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.key").value("system.name"))
                    .andExpect(jsonPath("$.value").value("New System Name"));

            verify(userService).findByUsername("admin1");
            verify(settingService).updateSetting("system.name", "New System Name", 1L);
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 400 when admin not found")
        void updateSetting_AdminNotFound() throws Exception {
            when(userService.findByUsername("admin1")).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/admin/settings/system.name")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("value", "New Value"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to update setting")));

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 400 when update fails")
        void updateSetting_ServiceException() throws Exception {
            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(settingService.updateSetting(anyString(), anyString(), anyLong()))
                    .thenThrow(new RuntimeException("Update failed"));

            mockMvc.perform(put("/api/admin/settings/system.name")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("value", "New Value"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to update setting")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void updateSetting_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(put("/api/admin/settings/system.name")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("value", "New Value"))))
                    .andExpect(status().isForbidden());

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void updateSetting_ForbiddenForStudent() throws Exception {
            mockMvc.perform(put("/api/admin/settings/system.name")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("value", "New Value"))))
                    .andExpect(status().isForbidden());

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void updateSetting_Unauthenticated() throws Exception {
            mockMvc.perform(put("/api/admin/settings/system.name")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("value", "New Value"))))
                    .andExpect(status().is3xxRedirection());

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }
    }

    // ============= POST Update Multiple Settings Tests =============

    @Nested
    @DisplayName("POST /api/admin/settings")
    class UpdateMultipleSettings {

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should update multiple settings successfully")
        void updateMultipleSettings_Success() throws Exception {
            List<Map<String, String>> settingsList = Arrays.asList(
                    Map.of("key", "system.name", "value", "New Name"),
                    Map.of("key", "max.upload.size", "value", "20971520"));

            setting1.setSettingValue("New Name");
            setting2.setSettingValue("20971520");

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(settingService.updateSetting("system.name", "New Name", 1L)).thenReturn(setting1);
            when(settingService.updateSetting("max.upload.size", "20971520", 1L)).thenReturn(setting2);

            mockMvc.perform(post("/api/admin/settings")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("settings", settingsList))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].value").value("New Name"))
                    .andExpect(jsonPath("$[1].value").value("20971520"));

            verify(settingService).updateSetting("system.name", "New Name", 1L);
            verify(settingService).updateSetting("max.upload.size", "20971520", 1L);
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 400 when admin not found")
        void updateMultipleSettings_AdminNotFound() throws Exception {
            List<Map<String, String>> settingsList = Arrays.asList(
                    Map.of("key", "system.name", "value", "New Name"));

            when(userService.findByUsername("admin1")).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/admin/settings")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("settings", settingsList))))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to update settings")));

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 400 when update fails")
        void updateMultipleSettings_ServiceException() throws Exception {
            List<Map<String, String>> settingsList = Arrays.asList(
                    Map.of("key", "system.name", "value", "New Name"));

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(settingService.updateSetting(anyString(), anyString(), anyLong()))
                    .thenThrow(new RuntimeException("Update failed"));

            mockMvc.perform(post("/api/admin/settings")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("settings", settingsList))))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Failed to update settings")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void updateMultipleSettings_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(post("/api/admin/settings")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("settings", Collections.emptyList()))))
                    .andExpect(status().isForbidden());

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void updateMultipleSettings_ForbiddenForStudent() throws Exception {
            mockMvc.perform(post("/api/admin/settings")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("settings", Collections.emptyList()))))
                    .andExpect(status().isForbidden());

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void updateMultipleSettings_Unauthenticated() throws Exception {
            mockMvc.perform(post("/api/admin/settings")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("settings", Collections.emptyList()))))
                    .andExpect(status().is3xxRedirection());

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }
    }

    // ============= POST Maintenance Mode Tests =============

    @Nested
    @DisplayName("POST /api/admin/settings/maintenance")
    class ToggleMaintenanceMode {

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should enable maintenance mode successfully")
        void toggleMaintenanceMode_Enable() throws Exception {
            maintenanceSetting.setSettingValue("true");

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(settingService.updateSetting("maintenance.mode", "true", 1L))
                    .thenReturn(maintenanceSetting);

            mockMvc.perform(post("/api/admin/settings/maintenance")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("enabled", true))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.maintenanceMode").value(true))
                    .andExpect(jsonPath("$.updatedBy").value("Admin User"))
                    .andExpect(jsonPath("$.updatedAt").exists());

            verify(settingService).updateSetting("maintenance.mode", "true", 1L);
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should disable maintenance mode successfully")
        void toggleMaintenanceMode_Disable() throws Exception {
            maintenanceSetting.setSettingValue("false");

            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(settingService.updateSetting("maintenance.mode", "false", 1L))
                    .thenReturn(maintenanceSetting);

            mockMvc.perform(post("/api/admin/settings/maintenance")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("enabled", false))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.maintenanceMode").value(false))
                    .andExpect(jsonPath("$.updatedBy").value("Admin User"));

            verify(settingService).updateSetting("maintenance.mode", "false", 1L);
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 500 when admin not found")
        void toggleMaintenanceMode_AdminNotFound() throws Exception {
            when(userService.findByUsername("admin1")).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/admin/settings/maintenance")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("enabled", true))))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to toggle maintenance mode")));

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return 500 when update fails")
        void toggleMaintenanceMode_ServiceException() throws Exception {
            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(settingService.updateSetting(anyString(), anyString(), anyLong()))
                    .thenThrow(new RuntimeException("Update failed"));

            mockMvc.perform(post("/api/admin/settings/maintenance")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("enabled", true))))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(containsString("Failed to toggle maintenance mode")));
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("Should return 403 for PROFESSOR role")
        void toggleMaintenanceMode_ForbiddenForProfessor() throws Exception {
            mockMvc.perform(post("/api/admin/settings/maintenance")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("enabled", true))))
                    .andExpect(status().isForbidden());

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should return 403 for STUDENT role")
        void toggleMaintenanceMode_ForbiddenForStudent() throws Exception {
            mockMvc.perform(post("/api/admin/settings/maintenance")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("enabled", true))))
                    .andExpect(status().isForbidden());

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }

        @Test
        @DisplayName("Should redirect to OAuth2 when not authenticated")
        void toggleMaintenanceMode_Unauthenticated() throws Exception {
            mockMvc.perform(post("/api/admin/settings/maintenance")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("enabled", true))))
                    .andExpect(status().is3xxRedirection());

            verify(settingService, never()).updateSetting(anyString(), anyString(), anyLong());
        }
    }

    // ============= Role-Based Access Control Tests =============

    @Nested
    @DisplayName("Role-Based Access Control Tests")
    class RoleBasedAccessTests {

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("ADMIN should have full access to all settings endpoints")
        void adminHasFullAccess() throws Exception {
            when(settingService.getAllSettings()).thenReturn(allSettings);
            when(settingService.getSettingByKey("system.name")).thenReturn(setting1);
            when(userService.findByUsername("admin1")).thenReturn(Optional.of(admin));
            when(settingService.updateSetting(anyString(), anyString(), anyLong())).thenReturn(setting1);

            // GET all settings
            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().isOk());

            // GET setting by key
            mockMvc.perform(get("/api/admin/settings/system.name"))
                    .andExpect(status().isOk());

            // PUT update setting
            mockMvc.perform(put("/api/admin/settings/system.name")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("value", "New Value"))))
                    .andExpect(status().isOk());

            // POST update multiple settings
            mockMvc.perform(post("/api/admin/settings")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(Map.of("settings",
                            Arrays.asList(Map.of("key", "system.name", "value", "Test"))))))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "PROFESSOR")
        @DisplayName("PROFESSOR should be denied access to all settings endpoints")
        void professorDeniedAccess() throws Exception {
            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/settings/system.name"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(put("/api/admin/settings/system.name")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(post("/api/admin/settings")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(post("/api/admin/settings/maintenance")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("STUDENT should be denied access to all settings endpoints")
        void studentDeniedAccess() throws Exception {
            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(get("/api/admin/settings/system.name"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(put("/api/admin/settings/system.name")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(post("/api/admin/settings")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isForbidden());

            mockMvc.perform(post("/api/admin/settings/maintenance")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isForbidden());
        }
    }

    // ============= Setting Types and Categories Tests =============

    @Nested
    @DisplayName("Setting Types and Categories Tests")
    class SettingTypesAndCategoriesTests {

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should handle different setting data types")
        void handleDifferentDataTypes() throws Exception {
            SystemSetting booleanSetting = createSetting(1L, "feature.enabled", "true", "FEATURES", "BOOLEAN");
            SystemSetting integerSetting = createSetting(2L, "max.connections", "100", "DATABASE", "INTEGER");
            SystemSetting stringSetting = createSetting(3L, "app.title", "My App", "GENERAL", "STRING");

            when(settingService.getAllSettings())
                    .thenReturn(Arrays.asList(booleanSetting, integerSetting, stringSetting));

            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].type").value("BOOLEAN"))
                    .andExpect(jsonPath("$[1].type").value("INTEGER"))
                    .andExpect(jsonPath("$[2].type").value("STRING"));
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should filter by multiple categories")
        void filterByMultipleCategories() throws Exception {
            SystemSetting security1 = createSetting(1L, "security.timeout", "3600", "SECURITY", "INTEGER");
            SystemSetting security2 = createSetting(2L, "security.max-attempts", "5", "SECURITY", "INTEGER");

            when(settingService.getSettingsByCategory("SECURITY"))
                    .thenReturn(Arrays.asList(security1, security2));

            mockMvc.perform(get("/api/admin/settings")
                    .param("category", "SECURITY"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].category", everyItem(is("SECURITY"))));
        }

        @Test
        @WithMockUser(username = "admin1", roles = "ADMIN")
        @DisplayName("Should return empty list for non-existent category")
        void filterByNonExistentCategory() throws Exception {
            when(settingService.getSettingsByCategory("NONEXISTENT"))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/admin/settings")
                    .param("category", "NONEXISTENT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ============= Helper Methods =============

    private SystemSetting createSetting(Long id, String key, String value, String category, String dataType) {
        SystemSetting setting = new SystemSetting();
        setting.setId(id);
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        setting.setCategory(category);
        setting.setDescription("Test setting");
        setting.setDataType(dataType);
        setting.setUpdatedBy(admin);
        setting.setUpdatedAt(LocalDateTime.now());
        return setting;
    }
}
