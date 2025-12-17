package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuditLog entity.
 */
@DisplayName("AuditLog Entity Tests")
class AuditLogTest {

    private AuditLog auditLog;
    private User user;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog();
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            AuditLog entity = new AuditLog();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getUser());
            assertNull(entity.getAction());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime now = LocalDateTime.now();
            AuditLog entity = new AuditLog(
                    1L, user, AuditLog.ActionType.LOGIN,
                    AuditLog.ResourceType.USER, 1L,
                    "User logged in", "192.168.1.1", now);

            assertEquals(1L, entity.getId());
            assertEquals(user, entity.getUser());
            assertEquals(AuditLog.ActionType.LOGIN, entity.getAction());
            assertEquals(AuditLog.ResourceType.USER, entity.getResourceType());
            assertEquals(1L, entity.getResourceId());
            assertEquals("User logged in", entity.getDetails());
            assertEquals("192.168.1.1", entity.getIpAddress());
            assertEquals(now, entity.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            auditLog.setId(100L);
            assertEquals(100L, auditLog.getId());
        }

        @Test
        @DisplayName("Should set and get user")
        void setAndGetUser() {
            auditLog.setUser(user);
            assertEquals(user, auditLog.getUser());
        }

        @Test
        @DisplayName("Should set and get action")
        void setAndGetAction() {
            auditLog.setAction(AuditLog.ActionType.UPLOAD);
            assertEquals(AuditLog.ActionType.UPLOAD, auditLog.getAction());
        }

        @Test
        @DisplayName("Should set and get resourceType")
        void setAndGetResourceType() {
            auditLog.setResourceType(AuditLog.ResourceType.DOCUMENT);
            assertEquals(AuditLog.ResourceType.DOCUMENT, auditLog.getResourceType());
        }

        @Test
        @DisplayName("Should set and get resourceId")
        void setAndGetResourceId() {
            auditLog.setResourceId(50L);
            assertEquals(50L, auditLog.getResourceId());
        }

        @Test
        @DisplayName("Should set and get details")
        void setAndGetDetails() {
            auditLog.setDetails("Document uploaded successfully");
            assertEquals("Document uploaded successfully", auditLog.getDetails());
        }

        @Test
        @DisplayName("Should set and get ipAddress")
        void setAndGetIpAddress() {
            auditLog.setIpAddress("10.0.0.1");
            assertEquals("10.0.0.1", auditLog.getIpAddress());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void setAndGetCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            auditLog.setCreatedAt(now);
            assertEquals(now, auditLog.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("ActionType Enum Tests")
    class ActionTypeEnumTests {

        @Test
        @DisplayName("Should have authentication actions")
        void authenticationActions() {
            assertEquals("LOGIN", AuditLog.ActionType.LOGIN.name());
            assertEquals("LOGOUT", AuditLog.ActionType.LOGOUT.name());
            assertEquals("REGISTER", AuditLog.ActionType.REGISTER.name());
        }

        @Test
        @DisplayName("Should have document actions")
        void documentActions() {
            assertEquals("UPLOAD", AuditLog.ActionType.UPLOAD.name());
            assertEquals("DOWNLOAD", AuditLog.ActionType.DOWNLOAD.name());
            assertEquals("VIEW", AuditLog.ActionType.VIEW.name());
            assertEquals("EVALUATE", AuditLog.ActionType.EVALUATE.name());
            assertEquals("OVERRIDE", AuditLog.ActionType.OVERRIDE.name());
        }

        @Test
        @DisplayName("Should have CRUD actions")
        void crudActions() {
            assertEquals("CREATE", AuditLog.ActionType.CREATE.name());
            assertEquals("UPDATE", AuditLog.ActionType.UPDATE.name());
            assertEquals("DELETE", AuditLog.ActionType.DELETE.name());
        }

        @Test
        @DisplayName("Should have admin actions")
        void adminActions() {
            assertEquals("ASSIGN", AuditLog.ActionType.ASSIGN.name());
            assertEquals("RESET_PASSWORD", AuditLog.ActionType.RESET_PASSWORD.name());
            assertEquals("LOCK_ACCOUNT", AuditLog.ActionType.LOCK_ACCOUNT.name());
            assertEquals("UNLOCK_ACCOUNT", AuditLog.ActionType.UNLOCK_ACCOUNT.name());
        }

        @Test
        @DisplayName("Should have system actions")
        void systemActions() {
            assertEquals("EXPORT", AuditLog.ActionType.EXPORT.name());
            assertEquals("IMPORT", AuditLog.ActionType.IMPORT.name());
        }

        @Test
        @DisplayName("Should have correct number of action types")
        void actionTypeCount() {
            assertEquals(17, AuditLog.ActionType.values().length);
        }
    }

    @Nested
    @DisplayName("ResourceType Enum Tests")
    class ResourceTypeEnumTests {

        @Test
        @DisplayName("Should have all resource types")
        void allResourceTypes() {
            assertEquals("USER", AuditLog.ResourceType.USER.name());
            assertEquals("DOCUMENT", AuditLog.ResourceType.DOCUMENT.name());
            assertEquals("TASK", AuditLog.ResourceType.TASK.name());
            assertEquals("EVALUATION", AuditLog.ResourceType.EVALUATION.name());
            assertEquals("GRADING_CRITERIA", AuditLog.ResourceType.GRADING_CRITERIA.name());
            assertEquals("NOTIFICATION", AuditLog.ResourceType.NOTIFICATION.name());
            assertEquals("SYSTEM", AuditLog.ResourceType.SYSTEM.name());
        }

        @Test
        @DisplayName("Should have correct number of resource types")
        void resourceTypeCount() {
            assertEquals(7, AuditLog.ResourceType.values().length);
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            LocalDateTime now = LocalDateTime.now();
            AuditLog log1 = new AuditLog(1L, user, AuditLog.ActionType.LOGIN,
                    AuditLog.ResourceType.USER, 1L, "details", "127.0.0.1", now);
            AuditLog log2 = new AuditLog(1L, user, AuditLog.ActionType.LOGIN,
                    AuditLog.ResourceType.USER, 1L, "details", "127.0.0.1", now);

            assertEquals(log1, log2);
            assertEquals(log1.hashCode(), log2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different IDs")
        void equals_DifferentIds_ReturnsFalse() {
            AuditLog log1 = new AuditLog();
            log1.setId(1L);
            AuditLog log2 = new AuditLog();
            log2.setId(2L);

            assertNotEquals(log1, log2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with fields")
        void toString_ContainsFields() {
            auditLog.setId(1L);
            auditLog.setAction(AuditLog.ActionType.LOGIN);
            auditLog.setResourceType(AuditLog.ResourceType.USER);

            String result = auditLog.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("action=LOGIN"));
            assertTrue(result.contains("resourceType=USER"));
        }
    }
}
