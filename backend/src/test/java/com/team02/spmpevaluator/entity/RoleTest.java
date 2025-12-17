package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Role enum.
 */
@DisplayName("Role Enum Tests")
class RoleTest {

    @Nested
    @DisplayName("Role Values Tests")
    class RoleValuesTests {

        @Test
        @DisplayName("Should have STUDENT role")
        void studentRole() {
            assertEquals("STUDENT", Role.STUDENT.name());
        }

        @Test
        @DisplayName("Should have PROFESSOR role")
        void professorRole() {
            assertEquals("PROFESSOR", Role.PROFESSOR.name());
        }

        @Test
        @DisplayName("Should have ADMIN role")
        void adminRole() {
            assertEquals("ADMIN", Role.ADMIN.name());
        }

        @Test
        @DisplayName("Should have exactly 3 roles")
        void roleCount() {
            assertEquals(3, Role.values().length);
        }
    }

    @Nested
    @DisplayName("Role valueOf Tests")
    class RoleValueOfTests {

        @Test
        @DisplayName("Should parse STUDENT from string")
        void parseStudent() {
            Role role = Role.valueOf("STUDENT");
            assertEquals(Role.STUDENT, role);
        }

        @Test
        @DisplayName("Should parse PROFESSOR from string")
        void parseProfessor() {
            Role role = Role.valueOf("PROFESSOR");
            assertEquals(Role.PROFESSOR, role);
        }

        @Test
        @DisplayName("Should parse ADMIN from string")
        void parseAdmin() {
            Role role = Role.valueOf("ADMIN");
            assertEquals(Role.ADMIN, role);
        }

        @Test
        @DisplayName("Should throw exception for invalid role")
        void invalidRole() {
            assertThrows(IllegalArgumentException.class, () -> {
                Role.valueOf("INVALID");
            });
        }

        @Test
        @DisplayName("Should throw exception for lowercase role")
        void lowercaseRole() {
            assertThrows(IllegalArgumentException.class, () -> {
                Role.valueOf("student");
            });
        }
    }

    @Nested
    @DisplayName("Role Ordinal Tests")
    class RoleOrdinalTests {

        @Test
        @DisplayName("STUDENT should have ordinal 0")
        void studentOrdinal() {
            assertEquals(0, Role.STUDENT.ordinal());
        }

        @Test
        @DisplayName("PROFESSOR should have ordinal 1")
        void professorOrdinal() {
            assertEquals(1, Role.PROFESSOR.ordinal());
        }

        @Test
        @DisplayName("ADMIN should have ordinal 2")
        void adminOrdinal() {
            assertEquals(2, Role.ADMIN.ordinal());
        }
    }

    @Nested
    @DisplayName("Role Comparison Tests")
    class RoleComparisonTests {

        @Test
        @DisplayName("Should compare roles by ordinal")
        void compareByOrdinal() {
            assertTrue(Role.STUDENT.ordinal() < Role.PROFESSOR.ordinal());
            assertTrue(Role.PROFESSOR.ordinal() < Role.ADMIN.ordinal());
        }

        @Test
        @DisplayName("Should compare roles using compareTo")
        void compareUsingCompareTo() {
            assertTrue(Role.STUDENT.compareTo(Role.PROFESSOR) < 0);
            assertTrue(Role.ADMIN.compareTo(Role.STUDENT) > 0);
            assertEquals(0, Role.PROFESSOR.compareTo(Role.PROFESSOR));
        }
    }

    @Nested
    @DisplayName("Role Hierarchy Tests")
    class RoleHierarchyTests {

        @Test
        @DisplayName("STUDENT is the base role")
        void studentIsBase() {
            assertEquals(Role.values()[0], Role.STUDENT);
        }

        @Test
        @DisplayName("ADMIN is the highest role")
        void adminIsHighest() {
            Role[] roles = Role.values();
            assertEquals(Role.ADMIN, roles[roles.length - 1]);
        }

        @Test
        @DisplayName("Roles should be in order: STUDENT, PROFESSOR, ADMIN")
        void roleOrder() {
            Role[] roles = Role.values();
            assertEquals(Role.STUDENT, roles[0]);
            assertEquals(Role.PROFESSOR, roles[1]);
            assertEquals(Role.ADMIN, roles[2]);
        }
    }

    @Nested
    @DisplayName("Role Use Case Tests")
    class RoleUseCaseTests {

        @Test
        @DisplayName("STUDENT role for document submission (UC 2.1)")
        void studentDocumentSubmission() {
            Role role = Role.STUDENT;
            assertEquals("STUDENT", role.name());
            // Students can upload SPMP documents
        }

        @Test
        @DisplayName("PROFESSOR role for score override (UC 2.6)")
        void professorScoreOverride() {
            Role role = Role.PROFESSOR;
            assertEquals("PROFESSOR", role.name());
            // Professors can override AI scores
        }

        @Test
        @DisplayName("ADMIN role for system management (UC 2.10)")
        void adminSystemManagement() {
            Role role = Role.ADMIN;
            assertEquals("ADMIN", role.name());
            // Admins can manage system settings
        }
    }

    @Nested
    @DisplayName("Role String Operations Tests")
    class RoleStringOperationsTests {

        @Test
        @DisplayName("Should convert role to string")
        void roleToString() {
            assertEquals("STUDENT", Role.STUDENT.toString());
            assertEquals("PROFESSOR", Role.PROFESSOR.toString());
            assertEquals("ADMIN", Role.ADMIN.toString());
        }

        @Test
        @DisplayName("name() and toString() should return same value")
        void nameEqualsToString() {
            for (Role role : Role.values()) {
                assertEquals(role.name(), role.toString());
            }
        }
    }

    @Nested
    @DisplayName("Role Iteration Tests")
    class RoleIterationTests {

        @Test
        @DisplayName("Should iterate through all roles")
        void iterateThroughRoles() {
            int count = 0;
            for (Role role : Role.values()) {
                assertNotNull(role);
                count++;
            }
            assertEquals(3, count);
        }

        @Test
        @DisplayName("Should find role in array")
        void findRoleInArray() {
            Role[] roles = Role.values();
            boolean foundProfessor = false;
            for (Role role : roles) {
                if (role == Role.PROFESSOR) {
                    foundProfessor = true;
                    break;
                }
            }
            assertTrue(foundProfessor);
        }
    }
}
