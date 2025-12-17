package com.team02.spmpevaluator.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtUtil.
 * Tests JWT token generation and validation.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "testSecretKey123456789abcdefghijklmnopqrstuvwxyz";
    private static final long TEST_EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", TEST_EXPIRATION);
    }

    @Nested
    @DisplayName("Generate Token Tests")
    class GenerateTokenTests {

        @Test
        @DisplayName("Should generate non-null token")
        void generateToken_ValidUsername_ReturnsNonNullToken() {
            String token = jwtUtil.generateToken("testuser");

            assertNotNull(token);
            assertFalse(token.isEmpty());
        }

        @Test
        @DisplayName("Should generate token with three parts (header.payload.signature)")
        void generateToken_ValidUsername_ReturnsValidJwtFormat() {
            String token = jwtUtil.generateToken("testuser");

            String[] parts = token.split("\\.");
            assertEquals(3, parts.length);
        }

        @Test
        @DisplayName("Should generate different tokens for different users")
        void generateToken_DifferentUsers_ReturnsDifferentTokens() {
            String token1 = jwtUtil.generateToken("user1");
            String token2 = jwtUtil.generateToken("user2");

            assertNotEquals(token1, token2);
        }

        @Test
        @DisplayName("Should generate token with correct subject")
        void generateToken_SameUser_HasCorrectSubject() {
            String token = jwtUtil.generateToken("testuser");

            String extractedUsername = jwtUtil.extractUsername(token);
            assertEquals("testuser", extractedUsername);
        }
    }

    @Nested
    @DisplayName("Extract Username Tests")
    class ExtractUsernameTests {

        @Test
        @DisplayName("Should extract correct username from token")
        void extractUsername_ValidToken_ReturnsUsername() {
            String token = jwtUtil.generateToken("testuser");

            String username = jwtUtil.extractUsername(token);

            assertEquals("testuser", username);
        }

        @Test
        @DisplayName("Should extract username with special characters")
        void extractUsername_UsernameWithSpecialChars_ReturnsUsername() {
            String token = jwtUtil.generateToken("user.name@test.com");

            String username = jwtUtil.extractUsername(token);

            assertEquals("user.name@test.com", username);
        }

        @Test
        @DisplayName("Should throw exception for invalid token")
        void extractUsername_InvalidToken_ThrowsException() {
            assertThrows(Exception.class, () -> jwtUtil.extractUsername("invalid.token.here"));
        }

        @Test
        @DisplayName("Should throw exception for malformed token")
        void extractUsername_MalformedToken_ThrowsException() {
            assertThrows(Exception.class, () -> jwtUtil.extractUsername("notavalidjwt"));
        }
    }

    @Nested
    @DisplayName("Extract Expiration Tests")
    class ExtractExpirationTests {

        @Test
        @DisplayName("Should extract expiration date from token")
        void extractExpiration_ValidToken_ReturnsExpirationDate() {
            String token = jwtUtil.generateToken("testuser");

            Date expiration = jwtUtil.extractExpiration(token);

            assertNotNull(expiration);
            assertTrue(expiration.after(new Date()));
        }

        @Test
        @DisplayName("Should set expiration approximately 24 hours in future")
        void extractExpiration_ValidToken_ExpirationIs24HoursAhead() {
            String token = jwtUtil.generateToken("testuser");

            Date expiration = jwtUtil.extractExpiration(token);
            long expectedExpiration = System.currentTimeMillis() + TEST_EXPIRATION;
            long tolerance = 5000; // 5 seconds tolerance

            assertTrue(Math.abs(expiration.getTime() - expectedExpiration) < tolerance);
        }
    }

    @Nested
    @DisplayName("Validate Token Tests")
    class ValidateTokenTests {

        @Test
        @DisplayName("Should return true for valid token and matching username")
        void validateToken_ValidTokenAndMatchingUsername_ReturnsTrue() {
            String token = jwtUtil.generateToken("testuser");

            Boolean isValid = jwtUtil.validateToken(token, "testuser");

            assertTrue(isValid);
        }

        @Test
        @DisplayName("Should return false for valid token but non-matching username")
        void validateToken_ValidTokenButWrongUsername_ReturnsFalse() {
            String token = jwtUtil.generateToken("testuser");

            Boolean isValid = jwtUtil.validateToken(token, "wronguser");

            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should return false for invalid token")
        void validateToken_InvalidToken_ReturnsFalse() {
            Boolean isValid = jwtUtil.validateToken("invalid.token.here", "testuser");

            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should return false for expired token")
        void validateToken_ExpiredToken_ReturnsFalse() {
            // Create a JwtUtil with very short expiration
            JwtUtil shortExpirationJwtUtil = new JwtUtil();
            ReflectionTestUtils.setField(shortExpirationJwtUtil, "secret", TEST_SECRET);
            ReflectionTestUtils.setField(shortExpirationJwtUtil, "expirationTime", 1L); // 1ms expiration

            String token = shortExpirationJwtUtil.generateToken("testuser");

            // Wait for token to expire
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Boolean isValid = shortExpirationJwtUtil.validateToken(token, "testuser");

            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should return false for null token")
        void validateToken_NullToken_ReturnsFalse() {
            Boolean isValid = jwtUtil.validateToken(null, "testuser");

            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should return false for empty token")
        void validateToken_EmptyToken_ReturnsFalse() {
            Boolean isValid = jwtUtil.validateToken("", "testuser");

            assertFalse(isValid);
        }

        @Test
        @DisplayName("Should return false for tampered token")
        void validateToken_TamperedToken_ReturnsFalse() {
            String token = jwtUtil.generateToken("testuser");
            String tamperedToken = token.substring(0, token.length() - 5) + "xxxxx";

            Boolean isValid = jwtUtil.validateToken(tamperedToken, "testuser");

            assertFalse(isValid);
        }
    }

    @Nested
    @DisplayName("Extract Claim Tests")
    class ExtractClaimTests {

        @Test
        @DisplayName("Should extract subject claim")
        void extractClaim_SubjectClaim_ReturnsSubject() {
            String token = jwtUtil.generateToken("testuser");

            String subject = jwtUtil.extractClaim(token, claims -> claims.getSubject());

            assertEquals("testuser", subject);
        }

        @Test
        @DisplayName("Should extract issued at claim")
        void extractClaim_IssuedAtClaim_ReturnsIssuedAt() {
            String token = jwtUtil.generateToken("testuser");

            Date issuedAt = jwtUtil.extractClaim(token, claims -> claims.getIssuedAt());

            assertNotNull(issuedAt);
            // Issued at should be within 5 seconds of now (generous tolerance)
            long now = System.currentTimeMillis();
            assertTrue(Math.abs(issuedAt.getTime() - now) < 5000);
        }
    }

    @Nested
    @DisplayName("Token Format Tests")
    class TokenFormatTests {

        @Test
        @DisplayName("Should generate base64 encoded token parts")
        void generateToken_ValidUsername_Base64EncodedParts() {
            String token = jwtUtil.generateToken("testuser");

            String[] parts = token.split("\\.");
            for (String part : parts) {
                // Base64 URL safe characters only
                assertTrue(part.matches("[A-Za-z0-9_-]+"));
            }
        }
    }
}
