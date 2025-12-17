package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RegisterRequest.
 */
@DisplayName("RegisterRequest Tests")
class RegisterRequestTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            RegisterRequest dto = new RegisterRequest();

            assertNotNull(dto);
            assertNull(dto.getUsername());
            assertNull(dto.getEmail());
            assertNull(dto.getPassword());
            assertNull(dto.getFirstName());
            assertNull(dto.getLastName());
            assertNull(dto.getRole());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            RegisterRequest dto = new RegisterRequest(
                    "johndoe", "john@example.com", "password123",
                    "John", "Doe", "STUDENT");

            assertEquals("johndoe", dto.getUsername());
            assertEquals("john@example.com", dto.getEmail());
            assertEquals("password123", dto.getPassword());
            assertEquals("John", dto.getFirstName());
            assertEquals("Doe", dto.getLastName());
            assertEquals("STUDENT", dto.getRole());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get username")
        void setAndGetUsername() {
            RegisterRequest dto = new RegisterRequest();
            dto.setUsername("testuser");
            assertEquals("testuser", dto.getUsername());
        }

        @Test
        @DisplayName("Should set and get email")
        void setAndGetEmail() {
            RegisterRequest dto = new RegisterRequest();
            dto.setEmail("test@example.com");
            assertEquals("test@example.com", dto.getEmail());
        }

        @Test
        @DisplayName("Should set and get password")
        void setAndGetPassword() {
            RegisterRequest dto = new RegisterRequest();
            dto.setPassword("securePassword123");
            assertEquals("securePassword123", dto.getPassword());
        }

        @Test
        @DisplayName("Should set and get firstName")
        void setAndGetFirstName() {
            RegisterRequest dto = new RegisterRequest();
            dto.setFirstName("Jane");
            assertEquals("Jane", dto.getFirstName());
        }

        @Test
        @DisplayName("Should set and get lastName")
        void setAndGetLastName() {
            RegisterRequest dto = new RegisterRequest();
            dto.setLastName("Smith");
            assertEquals("Smith", dto.getLastName());
        }

        @Test
        @DisplayName("Should set and get role")
        void setAndGetRole() {
            RegisterRequest dto = new RegisterRequest();
            dto.setRole("PROFESSOR");
            assertEquals("PROFESSOR", dto.getRole());
        }
    }

    @Nested
    @DisplayName("Role Values Tests")
    class RoleValuesTests {

        @Test
        @DisplayName("Should handle STUDENT role")
        void roleStudent() {
            RegisterRequest dto = new RegisterRequest();
            dto.setRole("STUDENT");
            assertEquals("STUDENT", dto.getRole());
        }

        @Test
        @DisplayName("Should handle PROFESSOR role")
        void roleProfessor() {
            RegisterRequest dto = new RegisterRequest();
            dto.setRole("PROFESSOR");
            assertEquals("PROFESSOR", dto.getRole());
        }

        @Test
        @DisplayName("Should handle ADMIN role")
        void roleAdmin() {
            RegisterRequest dto = new RegisterRequest();
            dto.setRole("ADMIN");
            assertEquals("ADMIN", dto.getRole());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty strings")
        void emptyStrings() {
            RegisterRequest dto = new RegisterRequest("", "", "", "", "", "");
            assertEquals("", dto.getUsername());
            assertEquals("", dto.getEmail());
            assertEquals("", dto.getPassword());
            assertEquals("", dto.getFirstName());
            assertEquals("", dto.getLastName());
            assertEquals("", dto.getRole());
        }

        @Test
        @DisplayName("Should handle null values")
        void nullValues() {
            RegisterRequest dto = new RegisterRequest(null, null, null, null, null, null);
            assertNull(dto.getUsername());
            assertNull(dto.getEmail());
            assertNull(dto.getPassword());
            assertNull(dto.getFirstName());
            assertNull(dto.getLastName());
            assertNull(dto.getRole());
        }

        @Test
        @DisplayName("Should handle special characters in email")
        void specialCharactersInEmail() {
            RegisterRequest dto = new RegisterRequest();
            dto.setEmail("user+test@sub.domain.com");
            assertEquals("user+test@sub.domain.com", dto.getEmail());
        }

        @Test
        @DisplayName("Should handle unicode characters in names")
        void unicodeCharactersInNames() {
            RegisterRequest dto = new RegisterRequest();
            dto.setFirstName("José");
            dto.setLastName("García");
            assertEquals("José", dto.getFirstName());
            assertEquals("García", dto.getLastName());
        }

        @Test
        @DisplayName("Should handle special characters in password")
        void specialCharactersInPassword() {
            RegisterRequest dto = new RegisterRequest();
            dto.setPassword("P@$$w0rd!#%^&*()");
            assertEquals("P@$$w0rd!#%^&*()", dto.getPassword());
        }

        @Test
        @DisplayName("Should handle long username")
        void longUsername() {
            RegisterRequest dto = new RegisterRequest();
            String longUsername = "a".repeat(100);
            dto.setUsername(longUsername);
            assertEquals(longUsername, dto.getUsername());
        }
    }

    @Nested
    @DisplayName("Modification Tests")
    class ModificationTests {

        @Test
        @DisplayName("Should allow modifying all fields after construction")
        void modifyAllFields() {
            RegisterRequest dto = new RegisterRequest(
                    "original", "original@test.com", "originalPass",
                    "OriginalFirst", "OriginalLast", "STUDENT");

            dto.setUsername("modified");
            dto.setEmail("modified@test.com");
            dto.setPassword("modifiedPass");
            dto.setFirstName("ModifiedFirst");
            dto.setLastName("ModifiedLast");
            dto.setRole("PROFESSOR");

            assertEquals("modified", dto.getUsername());
            assertEquals("modified@test.com", dto.getEmail());
            assertEquals("modifiedPass", dto.getPassword());
            assertEquals("ModifiedFirst", dto.getFirstName());
            assertEquals("ModifiedLast", dto.getLastName());
            assertEquals("PROFESSOR", dto.getRole());
        }
    }
}
