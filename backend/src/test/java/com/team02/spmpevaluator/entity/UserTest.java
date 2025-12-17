package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User entity.
 */
@DisplayName("User Entity Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            User entity = new User();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getUsername());
            assertNull(entity.getEmail());
            assertTrue(entity.isEnabled()); // Default value
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime now = LocalDateTime.now();
            User entity = new User(
                    1L, "johndoe", "john@example.com",
                    "$2a$10$hashedpassword123456", Role.STUDENT,
                    "John", "Doe", true, now, now);

            assertEquals(1L, entity.getId());
            assertEquals("johndoe", entity.getUsername());
            assertEquals("john@example.com", entity.getEmail());
            assertEquals("$2a$10$hashedpassword123456", entity.getPassword());
            assertEquals(Role.STUDENT, entity.getRole());
            assertEquals("John", entity.getFirstName());
            assertEquals("Doe", entity.getLastName());
            assertTrue(entity.isEnabled());
            assertEquals(now, entity.getCreatedAt());
            assertEquals(now, entity.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testId() {
            user.setId(100L);
            assertEquals(100L, user.getId());
        }

        @Test
        @DisplayName("Should set and get username")
        void testUsername() {
            user.setUsername("testuser");
            assertEquals("testuser", user.getUsername());
        }

        @Test
        @DisplayName("Should set and get email")
        void testEmail() {
            user.setEmail("test@example.com");
            assertEquals("test@example.com", user.getEmail());
        }

        @Test
        @DisplayName("Should set and get password")
        void testPassword() {
            user.setPassword("$2a$10$encodedpassword");
            assertEquals("$2a$10$encodedpassword", user.getPassword());
        }

        @Test
        @DisplayName("Should set and get role")
        void testRole() {
            user.setRole(Role.PROFESSOR);
            assertEquals(Role.PROFESSOR, user.getRole());
        }

        @Test
        @DisplayName("Should set and get firstName")
        void testFirstName() {
            user.setFirstName("Alice");
            assertEquals("Alice", user.getFirstName());
        }

        @Test
        @DisplayName("Should set and get lastName")
        void testLastName() {
            user.setLastName("Smith");
            assertEquals("Smith", user.getLastName());
        }

        @Test
        @DisplayName("Should set and get enabled")
        void testEnabled() {
            user.setEnabled(false);
            assertFalse(user.isEnabled());
            user.setEnabled(true);
            assertTrue(user.isEnabled());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void testCreatedAt() {
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            assertEquals(now, user.getCreatedAt());
        }

        @Test
        @DisplayName("Should set and get updatedAt")
        void testUpdatedAt() {
            LocalDateTime now = LocalDateTime.now();
            user.setUpdatedAt(now);
            assertEquals(now, user.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have enabled default to true")
        void enabledDefaultTrue() {
            User entity = new User();
            assertTrue(entity.isEnabled());
        }
    }

    @Nested
    @DisplayName("Role Assignment Tests")
    class RoleAssignmentTests {

        @Test
        @DisplayName("Should set STUDENT role")
        void studentRole() {
            user.setRole(Role.STUDENT);
            assertEquals(Role.STUDENT, user.getRole());
        }

        @Test
        @DisplayName("Should set PROFESSOR role")
        void professorRole() {
            user.setRole(Role.PROFESSOR);
            assertEquals(Role.PROFESSOR, user.getRole());
        }

        @Test
        @DisplayName("Should set ADMIN role")
        void adminRole() {
            user.setRole(Role.ADMIN);
            assertEquals(Role.ADMIN, user.getRole());
        }

        @Test
        @DisplayName("Should allow role change")
        void roleChange() {
            user.setRole(Role.STUDENT);
            assertEquals(Role.STUDENT, user.getRole());

            user.setRole(Role.PROFESSOR);
            assertEquals(Role.PROFESSOR, user.getRole());
        }
    }

    @Nested
    @DisplayName("User Account Tests")
    class UserAccountTests {

        @Test
        @DisplayName("Should represent active user")
        void activeUser() {
            user.setEnabled(true);
            assertTrue(user.isEnabled());
        }

        @Test
        @DisplayName("Should represent locked user (UC 2.13)")
        void lockedUser() {
            user.setEnabled(false);
            assertFalse(user.isEnabled());
        }
    }

    @Nested
    @DisplayName("User Use Case Tests")
    class UseCaseTests {

        @Test
        @DisplayName("Student user for document upload (UC 2.1)")
        void studentForDocumentUpload() {
            user.setUsername("student1");
            user.setEmail("student1@university.edu");
            user.setRole(Role.STUDENT);
            user.setEnabled(true);

            assertEquals(Role.STUDENT, user.getRole());
            assertTrue(user.isEnabled());
        }

        @Test
        @DisplayName("Professor user for score override (UC 2.6)")
        void professorForScoreOverride() {
            user.setUsername("prof_smith");
            user.setEmail("smith@university.edu");
            user.setRole(Role.PROFESSOR);
            user.setFirstName("Dr. John");
            user.setLastName("Smith");

            assertEquals(Role.PROFESSOR, user.getRole());
        }

        @Test
        @DisplayName("Admin user for system management (UC 2.10)")
        void adminForSystemManagement() {
            user.setUsername("admin");
            user.setEmail("admin@university.edu");
            user.setRole(Role.ADMIN);

            assertEquals(Role.ADMIN, user.getRole());
        }
    }

    @Nested
    @DisplayName("Email Format Tests")
    class EmailFormatTests {

        @Test
        @DisplayName("Should accept standard email format")
        void standardEmail() {
            user.setEmail("user@example.com");
            assertEquals("user@example.com", user.getEmail());
        }

        @Test
        @DisplayName("Should accept university email format")
        void universityEmail() {
            user.setEmail("student@university.edu");
            assertEquals("student@university.edu", user.getEmail());
        }

        @Test
        @DisplayName("Should accept email with subdomain")
        void emailWithSubdomain() {
            user.setEmail("user@mail.university.edu");
            assertEquals("user@mail.university.edu", user.getEmail());
        }
    }

    @Nested
    @DisplayName("Password Tests")
    class PasswordTests {

        @Test
        @DisplayName("Should accept BCrypt encoded password")
        void bcryptPassword() {
            String bcryptPassword = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
            user.setPassword(bcryptPassword);
            assertTrue(user.getPassword().startsWith("$2a$"));
        }

        @Test
        @DisplayName("Should store password as-is")
        void passwordStoredAsIs() {
            String password = "plaintext";
            user.setPassword(password);
            assertEquals(password, user.getPassword());
        }
    }

    @Nested
    @DisplayName("Name Tests")
    class NameTests {

        @Test
        @DisplayName("Should handle names with spaces")
        void namesWithSpaces() {
            user.setFirstName("Mary Jane");
            user.setLastName("Watson Parker");
            assertEquals("Mary Jane", user.getFirstName());
            assertEquals("Watson Parker", user.getLastName());
        }

        @Test
        @DisplayName("Should handle null names")
        void nullNames() {
            user.setFirstName(null);
            user.setLastName(null);
            assertNull(user.getFirstName());
            assertNull(user.getLastName());
        }

        @Test
        @DisplayName("Should handle empty names")
        void emptyNames() {
            user.setFirstName("");
            user.setLastName("");
            assertEquals("", user.getFirstName());
            assertEquals("", user.getLastName());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be reflexive")
        void equalsReflexive() {
            user.setId(1L);
            user.setUsername("test");
            assertEquals(user, user);
        }

        @Test
        @DisplayName("Should handle null comparison")
        void equalsNull() {
            user.setId(1L);
            assertNotEquals(null, user);
        }

        @Test
        @DisplayName("Should handle different class comparison")
        void equalsDifferentClass() {
            user.setId(1L);
            assertNotEquals("not a user", user);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should not be null")
        void toStringNotNull() {
            user.setId(1L);
            user.setUsername("testuser");
            user.setRole(Role.STUDENT);

            String str = user.toString();
            assertNotNull(str);
        }
    }
}
