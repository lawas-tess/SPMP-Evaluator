package com.team02.spmpevaluator.security;

import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CustomUserDetails.
 * Tests Spring Security UserDetails implementation.
 */
class CustomUserDetailsTest {

    private User testUser;
    private CustomUserDetails customUserDetails;

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
    @DisplayName("Get Authorities Tests")
    class GetAuthoritiesTests {

        @Test
        @DisplayName("Should return ROLE_STUDENT authority for student user")
        void getAuthorities_StudentRole_ReturnsRoleStudent() {
            testUser.setRole(Role.STUDENT);
            customUserDetails = new CustomUserDetails(testUser);

            Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

            assertEquals(1, authorities.size());
            assertTrue(authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
        }

        @Test
        @DisplayName("Should return ROLE_PROFESSOR authority for professor user")
        void getAuthorities_ProfessorRole_ReturnsRoleProfessor() {
            testUser.setRole(Role.PROFESSOR);
            customUserDetails = new CustomUserDetails(testUser);

            Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

            assertEquals(1, authorities.size());
            assertTrue(authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSOR")));
        }

        @Test
        @DisplayName("Should return ROLE_ADMIN authority for admin user")
        void getAuthorities_AdminRole_ReturnsRoleAdmin() {
            testUser.setRole(Role.ADMIN);
            customUserDetails = new CustomUserDetails(testUser);

            Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

            assertEquals(1, authorities.size());
            assertTrue(authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }
    }

    @Nested
    @DisplayName("Get Credentials Tests")
    class GetCredentialsTests {

        @Test
        @DisplayName("Should return user password")
        void getPassword_ReturnsUserPassword() {
            customUserDetails = new CustomUserDetails(testUser);

            assertEquals("encodedPassword123", customUserDetails.getPassword());
        }

        @Test
        @DisplayName("Should return username")
        void getUsername_ReturnsUsername() {
            customUserDetails = new CustomUserDetails(testUser);

            assertEquals("testuser", customUserDetails.getUsername());
        }
    }

    @Nested
    @DisplayName("Account Status Tests")
    class AccountStatusTests {

        @Test
        @DisplayName("Should return true for isAccountNonExpired")
        void isAccountNonExpired_ReturnsTrue() {
            customUserDetails = new CustomUserDetails(testUser);

            assertTrue(customUserDetails.isAccountNonExpired());
        }

        @Test
        @DisplayName("Should return true for isAccountNonLocked")
        void isAccountNonLocked_ReturnsTrue() {
            customUserDetails = new CustomUserDetails(testUser);

            assertTrue(customUserDetails.isAccountNonLocked());
        }

        @Test
        @DisplayName("Should return true for isCredentialsNonExpired")
        void isCredentialsNonExpired_ReturnsTrue() {
            customUserDetails = new CustomUserDetails(testUser);

            assertTrue(customUserDetails.isCredentialsNonExpired());
        }

        @Test
        @DisplayName("Should return true for isEnabled when user is enabled")
        void isEnabled_UserEnabled_ReturnsTrue() {
            testUser.setEnabled(true);
            customUserDetails = new CustomUserDetails(testUser);

            assertTrue(customUserDetails.isEnabled());
        }

        @Test
        @DisplayName("Should return false for isEnabled when user is disabled")
        void isEnabled_UserDisabled_ReturnsFalse() {
            testUser.setEnabled(false);
            customUserDetails = new CustomUserDetails(testUser);

            assertFalse(customUserDetails.isEnabled());
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Should return underlying User entity")
        void getUser_ReturnsUserEntity() {
            customUserDetails = new CustomUserDetails(testUser);

            User result = customUserDetails.getUser();

            assertNotNull(result);
            assertEquals(testUser.getId(), result.getId());
            assertEquals(testUser.getEmail(), result.getEmail());
        }

        @Test
        @DisplayName("Should return same user reference")
        void getUser_ReturnsSameReference() {
            customUserDetails = new CustomUserDetails(testUser);

            assertSame(testUser, customUserDetails.getUser());
        }
    }
}
