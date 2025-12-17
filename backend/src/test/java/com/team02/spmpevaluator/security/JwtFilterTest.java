package com.team02.spmpevaluator.security;

import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtFilter.
 * Tests JWT authentication filter functionality.
 */
@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    private User testUser;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setRole(Role.STUDENT);
        testUser.setEnabled(true);

        customUserDetails = new CustomUserDetails(testUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Filter Chain Tests")
    class FilterChainTests {

        @Test
        @DisplayName("Should continue filter chain when no Authorization header")
        void doFilterInternal_NoAuthHeader_ContinuesChain() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(null);

            jwtFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Should continue filter chain when Authorization header doesn't start with Bearer")
        void doFilterInternal_NonBearerHeader_ContinuesChain() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

            jwtFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Should always call filter chain")
        void doFilterInternal_AnyRequest_AlwaysCallsFilterChain() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
            when(jwtUtil.extractUsername("valid.token.here")).thenReturn("testuser");
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(customUserDetails);
            when(jwtUtil.validateToken("valid.token.here", "testuser")).thenReturn(true);

            jwtFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Valid Token Tests")
    class ValidTokenTests {

        @Test
        @DisplayName("Should set authentication when token is valid")
        void doFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
            when(jwtUtil.extractUsername("valid.token.here")).thenReturn("testuser");
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(customUserDetails);
            when(jwtUtil.validateToken("valid.token.here", "testuser")).thenReturn(true);

            jwtFilter.doFilterInternal(request, response, filterChain);

            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
        }

        @Test
        @DisplayName("Should set correct authorities in authentication")
        void doFilterInternal_ValidToken_SetsCorrectAuthorities() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
            when(jwtUtil.extractUsername("valid.token.here")).thenReturn("testuser");
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(customUserDetails);
            when(jwtUtil.validateToken("valid.token.here", "testuser")).thenReturn(true);

            jwtFilter.doFilterInternal(request, response, filterChain);

            assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
        }

        @Test
        @DisplayName("Should set user details as principal")
        void doFilterInternal_ValidToken_SetsPrincipal() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
            when(jwtUtil.extractUsername("valid.token.here")).thenReturn("testuser");
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(customUserDetails);
            when(jwtUtil.validateToken("valid.token.here", "testuser")).thenReturn(true);

            jwtFilter.doFilterInternal(request, response, filterChain);

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            assertInstanceOf(CustomUserDetails.class, principal);
        }
    }

    @Nested
    @DisplayName("Invalid Token Tests")
    class InvalidTokenTests {

        @Test
        @DisplayName("Should not set authentication when token validation fails")
        void doFilterInternal_InvalidToken_NoAuthentication() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token.here");
            when(jwtUtil.extractUsername("invalid.token.here")).thenReturn("testuser");
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(customUserDetails);
            when(jwtUtil.validateToken("invalid.token.here", "testuser")).thenReturn(false);

            jwtFilter.doFilterInternal(request, response, filterChain);

            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle exception when extracting username fails")
        void doFilterInternal_ExtractUsernameException_ContinuesChain() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer malformed.token");
            when(jwtUtil.extractUsername("malformed.token")).thenThrow(new RuntimeException("Invalid token"));

            jwtFilter.doFilterInternal(request, response, filterChain);

            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should handle null username from token")
        void doFilterInternal_NullUsername_ContinuesChain() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer some.token.here");
            when(jwtUtil.extractUsername("some.token.here")).thenReturn(null);

            jwtFilter.doFilterInternal(request, response, filterChain);

            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Authentication Context Tests")
    class AuthenticationContextTests {

        @Test
        @DisplayName("Should not override existing authentication")
        void doFilterInternal_ExistingAuthentication_NoOverride() throws ServletException, IOException {
            // Set up existing authentication
            when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
            when(jwtUtil.extractUsername("valid.token.here")).thenReturn("testuser");
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(customUserDetails);
            when(jwtUtil.validateToken("valid.token.here", "testuser")).thenReturn(true);

            // First call sets authentication
            jwtFilter.doFilterInternal(request, response, filterChain);

            // Second call with different token shouldn't override
            User otherUser = new User();
            otherUser.setUsername("otheruser");
            otherUser.setRole(Role.PROFESSOR);
            otherUser.setEnabled(true);

            when(jwtUtil.extractUsername("valid.token.here")).thenReturn("otheruser");

            jwtFilter.doFilterInternal(request, response, filterChain);

            // Authentication should still be for testuser
            assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }

    @Nested
    @DisplayName("Bearer Token Extraction Tests")
    class BearerTokenExtractionTests {

        @Test
        @DisplayName("Should extract token after 'Bearer ' prefix")
        void doFilterInternal_BearerPrefix_ExtractsToken() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer mytoken123");
            when(jwtUtil.extractUsername("mytoken123")).thenReturn("testuser");
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(customUserDetails);
            when(jwtUtil.validateToken("mytoken123", "testuser")).thenReturn(true);

            jwtFilter.doFilterInternal(request, response, filterChain);

            verify(jwtUtil).extractUsername("mytoken123");
        }

        @Test
        @DisplayName("Should handle Bearer with extra spaces")
        void doFilterInternal_BearerWithToken_HandlesCorrectly() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer   token.with.spaces");

            jwtFilter.doFilterInternal(request, response, filterChain);

            // Token starts at index 7, includes spaces
            verify(jwtUtil).extractUsername("  token.with.spaces");
        }
    }

    @Nested
    @DisplayName("Exception Handling Tests")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Should continue filter chain even when user details service throws exception")
        void doFilterInternal_UserDetailsServiceException_ContinuesChain() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
            when(jwtUtil.extractUsername("valid.token.here")).thenReturn("testuser");
            when(userDetailsService.loadUserByUsername("testuser"))
                    .thenThrow(new RuntimeException("Database error"));

            jwtFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should not set authentication when exception occurs")
        void doFilterInternal_ExceptionOccurs_NoAuthentication() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
            when(jwtUtil.extractUsername("valid.token.here")).thenReturn("testuser");
            when(userDetailsService.loadUserByUsername("testuser"))
                    .thenThrow(new RuntimeException("Database error"));

            jwtFilter.doFilterInternal(request, response, filterChain);

            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }
}
