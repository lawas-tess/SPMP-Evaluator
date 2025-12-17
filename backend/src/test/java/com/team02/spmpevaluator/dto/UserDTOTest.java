package com.team02.spmpevaluator.dto;

import com.team02.spmpevaluator.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserDTO.
 */
@DisplayName("UserDTO Tests")
class UserDTOTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            UserDTO dto = new UserDTO();

            assertNotNull(dto);
            assertNull(dto.getId());
            assertNull(dto.getUsername());
            assertNull(dto.getEmail());
            assertNull(dto.getFirstName());
            assertNull(dto.getLastName());
            assertNull(dto.getRole());
            assertFalse(dto.isEnabled());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            UserDTO dto = new UserDTO(
                    1L, "johndoe", "john@example.com",
                    "John", "Doe", Role.STUDENT, true);

            assertEquals(1L, dto.getId());
            assertEquals("johndoe", dto.getUsername());
            assertEquals("john@example.com", dto.getEmail());
            assertEquals("John", dto.getFirstName());
            assertEquals("Doe", dto.getLastName());
            assertEquals(Role.STUDENT, dto.getRole());
            assertTrue(dto.isEnabled());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            UserDTO dto = new UserDTO();
            dto.setId(100L);
            assertEquals(100L, dto.getId());
        }

        @Test
        @DisplayName("Should set and get username")
        void setAndGetUsername() {
            UserDTO dto = new UserDTO();
            dto.setUsername("testuser");
            assertEquals("testuser", dto.getUsername());
        }

        @Test
        @DisplayName("Should set and get email")
        void setAndGetEmail() {
            UserDTO dto = new UserDTO();
            dto.setEmail("test@example.com");
            assertEquals("test@example.com", dto.getEmail());
        }

        @Test
        @DisplayName("Should set and get firstName")
        void setAndGetFirstName() {
            UserDTO dto = new UserDTO();
            dto.setFirstName("Jane");
            assertEquals("Jane", dto.getFirstName());
        }

        @Test
        @DisplayName("Should set and get lastName")
        void setAndGetLastName() {
            UserDTO dto = new UserDTO();
            dto.setLastName("Smith");
            assertEquals("Smith", dto.getLastName());
        }

        @Test
        @DisplayName("Should set and get role")
        void setAndGetRole() {
            UserDTO dto = new UserDTO();
            dto.setRole(Role.PROFESSOR);
            assertEquals(Role.PROFESSOR, dto.getRole());
        }

        @Test
        @DisplayName("Should set and get enabled")
        void setAndGetEnabled() {
            UserDTO dto = new UserDTO();
            dto.setEnabled(true);
            assertTrue(dto.isEnabled());

            dto.setEnabled(false);
            assertFalse(dto.isEnabled());
        }
    }

    @Nested
    @DisplayName("Role Values Tests")
    class RoleValuesTests {

        @Test
        @DisplayName("Should handle STUDENT role")
        void roleStudent() {
            UserDTO dto = new UserDTO();
            dto.setRole(Role.STUDENT);
            assertEquals(Role.STUDENT, dto.getRole());
        }

        @Test
        @DisplayName("Should handle PROFESSOR role")
        void roleProfessor() {
            UserDTO dto = new UserDTO();
            dto.setRole(Role.PROFESSOR);
            assertEquals(Role.PROFESSOR, dto.getRole());
        }

        @Test
        @DisplayName("Should handle ADMIN role")
        void roleAdmin() {
            UserDTO dto = new UserDTO();
            dto.setRole(Role.ADMIN);
            assertEquals(Role.ADMIN, dto.getRole());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null values")
        void nullValues() {
            UserDTO dto = new UserDTO(
                    null, null, null, null, null, null, false);
            assertNull(dto.getId());
            assertNull(dto.getUsername());
            assertNull(dto.getEmail());
            assertNull(dto.getRole());
        }

        @Test
        @DisplayName("Should handle empty strings")
        void emptyStrings() {
            UserDTO dto = new UserDTO(
                    1L, "", "", "", "", Role.STUDENT, true);
            assertEquals("", dto.getUsername());
            assertEquals("", dto.getEmail());
            assertEquals("", dto.getFirstName());
            assertEquals("", dto.getLastName());
        }

        @Test
        @DisplayName("Should handle special characters in email")
        void specialCharactersInEmail() {
            UserDTO dto = new UserDTO();
            dto.setEmail("user+test@sub.domain.com");
            assertEquals("user+test@sub.domain.com", dto.getEmail());
        }

        @Test
        @DisplayName("Should handle unicode characters in names")
        void unicodeCharactersInNames() {
            UserDTO dto = new UserDTO();
            dto.setFirstName("José");
            dto.setLastName("García");
            assertEquals("José", dto.getFirstName());
            assertEquals("García", dto.getLastName());
        }

        @Test
        @DisplayName("Should handle disabled user")
        void disabledUser() {
            UserDTO dto = new UserDTO(
                    1L, "inactive", "inactive@test.com",
                    "Inactive", "User", Role.STUDENT, false);
            assertFalse(dto.isEnabled());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            UserDTO dto1 = new UserDTO(
                    1L, "user", "user@test.com", "First", "Last", Role.STUDENT, true);
            UserDTO dto2 = new UserDTO(
                    1L, "user", "user@test.com", "First", "Last", Role.STUDENT, true);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different values")
        void equals_DifferentValues_ReturnsFalse() {
            UserDTO dto1 = new UserDTO();
            dto1.setId(1L);
            UserDTO dto2 = new UserDTO();
            dto2.setId(2L);

            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("Should not be equal for different roles")
        void equals_DifferentRoles_ReturnsFalse() {
            UserDTO dto1 = new UserDTO(
                    1L, "user", "user@test.com", "First", "Last", Role.STUDENT, true);
            UserDTO dto2 = new UserDTO(
                    1L, "user", "user@test.com", "First", "Last", Role.PROFESSOR, true);

            assertNotEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with all fields")
        void toString_ContainsAllFields() {
            UserDTO dto = new UserDTO();
            dto.setId(1L);
            dto.setUsername("testuser");
            dto.setEmail("test@example.com");
            dto.setRole(Role.STUDENT);
            dto.setEnabled(true);

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("username=testuser"));
            assertTrue(result.contains("email=test@example.com"));
            assertTrue(result.contains("role=STUDENT"));
            assertTrue(result.contains("enabled=true"));
        }
    }

    @Nested
    @DisplayName("Modification Tests")
    class ModificationTests {

        @Test
        @DisplayName("Should allow role change")
        void roleChange() {
            UserDTO dto = new UserDTO();
            dto.setRole(Role.STUDENT);
            assertEquals(Role.STUDENT, dto.getRole());

            dto.setRole(Role.PROFESSOR);
            assertEquals(Role.PROFESSOR, dto.getRole());
        }

        @Test
        @DisplayName("Should allow enabling and disabling")
        void enableDisable() {
            UserDTO dto = new UserDTO();
            dto.setEnabled(true);
            assertTrue(dto.isEnabled());

            dto.setEnabled(false);
            assertFalse(dto.isEnabled());
        }
    }
}
