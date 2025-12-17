package com.team02.spmpevaluator.security;

import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomUserDetailsService.
 * Tests user loading for Spring Security authentication.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setRole(Role.STUDENT);
        testUser.setEnabled(true);
    }

    @Nested
    @DisplayName("Load User by Username Tests")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Should load user successfully when user exists and is enabled")
        void loadUserByUsername_UserExistsAndEnabled_ReturnsUserDetails() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
            assertTrue(result.isEnabled());
            verify(userRepository).findByUsername("testuser");
        }

        @Test
        @DisplayName("Should return CustomUserDetails instance")
        void loadUserByUsername_ReturnsCustomUserDetailsInstance() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            assertInstanceOf(CustomUserDetails.class, result);
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user not found")
        void loadUserByUsername_UserNotFound_ThrowsException() {
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> customUserDetailsService.loadUserByUsername("nonexistent"));

            assertTrue(exception.getMessage().contains("User not found"));
            assertTrue(exception.getMessage().contains("nonexistent"));
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user is disabled")
        void loadUserByUsername_UserDisabled_ThrowsException() {
            testUser.setEnabled(false);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> customUserDetailsService.loadUserByUsername("testuser"));

            assertTrue(exception.getMessage().contains("disabled"));
        }

        @Test
        @DisplayName("Should return user with correct authorities")
        void loadUserByUsername_ReturnsUserWithCorrectAuthorities() {
            testUser.setRole(Role.PROFESSOR);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            assertTrue(result.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR")));
        }

        @Test
        @DisplayName("Should return user with correct password")
        void loadUserByUsername_ReturnsUserWithCorrectPassword() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            assertEquals("encodedPassword123", result.getPassword());
        }
    }

    @Nested
    @DisplayName("Role Authority Tests")
    class RoleAuthorityTests {

        @Test
        @DisplayName("Should return ROLE_STUDENT for student user")
        void loadUserByUsername_StudentRole_HasStudentAuthority() {
            testUser.setRole(Role.STUDENT);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            assertTrue(result.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
        }

        @Test
        @DisplayName("Should return ROLE_ADMIN for admin user")
        void loadUserByUsername_AdminRole_HasAdminAuthority() {
            testUser.setRole(Role.ADMIN);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

            assertTrue(result.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }
    }
}
