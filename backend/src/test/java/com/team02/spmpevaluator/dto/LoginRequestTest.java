package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoginRequest.
 */
@DisplayName("LoginRequest Tests")
class LoginRequestTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            LoginRequest dto = new LoginRequest();

            assertNotNull(dto);
            assertNull(dto.getUsername());
            assertNull(dto.getPassword());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            LoginRequest dto = new LoginRequest("testuser", "password123");

            assertEquals("testuser", dto.getUsername());
            assertEquals("password123", dto.getPassword());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get username")
        void setAndGetUsername() {
            LoginRequest dto = new LoginRequest();
            dto.setUsername("admin");
            assertEquals("admin", dto.getUsername());
        }

        @Test
        @DisplayName("Should set and get password")
        void setAndGetPassword() {
            LoginRequest dto = new LoginRequest();
            dto.setPassword("securePassword");
            assertEquals("securePassword", dto.getPassword());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty username")
        void emptyUsername() {
            LoginRequest dto = new LoginRequest("", "password");
            assertEquals("", dto.getUsername());
        }

        @Test
        @DisplayName("Should handle empty password")
        void emptyPassword() {
            LoginRequest dto = new LoginRequest("user", "");
            assertEquals("", dto.getPassword());
        }

        @Test
        @DisplayName("Should handle null values")
        void nullValues() {
            LoginRequest dto = new LoginRequest(null, null);
            assertNull(dto.getUsername());
            assertNull(dto.getPassword());
        }

        @Test
        @DisplayName("Should handle special characters in username")
        void specialCharactersInUsername() {
            LoginRequest dto = new LoginRequest("user@domain.com", "password");
            assertEquals("user@domain.com", dto.getUsername());
        }

        @Test
        @DisplayName("Should handle special characters in password")
        void specialCharactersInPassword() {
            LoginRequest dto = new LoginRequest("user", "P@$$w0rd!#%");
            assertEquals("P@$$w0rd!#%", dto.getPassword());
        }

        @Test
        @DisplayName("Should handle unicode characters")
        void unicodeCharacters() {
            LoginRequest dto = new LoginRequest("用户", "密码");
            assertEquals("用户", dto.getUsername());
            assertEquals("密码", dto.getPassword());
        }

        @Test
        @DisplayName("Should handle whitespace")
        void whitespaceHandling() {
            LoginRequest dto = new LoginRequest("  user  ", "  pass  ");
            assertEquals("  user  ", dto.getUsername());
            assertEquals("  pass  ", dto.getPassword());
        }
    }

    @Nested
    @DisplayName("Modification Tests")
    class ModificationTests {

        @Test
        @DisplayName("Should allow modifying username after construction")
        void modifyUsername() {
            LoginRequest dto = new LoginRequest("original", "password");
            dto.setUsername("modified");
            assertEquals("modified", dto.getUsername());
        }

        @Test
        @DisplayName("Should allow modifying password after construction")
        void modifyPassword() {
            LoginRequest dto = new LoginRequest("user", "original");
            dto.setPassword("modified");
            assertEquals("modified", dto.getPassword());
        }
    }
}
