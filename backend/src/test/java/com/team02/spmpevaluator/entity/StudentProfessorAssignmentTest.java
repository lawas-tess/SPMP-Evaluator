package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentProfessorAssignment entity.
 * UC 2.12: Admin Assign Students to Professors
 */
@DisplayName("StudentProfessorAssignment Entity Tests")
class StudentProfessorAssignmentTest {

    private StudentProfessorAssignment assignment;
    private User student;
    private User professor;
    private User admin;

    @BeforeEach
    void setUp() {
        assignment = new StudentProfessorAssignment();

        student = new User();
        student.setId(1L);
        student.setUsername("student1");
        student.setRole(Role.STUDENT);

        professor = new User();
        professor.setId(2L);
        professor.setUsername("professor1");
        professor.setRole(Role.PROFESSOR);

        admin = new User();
        admin.setId(3L);
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            StudentProfessorAssignment entity = new StudentProfessorAssignment();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getStudent());
            assertNull(entity.getProfessor());
        }

        @Test
        @DisplayName("Should create entity with all-args constructor")
        void allArgsConstructor_CreatesPopulatedEntity() {
            LocalDateTime now = LocalDateTime.now();
            StudentProfessorAssignment entity = new StudentProfessorAssignment(
                    1L, student, professor, admin, now, "Capstone project mentorship");

            assertEquals(1L, entity.getId());
            assertEquals(student, entity.getStudent());
            assertEquals(professor, entity.getProfessor());
            assertEquals(admin, entity.getAssignedBy());
            assertEquals(now, entity.getAssignedAt());
            assertEquals("Capstone project mentorship", entity.getNotes());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testId() {
            assignment.setId(100L);
            assertEquals(100L, assignment.getId());
        }

        @Test
        @DisplayName("Should set and get student")
        void testStudent() {
            assignment.setStudent(student);
            assertEquals(student, assignment.getStudent());
            assertEquals(Role.STUDENT, assignment.getStudent().getRole());
        }

        @Test
        @DisplayName("Should set and get professor")
        void testProfessor() {
            assignment.setProfessor(professor);
            assertEquals(professor, assignment.getProfessor());
            assertEquals(Role.PROFESSOR, assignment.getProfessor().getRole());
        }

        @Test
        @DisplayName("Should set and get assignedBy")
        void testAssignedBy() {
            assignment.setAssignedBy(admin);
            assertEquals(admin, assignment.getAssignedBy());
            assertEquals(Role.ADMIN, assignment.getAssignedBy().getRole());
        }

        @Test
        @DisplayName("Should set and get assignedAt")
        void testAssignedAt() {
            LocalDateTime now = LocalDateTime.now();
            assignment.setAssignedAt(now);
            assertEquals(now, assignment.getAssignedAt());
        }

        @Test
        @DisplayName("Should set and get notes")
        void testNotes() {
            assignment.setNotes("Final year project supervision");
            assertEquals("Final year project supervision", assignment.getNotes());
        }
    }

    @Nested
    @DisplayName("Assignment Workflow Tests")
    class AssignmentWorkflowTests {

        @Test
        @DisplayName("Should create complete assignment (UC 2.12)")
        void completeAssignment() {
            assignment.setStudent(student);
            assignment.setProfessor(professor);
            assignment.setAssignedBy(admin);
            assignment.setAssignedAt(LocalDateTime.now());
            assignment.setNotes("SPMP Evaluator project mentorship");

            assertNotNull(assignment.getStudent());
            assertNotNull(assignment.getProfessor());
            assertNotNull(assignment.getAssignedBy());
            assertNotNull(assignment.getAssignedAt());

            assertEquals(Role.STUDENT, assignment.getStudent().getRole());
            assertEquals(Role.PROFESSOR, assignment.getProfessor().getRole());
            assertEquals(Role.ADMIN, assignment.getAssignedBy().getRole());
        }

        @Test
        @DisplayName("Should allow reassignment to different professor")
        void reassignToDifferentProfessor() {
            assignment.setStudent(student);
            assignment.setProfessor(professor);

            User newProfessor = new User();
            newProfessor.setId(4L);
            newProfessor.setUsername("professor2");
            newProfessor.setRole(Role.PROFESSOR);

            assignment.setProfessor(newProfessor);

            assertEquals(newProfessor, assignment.getProfessor());
            assertEquals("professor2", assignment.getProfessor().getUsername());
        }
    }

    @Nested
    @DisplayName("Role Validation Tests")
    class RoleValidationTests {

        @Test
        @DisplayName("Student should have STUDENT role")
        void studentHasCorrectRole() {
            assignment.setStudent(student);
            assertEquals(Role.STUDENT, assignment.getStudent().getRole());
        }

        @Test
        @DisplayName("Professor should have PROFESSOR role")
        void professorHasCorrectRole() {
            assignment.setProfessor(professor);
            assertEquals(Role.PROFESSOR, assignment.getProfessor().getRole());
        }

        @Test
        @DisplayName("AssignedBy should typically have ADMIN role")
        void assignedByHasAdminRole() {
            assignment.setAssignedBy(admin);
            assertEquals(Role.ADMIN, assignment.getAssignedBy().getRole());
        }
    }

    @Nested
    @DisplayName("Notes Tests")
    class NotesTests {

        @Test
        @DisplayName("Should handle empty notes")
        void emptyNotes() {
            assignment.setNotes("");
            assertEquals("", assignment.getNotes());
        }

        @Test
        @DisplayName("Should handle null notes")
        void nullNotes() {
            assignment.setNotes(null);
            assertNull(assignment.getNotes());
        }

        @Test
        @DisplayName("Should handle long notes")
        void longNotes() {
            String longNote = "This is a detailed note about the student-professor assignment. " +
                    "The student will be working on the SPMP Evaluator capstone project. " +
                    "Expected completion by end of semester.";
            assignment.setNotes(longNote);
            assertEquals(longNote, assignment.getNotes());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same id")
        void equalsWithSameId() {
            StudentProfessorAssignment a1 = new StudentProfessorAssignment();
            a1.setId(1L);

            StudentProfessorAssignment a2 = new StudentProfessorAssignment();
            a2.setId(1L);

            assertEquals(a1, a2);
            assertEquals(a1.hashCode(), a2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different ids")
        void notEqualsWithDifferentIds() {
            StudentProfessorAssignment a1 = new StudentProfessorAssignment();
            a1.setId(1L);

            StudentProfessorAssignment a2 = new StudentProfessorAssignment();
            a2.setId(2L);

            assertNotEquals(a1, a2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void toStringContainsFields() {
            assignment.setId(1L);
            assignment.setStudent(student);
            assignment.setProfessor(professor);

            String str = assignment.toString();
            assertNotNull(str);
            assertTrue(str.contains("1"));
        }
    }
}
