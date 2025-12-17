package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.StudentProfessorAssignment;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.StudentProfessorAssignmentRepository;
import com.team02.spmpevaluator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AssignmentService.
 * UC 2.12: Admin Assign Students to Professors
 */
@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private StudentProfessorAssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssignmentService assignmentService;

    private User testStudent;
    private User testStudent2;
    private User testProfessor;
    private User testProfessor2;
    private User testAdmin;
    private StudentProfessorAssignment testAssignment;

    @BeforeEach
    void setUp() {
        // Setup test student
        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setUsername("student1");
        testStudent.setEmail("student1@example.com");
        testStudent.setFirstName("John");
        testStudent.setLastName("Student");
        testStudent.setRole(Role.STUDENT);
        testStudent.setEnabled(true);

        // Setup second test student
        testStudent2 = new User();
        testStudent2.setId(5L);
        testStudent2.setUsername("student2");
        testStudent2.setEmail("student2@example.com");
        testStudent2.setFirstName("Jane");
        testStudent2.setLastName("Student");
        testStudent2.setRole(Role.STUDENT);
        testStudent2.setEnabled(true);

        // Setup test professor
        testProfessor = new User();
        testProfessor.setId(2L);
        testProfessor.setUsername("professor1");
        testProfessor.setEmail("professor1@example.com");
        testProfessor.setFirstName("Dr");
        testProfessor.setLastName("Professor");
        testProfessor.setRole(Role.PROFESSOR);
        testProfessor.setEnabled(true);

        // Setup second test professor
        testProfessor2 = new User();
        testProfessor2.setId(6L);
        testProfessor2.setUsername("professor2");
        testProfessor2.setEmail("professor2@example.com");
        testProfessor2.setFirstName("Dr2");
        testProfessor2.setLastName("Professor2");
        testProfessor2.setRole(Role.PROFESSOR);
        testProfessor2.setEnabled(true);

        // Setup test admin
        testAdmin = new User();
        testAdmin.setId(3L);
        testAdmin.setUsername("admin1");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setFirstName("Admin");
        testAdmin.setLastName("User");
        testAdmin.setRole(Role.ADMIN);
        testAdmin.setEnabled(true);

        // Setup test assignment
        testAssignment = new StudentProfessorAssignment();
        testAssignment.setId(1L);
        testAssignment.setStudent(testStudent);
        testAssignment.setProfessor(testProfessor);
        testAssignment.setAssignedBy(testAdmin);
        testAssignment.setNotes("Initial assignment");
        testAssignment.setAssignedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Assign Student to Professor Tests")
    class AssignStudentToProfessorTests {

        @Test
        @DisplayName("Should successfully assign student to professor")
        void assignStudentToProfessor_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(userRepository.findById(3L)).thenReturn(Optional.of(testAdmin));
            when(assignmentRepository.existsByStudent_IdAndProfessor_Id(1L, 2L)).thenReturn(false);
            when(assignmentRepository.save(any(StudentProfessorAssignment.class))).thenAnswer(invocation -> {
                StudentProfessorAssignment assignment = invocation.getArgument(0);
                assignment.setId(1L);
                return assignment;
            });

            // Act
            StudentProfessorAssignment result = assignmentService.assignStudentToProfessor(
                    1L, 2L, 3L, "Test notes");

            // Assert
            assertNotNull(result);
            assertEquals(testStudent, result.getStudent());
            assertEquals(testProfessor, result.getProfessor());
            assertEquals(testAdmin, result.getAssignedBy());
            assertEquals("Test notes", result.getNotes());

            ArgumentCaptor<StudentProfessorAssignment> captor = ArgumentCaptor
                    .forClass(StudentProfessorAssignment.class);
            verify(assignmentRepository).save(captor.capture());
            assertEquals(testStudent, captor.getValue().getStudent());
        }

        @Test
        @DisplayName("Should assign student without notes")
        void assignStudentToProfessor_NoNotes() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(userRepository.findById(3L)).thenReturn(Optional.of(testAdmin));
            when(assignmentRepository.existsByStudent_IdAndProfessor_Id(1L, 2L)).thenReturn(false);
            when(assignmentRepository.save(any(StudentProfessorAssignment.class))).thenAnswer(invocation -> {
                StudentProfessorAssignment assignment = invocation.getArgument(0);
                assignment.setId(1L);
                return assignment;
            });

            // Act
            StudentProfessorAssignment result = assignmentService.assignStudentToProfessor(
                    1L, 2L, 3L, null);

            // Assert
            assertNotNull(result);
            assertNull(result.getNotes());
            verify(assignmentRepository).save(any(StudentProfessorAssignment.class));
        }

        @Test
        @DisplayName("Should throw exception when student not found")
        void assignStudentToProfessor_StudentNotFound() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> assignmentService.assignStudentToProfessor(99L, 2L, 3L, "notes"));
            assertEquals("Student not found", exception.getMessage());
            verify(assignmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when professor not found")
        void assignStudentToProfessor_ProfessorNotFound() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> assignmentService.assignStudentToProfessor(1L, 99L, 3L, "notes"));
            assertEquals("Professor not found", exception.getMessage());
            verify(assignmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when admin not found")
        void assignStudentToProfessor_AdminNotFound() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> assignmentService.assignStudentToProfessor(1L, 2L, 99L, "notes"));
            assertEquals("Admin not found", exception.getMessage());
            verify(assignmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when user is not a student")
        void assignStudentToProfessor_NotAStudent() {
            // Arrange - Using professor as student
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(userRepository.findById(6L)).thenReturn(Optional.of(testProfessor2));
            when(userRepository.findById(3L)).thenReturn(Optional.of(testAdmin));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> assignmentService.assignStudentToProfessor(2L, 6L, 3L, "notes"));
            assertEquals("User is not a student", exception.getMessage());
            verify(assignmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when user is not a professor")
        void assignStudentToProfessor_NotAProfessor() {
            // Arrange - Using student as professor
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(5L)).thenReturn(Optional.of(testStudent2));
            when(userRepository.findById(3L)).thenReturn(Optional.of(testAdmin));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> assignmentService.assignStudentToProfessor(1L, 5L, 3L, "notes"));
            assertEquals("User is not a professor", exception.getMessage());
            verify(assignmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when assignedBy user is not an admin")
        void assignStudentToProfessor_NotAnAdmin() {
            // Arrange - Using professor as admin
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(userRepository.findById(6L)).thenReturn(Optional.of(testProfessor2));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> assignmentService.assignStudentToProfessor(1L, 2L, 6L, "notes"));
            assertEquals("User is not an admin", exception.getMessage());
            verify(assignmentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when assignment already exists")
        void assignStudentToProfessor_AlreadyAssigned() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(userRepository.findById(2L)).thenReturn(Optional.of(testProfessor));
            when(userRepository.findById(3L)).thenReturn(Optional.of(testAdmin));
            when(assignmentRepository.existsByStudent_IdAndProfessor_Id(1L, 2L)).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> assignmentService.assignStudentToProfessor(1L, 2L, 3L, "notes"));
            assertEquals("Student is already assigned to this professor", exception.getMessage());
            verify(assignmentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Assignments by Professor Tests")
    class GetAssignmentsByProfessorTests {

        @Test
        @DisplayName("Should return assignments for professor")
        void getAssignmentsByProfessor_Success() {
            // Arrange
            StudentProfessorAssignment assignment2 = new StudentProfessorAssignment();
            assignment2.setId(2L);
            assignment2.setStudent(testStudent2);
            assignment2.setProfessor(testProfessor);
            assignment2.setAssignedBy(testAdmin);

            List<StudentProfessorAssignment> assignments = Arrays.asList(testAssignment, assignment2);
            when(assignmentRepository.findByProfessor_Id(2L)).thenReturn(assignments);

            // Act
            List<StudentProfessorAssignment> result = assignmentService.getAssignmentsByProfessor(2L);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains(testAssignment));
            assertTrue(result.contains(assignment2));
            verify(assignmentRepository).findByProfessor_Id(2L);
        }

        @Test
        @DisplayName("Should return empty list when professor has no assignments")
        void getAssignmentsByProfessor_NoAssignments() {
            // Arrange
            when(assignmentRepository.findByProfessor_Id(2L)).thenReturn(Collections.emptyList());

            // Act
            List<StudentProfessorAssignment> result = assignmentService.getAssignmentsByProfessor(2L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(assignmentRepository).findByProfessor_Id(2L);
        }

        @Test
        @DisplayName("Should return empty list for non-existent professor")
        void getAssignmentsByProfessor_NonExistentProfessor() {
            // Arrange
            when(assignmentRepository.findByProfessor_Id(999L)).thenReturn(Collections.emptyList());

            // Act
            List<StudentProfessorAssignment> result = assignmentService.getAssignmentsByProfessor(999L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Students by Professor Tests")
    class GetStudentsByProfessorTests {

        @Test
        @DisplayName("Should return students assigned to professor")
        void getStudentsByProfessor_Success() {
            // Arrange
            List<User> students = Arrays.asList(testStudent, testStudent2);
            when(assignmentRepository.findStudentsByProfessorId(2L)).thenReturn(students);

            // Act
            List<User> result = assignmentService.getStudentsByProfessor(2L);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains(testStudent));
            assertTrue(result.contains(testStudent2));
            verify(assignmentRepository).findStudentsByProfessorId(2L);
        }

        @Test
        @DisplayName("Should return empty list when professor has no students")
        void getStudentsByProfessor_NoStudents() {
            // Arrange
            when(assignmentRepository.findStudentsByProfessorId(2L)).thenReturn(Collections.emptyList());

            // Act
            List<User> result = assignmentService.getStudentsByProfessor(2L);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(assignmentRepository).findStudentsByProfessorId(2L);
        }

        @Test
        @DisplayName("Should return single student")
        void getStudentsByProfessor_SingleStudent() {
            // Arrange
            when(assignmentRepository.findStudentsByProfessorId(2L)).thenReturn(List.of(testStudent));

            // Act
            List<User> result = assignmentService.getStudentsByProfessor(2L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testStudent, result.get(0));
        }
    }

    @Nested
    @DisplayName("Get Professor by Student Tests")
    class GetProfessorByStudentTests {

        @Test
        @DisplayName("Should return professor for assigned student")
        void getProfessorByStudent_Success() {
            // Arrange
            when(assignmentRepository.findProfessorByStudentId(1L)).thenReturn(Optional.of(testProfessor));

            // Act
            User result = assignmentService.getProfessorByStudent(1L);

            // Assert
            assertNotNull(result);
            assertEquals(testProfessor, result);
            assertEquals(Role.PROFESSOR, result.getRole());
            verify(assignmentRepository).findProfessorByStudentId(1L);
        }

        @Test
        @DisplayName("Should return null when student has no professor")
        void getProfessorByStudent_NotAssigned() {
            // Arrange
            when(assignmentRepository.findProfessorByStudentId(1L)).thenReturn(Optional.empty());

            // Act
            User result = assignmentService.getProfessorByStudent(1L);

            // Assert
            assertNull(result);
            verify(assignmentRepository).findProfessorByStudentId(1L);
        }

        @Test
        @DisplayName("Should return null for non-existent student")
        void getProfessorByStudent_NonExistentStudent() {
            // Arrange
            when(assignmentRepository.findProfessorByStudentId(999L)).thenReturn(Optional.empty());

            // Act
            User result = assignmentService.getProfessorByStudent(999L);

            // Assert
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Remove Assignment Tests")
    class RemoveAssignmentTests {

        @Test
        @DisplayName("Should remove assignment successfully")
        void removeAssignment_Success() {
            // Arrange
            doNothing().when(assignmentRepository).deleteById(1L);

            // Act
            assignmentService.removeAssignment(1L);

            // Assert
            verify(assignmentRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should call deleteById even for non-existent assignment")
        void removeAssignment_NonExistent() {
            // Arrange - deleteById doesn't throw for non-existent
            doNothing().when(assignmentRepository).deleteById(999L);

            // Act
            assignmentService.removeAssignment(999L);

            // Assert
            verify(assignmentRepository).deleteById(999L);
        }
    }

    @Nested
    @DisplayName("Get All Assignments Tests")
    class GetAllAssignmentsTests {

        @Test
        @DisplayName("Should return all assignments")
        void getAllAssignments_Success() {
            // Arrange
            StudentProfessorAssignment assignment2 = new StudentProfessorAssignment();
            assignment2.setId(2L);
            assignment2.setStudent(testStudent2);
            assignment2.setProfessor(testProfessor);
            assignment2.setAssignedBy(testAdmin);

            StudentProfessorAssignment assignment3 = new StudentProfessorAssignment();
            assignment3.setId(3L);
            assignment3.setStudent(testStudent);
            assignment3.setProfessor(testProfessor2);
            assignment3.setAssignedBy(testAdmin);

            List<StudentProfessorAssignment> assignments = Arrays.asList(
                    testAssignment, assignment2, assignment3);
            when(assignmentRepository.findAll()).thenReturn(assignments);

            // Act
            List<StudentProfessorAssignment> result = assignmentService.getAllAssignments();

            // Assert
            assertNotNull(result);
            assertEquals(3, result.size());
            verify(assignmentRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no assignments exist")
        void getAllAssignments_Empty() {
            // Arrange
            when(assignmentRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<StudentProfessorAssignment> result = assignmentService.getAllAssignments();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(assignmentRepository).findAll();
        }

        @Test
        @DisplayName("Should return single assignment")
        void getAllAssignments_SingleAssignment() {
            // Arrange
            when(assignmentRepository.findAll()).thenReturn(List.of(testAssignment));

            // Act
            List<StudentProfessorAssignment> result = assignmentService.getAllAssignments();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testAssignment, result.get(0));
        }
    }
}
