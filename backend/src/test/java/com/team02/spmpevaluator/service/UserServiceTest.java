package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.dto.UserDTO;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Tests user registration, authentication, profile management, and admin
 * functions.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SPMPDocumentRepository documentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private StudentProfessorAssignmentRepository assignmentRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User testStudent;
    private User testProfessor;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(Role.STUDENT);
        testUser.setEnabled(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testStudent = new User();
        testStudent.setId(2L);
        testStudent.setUsername("student1");
        testStudent.setEmail("student@example.com");
        testStudent.setPassword("encodedPassword");
        testStudent.setFirstName("John");
        testStudent.setLastName("Student");
        testStudent.setRole(Role.STUDENT);
        testStudent.setEnabled(true);

        testProfessor = new User();
        testProfessor.setId(3L);
        testProfessor.setUsername("professor1");
        testProfessor.setEmail("professor@example.com");
        testProfessor.setPassword("encodedPassword");
        testProfessor.setFirstName("Jane");
        testProfessor.setLastName("Professor");
        testProfessor.setRole(Role.PROFESSOR);
        testProfessor.setEnabled(true);
    }

    @Nested
    @DisplayName("User Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register user successfully")
        void registerUser_Success() {
            // Arrange
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            // Act
            User result = userService.registerUser(
                    "newuser",
                    "new@example.com",
                    "password123",
                    "New",
                    "User",
                    Role.STUDENT);

            // Assert
            assertNotNull(result);
            assertEquals("newuser", result.getUsername());
            assertEquals("new@example.com", result.getEmail());
            assertEquals("encodedPassword", result.getPassword());
            assertEquals(Role.STUDENT, result.getRole());
            assertTrue(result.isEnabled());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        void registerUser_UsernameExists() {
            // Arrange
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.registerUser(
                            "existinguser",
                            "new@example.com",
                            "password",
                            "New",
                            "User",
                            Role.STUDENT));
            assertEquals("Username already exists", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void registerUser_EmailExists() {
            // Arrange
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.registerUser(
                            "newuser",
                            "existing@example.com",
                            "password",
                            "New",
                            "User",
                            Role.STUDENT));
            assertEquals("Email already exists", exception.getMessage());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Find User Tests")
    class FindUserTests {

        @Test
        @DisplayName("Should find user by username")
        void findByUsername_Found() {
            // Arrange
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Act
            Optional<User> result = userService.findByUsername("testuser");

            // Assert
            assertTrue(result.isPresent());
            assertEquals("testuser", result.get().getUsername());
        }

        @Test
        @DisplayName("Should return empty when username not found")
        void findByUsername_NotFound() {
            // Arrange
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            // Act
            Optional<User> result = userService.findByUsername("nonexistent");

            // Assert
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should find user by email")
        void findByEmail_Found() {
            // Arrange
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            // Act
            Optional<User> result = userService.findByEmail("test@example.com");

            // Assert
            assertTrue(result.isPresent());
            assertEquals("test@example.com", result.get().getEmail());
        }

        @Test
        @DisplayName("Should find user by ID")
        void findById_Found() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            Optional<User> result = userService.findById(1L);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getId());
        }

        @Test
        @DisplayName("Should get user by ID")
        void getUserById_Found() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            User result = userService.getUserById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Should throw exception when user not found by ID")
        void getUserById_NotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.getUserById(999L));
            assertTrue(exception.getMessage().contains("User not found"));
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user name by ID")
        void updateUser_ById_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User result = userService.updateUser(1L, "Updated", "Name");

            // Assert
            assertNotNull(result);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent user")
        void updateUser_ById_NotFound() {
            // Arrange
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.updateUser(999L, "Updated", "Name"));
        }

        @Test
        @DisplayName("Should update user entity")
        void updateUser_Entity_Success() {
            // Arrange
            when(userRepository.existsById(1L)).thenReturn(true);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User result = userService.updateUser(testUser);

            // Assert
            assertNotNull(result);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent user entity")
        void updateUser_Entity_NotFound() {
            // Arrange
            testUser.setId(999L);
            when(userRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.updateUser(testUser));
        }
    }

    @Nested
    @DisplayName("Password Tests")
    class PasswordTests {

        @Test
        @DisplayName("Should change password successfully")
        void changePassword_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
            when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

            // Act
            userService.changePassword(1L, "oldPassword", "newPassword");

            // Assert
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when old password is incorrect")
        void changePassword_WrongOldPassword() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.changePassword(1L, "wrongPassword", "newPassword"));
            assertEquals("Old password is incorrect", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should reset password (admin function)")
        void resetPassword_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

            // Act
            userService.resetPassword(1L, "newPassword");

            // Assert
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("User Status Tests")
    class UserStatusTests {

        @Test
        @DisplayName("Should lock user account")
        void lockUser_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            userService.lockUser(1L);

            // Assert
            verify(userRepository).save(argThat(user -> !user.isEnabled()));
        }

        @Test
        @DisplayName("Should unlock user account")
        void unlockUser_Success() {
            // Arrange
            testUser.setEnabled(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            userService.unlockUser(1L);

            // Assert
            verify(userRepository).save(argThat(User::isEnabled));
        }

        @Test
        @DisplayName("Should toggle user status")
        void toggleUserStatus_Success() {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // Act
            userService.toggleUserStatus(1L, false);

            // Assert
            verify(userRepository).save(argThat(user -> !user.isEnabled()));
        }
    }

    @Nested
    @DisplayName("Get Users Tests")
    class GetUsersTests {

        @Test
        @DisplayName("Should get all users")
        void getAllUsers_Success() {
            // Arrange
            List<User> users = Arrays.asList(testUser, testStudent, testProfessor);
            when(userRepository.findAll()).thenReturn(users);

            // Act
            List<UserDTO> result = userService.getAllUsers();

            // Assert
            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("Should get users by role")
        void getUsersByRole_Success() {
            // Arrange
            List<User> students = Arrays.asList(testUser, testStudent);
            when(userRepository.findByRole(Role.STUDENT)).thenReturn(students);

            // Act
            List<UserDTO> result = userService.getUsersByRole(Role.STUDENT);

            // Assert
            assertEquals(2, result.size());
            result.forEach(user -> assertEquals(Role.STUDENT, user.getRole()));
        }

        @Test
        @DisplayName("Should return empty list when no users with role")
        void getUsersByRole_Empty() {
            // Arrange
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of());

            // Act
            List<UserDTO> result = userService.getUsersByRole(Role.ADMIN);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Convert To DTO Tests")
    class ConvertToDTOTests {

        @Test
        @DisplayName("Should convert user to DTO")
        void convertToDTO_Success() {
            // Act
            UserDTO dto = userService.convertToDTO(testUser);

            // Assert
            assertEquals(testUser.getId(), dto.getId());
            assertEquals(testUser.getUsername(), dto.getUsername());
            assertEquals(testUser.getEmail(), dto.getEmail());
            assertEquals(testUser.getFirstName(), dto.getFirstName());
            assertEquals(testUser.getLastName(), dto.getLastName());
            assertEquals(testUser.getRole(), dto.getRole());
            assertEquals(testUser.isEnabled(), dto.isEnabled());
        }
    }
}
