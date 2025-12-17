package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AssignmentDTO.
 */
@DisplayName("AssignmentDTO Tests")
class AssignmentDTOTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            AssignmentDTO dto = new AssignmentDTO();

            assertNotNull(dto);
            assertNull(dto.getId());
            assertNull(dto.getStudentId());
            assertNull(dto.getStudentName());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            AssignmentDTO dto = new AssignmentDTO(
                    1L, 2L, "John Doe", "john@example.com",
                    3L, "Prof Smith", "smith@example.com",
                    "Admin User", "2025-12-18", "Test notes");

            assertEquals(1L, dto.getId());
            assertEquals(2L, dto.getStudentId());
            assertEquals("John Doe", dto.getStudentName());
            assertEquals("john@example.com", dto.getStudentEmail());
            assertEquals(3L, dto.getProfessorId());
            assertEquals("Prof Smith", dto.getProfessorName());
            assertEquals("smith@example.com", dto.getProfessorEmail());
            assertEquals("Admin User", dto.getAssignedByName());
            assertEquals("2025-12-18", dto.getAssignedAt());
            assertEquals("Test notes", dto.getNotes());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setId(100L);
            assertEquals(100L, dto.getId());
        }

        @Test
        @DisplayName("Should set and get studentId")
        void setAndGetStudentId() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setStudentId(200L);
            assertEquals(200L, dto.getStudentId());
        }

        @Test
        @DisplayName("Should set and get studentName")
        void setAndGetStudentName() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setStudentName("Test Student");
            assertEquals("Test Student", dto.getStudentName());
        }

        @Test
        @DisplayName("Should set and get studentEmail")
        void setAndGetStudentEmail() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setStudentEmail("student@test.com");
            assertEquals("student@test.com", dto.getStudentEmail());
        }

        @Test
        @DisplayName("Should set and get professorId")
        void setAndGetProfessorId() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setProfessorId(300L);
            assertEquals(300L, dto.getProfessorId());
        }

        @Test
        @DisplayName("Should set and get professorName")
        void setAndGetProfessorName() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setProfessorName("Professor Test");
            assertEquals("Professor Test", dto.getProfessorName());
        }

        @Test
        @DisplayName("Should set and get professorEmail")
        void setAndGetProfessorEmail() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setProfessorEmail("prof@test.com");
            assertEquals("prof@test.com", dto.getProfessorEmail());
        }

        @Test
        @DisplayName("Should set and get assignedByName")
        void setAndGetAssignedByName() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setAssignedByName("Assigner");
            assertEquals("Assigner", dto.getAssignedByName());
        }

        @Test
        @DisplayName("Should set and get assignedAt")
        void setAndGetAssignedAt() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setAssignedAt("2025-01-01");
            assertEquals("2025-01-01", dto.getAssignedAt());
        }

        @Test
        @DisplayName("Should set and get notes")
        void setAndGetNotes() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setNotes("Important notes");
            assertEquals("Important notes", dto.getNotes());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            AssignmentDTO dto1 = new AssignmentDTO(1L, 2L, "Name", "email@test.com",
                    3L, "Prof", "prof@test.com", "Admin", "2025-12-18", "notes");
            AssignmentDTO dto2 = new AssignmentDTO(1L, 2L, "Name", "email@test.com",
                    3L, "Prof", "prof@test.com", "Admin", "2025-12-18", "notes");

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different values")
        void equals_DifferentValues_ReturnsFalse() {
            AssignmentDTO dto1 = new AssignmentDTO();
            dto1.setId(1L);
            AssignmentDTO dto2 = new AssignmentDTO();
            dto2.setId(2L);

            assertNotEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with all fields")
        void toString_ContainsAllFields() {
            AssignmentDTO dto = new AssignmentDTO();
            dto.setId(1L);
            dto.setStudentName("John");

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("studentName=John"));
        }
    }
}
