package com.team02.spmpevaluator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team02.spmpevaluator.dto.LoginRequest;
import com.team02.spmpevaluator.dto.RegisterRequest;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.security.JwtUtil;
import com.team02.spmpevaluator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AuthController.
 * Tests authentication endpoints including registration, login, and token
 * validation.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    private User testUser;

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
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should register user successfully")
        void register_Success() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "newuser",
                    "new@example.com",
                    "password123",
                    "New",
                    "User",
                    "STUDENT");

            when(userService.registerUser(
                    eq("newuser"),
                    eq("new@example.com"),
                    eq("password123"),
                    eq("New"),
                    eq("User"),
                    eq(Role.STUDENT))).thenReturn(testUser);

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("Should return error for invalid role")
        void register_InvalidRole() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "newuser",
                    "new@example.com",
                    "password123",
                    "New",
                    "User",
                    "INVALID_ROLE");

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid role. Allowed values: STUDENT, PROFESSOR"));
        }

        @Test
        @DisplayName("Should return error when username exists")
        void register_UsernameExists() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "existinguser",
                    "new@example.com",
                    "password123",
                    "New",
                    "User",
                    "STUDENT");

            when(userService.registerUser(
                    anyString(), anyString(), anyString(), anyString(), anyString(), any(Role.class)))
                    .thenThrow(new IllegalArgumentException("Username already exists"));

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Username already exists"));
        }

        @Test
        @DisplayName("Should return error when email exists")
        void register_EmailExists() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "newuser",
                    "existing@example.com",
                    "password123",
                    "New",
                    "User",
                    "STUDENT");

            when(userService.registerUser(
                    anyString(), anyString(), anyString(), anyString(), anyString(), any(Role.class)))
                    .thenThrow(new IllegalArgumentException("Email already exists"));

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email already exists"));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully")
        void login_Success() throws Exception {
            LoginRequest request = new LoginRequest("testuser", "password123");

            when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken("testuser", "password123"));
            when(jwtUtil.generateToken("testuser")).thenReturn("jwt-token-here");

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token-here"))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.role").value("STUDENT"));
        }

        @Test
        @DisplayName("Should return error for invalid username")
        void login_InvalidUsername() throws Exception {
            LoginRequest request = new LoginRequest("nonexistent", "password123");

            when(userService.findByUsername("nonexistent")).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("Should return error for invalid password")
        void login_InvalidPassword() throws Exception {
            LoginRequest request = new LoginRequest("testuser", "wrongpassword");

            when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate valid token")
        void validateToken_Valid() throws Exception {
            when(jwtUtil.extractUsername("valid-token")).thenReturn("testuser");
            when(jwtUtil.validateToken("valid-token", "testuser")).thenReturn(true);
            when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            mockMvc.perform(get("/api/auth/validate")
                    .header("Authorization", "Bearer valid-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("Should return error for expired token")
        void validateToken_Expired() throws Exception {
            when(jwtUtil.extractUsername("expired-token")).thenReturn("testuser");
            when(jwtUtil.validateToken("expired-token", "testuser")).thenReturn(false);

            mockMvc.perform(get("/api/auth/validate")
                    .header("Authorization", "Bearer expired-token"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return error for missing authorization header")
        void validateToken_MissingHeader() throws Exception {
            mockMvc.perform(get("/api/auth/validate"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return error for invalid authorization header format")
        void validateToken_InvalidFormat() throws Exception {
            mockMvc.perform(get("/api/auth/validate")
                    .header("Authorization", "InvalidFormat token"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Health Check Tests")
    class HealthCheckTests {

        @Test
        @DisplayName("Should return health status")
        void health_Success() throws Exception {
            mockMvc.perform(get("/api/auth/health"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Authentication service is running"));
        }
    }

    @Nested
    @DisplayName("Password Reset Tests")
    class PasswordResetTests {

        @Test
        @DisplayName("Should send forgot password email")
        void forgotPassword_Success() throws Exception {
            doNothing().when(userService).processForgotPassword("test@example.com");

            mockMvc.perform(post("/api/auth/forgot-password")
                    .param("email", "test@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Password reset link sent to email."));
        }

        @Test
        @DisplayName("Should return error for non-existent email")
        void forgotPassword_EmailNotFound() throws Exception {
            doThrow(new IllegalArgumentException("Email not found"))
                    .when(userService).processForgotPassword("nonexistent@example.com");

            mockMvc.perform(post("/api/auth/forgot-password")
                    .param("email", "nonexistent@example.com"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email not found"));
        }

        @Test
        @DisplayName("Should reset password with valid token")
        void resetPassword_Success() throws Exception {
            doNothing().when(userService).resetPasswordWithToken("valid-token", "newPassword123");

            mockMvc.perform(post("/api/auth/reset-password")
                    .param("token", "valid-token")
                    .param("newPassword", "newPassword123"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Password successfully reset."));
        }

        @Test
        @DisplayName("Should return error for invalid reset token")
        void resetPassword_InvalidToken() throws Exception {
            doThrow(new IllegalArgumentException("Invalid or expired token"))
                    .when(userService).resetPasswordWithToken("invalid-token", "newPassword123");

            mockMvc.perform(post("/api/auth/reset-password")
                    .param("token", "invalid-token")
                    .param("newPassword", "newPassword123"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid or expired token"));
        }
    }
}
