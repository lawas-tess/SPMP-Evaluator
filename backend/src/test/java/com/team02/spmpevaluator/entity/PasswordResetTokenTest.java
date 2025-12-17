package com.team02.spmpevaluator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordResetToken entity.
 */
@DisplayName("PasswordResetToken Entity Tests")
class PasswordResetTokenTest {

    private PasswordResetToken token;
    private User user;

    @BeforeEach
    void setUp() {
        token = new PasswordResetToken();
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty entity with no-args constructor")
        void noArgsConstructor_CreatesEmptyEntity() {
            PasswordResetToken entity = new PasswordResetToken();
            assertNotNull(entity);
            assertNull(entity.getId());
            assertNull(entity.getToken());
            assertNull(entity.getUser());
            assertNull(entity.getExpiryDate());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void testId() {
            token.setId(100L);
            assertEquals(100L, token.getId());
        }

        @Test
        @DisplayName("Should set and get token string")
        void testToken() {
            String tokenValue = "abc123def456-reset-token";
            token.setToken(tokenValue);
            assertEquals(tokenValue, token.getToken());
        }

        @Test
        @DisplayName("Should set and get user")
        void testUser() {
            token.setUser(user);
            assertEquals(user, token.getUser());
            assertEquals(1L, token.getUser().getId());
            assertEquals("testuser", token.getUser().getUsername());
        }

        @Test
        @DisplayName("Should set and get expiryDate")
        void testExpiryDate() {
            LocalDateTime expiry = LocalDateTime.now().plusHours(24);
            token.setExpiryDate(expiry);
            assertEquals(expiry, token.getExpiryDate());
        }
    }

    @Nested
    @DisplayName("isExpired Method Tests")
    class IsExpiredTests {

        @Test
        @DisplayName("Should return false when token is not expired")
        void notExpired() {
            // Token expires in 1 hour
            token.setExpiryDate(LocalDateTime.now().plusHours(1));
            assertFalse(token.isExpired());
        }

        @Test
        @DisplayName("Should return true when token is expired")
        void isExpired() {
            // Token expired 1 hour ago
            token.setExpiryDate(LocalDateTime.now().minusHours(1));
            assertTrue(token.isExpired());
        }

        @Test
        @DisplayName("Should return true when token expired just now")
        void justExpired() {
            // Token expired 1 second ago
            token.setExpiryDate(LocalDateTime.now().minusSeconds(1));
            assertTrue(token.isExpired());
        }

        @Test
        @DisplayName("Should return false when token expires in the future")
        void expiresInFuture() {
            // Token expires in 24 hours
            token.setExpiryDate(LocalDateTime.now().plusDays(1));
            assertFalse(token.isExpired());
        }

        @Test
        @DisplayName("Should handle token expiring soon")
        void expiringSoon() {
            // Token expires in 1 minute
            token.setExpiryDate(LocalDateTime.now().plusMinutes(1));
            assertFalse(token.isExpired());
        }
    }

    @Nested
    @DisplayName("Token Validity Tests")
    class TokenValidityTests {

        @Test
        @DisplayName("Should handle 24-hour expiry (typical reset window)")
        void typicalResetWindow() {
            token.setExpiryDate(LocalDateTime.now().plusHours(24));
            assertFalse(token.isExpired());
        }

        @Test
        @DisplayName("Should handle 1-hour expiry (short reset window)")
        void shortResetWindow() {
            token.setExpiryDate(LocalDateTime.now().plusHours(1));
            assertFalse(token.isExpired());
        }

        @Test
        @DisplayName("Should handle long-expired token")
        void longExpiredToken() {
            token.setExpiryDate(LocalDateTime.now().minusDays(7));
            assertTrue(token.isExpired());
        }
    }

    @Nested
    @DisplayName("Token Format Tests")
    class TokenFormatTests {

        @Test
        @DisplayName("Should accept UUID-style token")
        void uuidToken() {
            String uuidToken = "550e8400-e29b-41d4-a716-446655440000";
            token.setToken(uuidToken);
            assertEquals(uuidToken, token.getToken());
        }

        @Test
        @DisplayName("Should accept alphanumeric token")
        void alphanumericToken() {
            String alphaToken = "AbC123XyZ789Reset";
            token.setToken(alphaToken);
            assertEquals(alphaToken, token.getToken());
        }

        @Test
        @DisplayName("Should handle empty token")
        void emptyToken() {
            token.setToken("");
            assertEquals("", token.getToken());
        }

        @Test
        @DisplayName("Should handle null token")
        void nullToken() {
            token.setToken(null);
            assertNull(token.getToken());
        }
    }

    @Nested
    @DisplayName("User Association Tests")
    class UserAssociationTests {

        @Test
        @DisplayName("Should associate token with user")
        void associateWithUser() {
            token.setUser(user);
            token.setToken("reset-token-123");
            token.setExpiryDate(LocalDateTime.now().plusHours(24));

            assertEquals(user, token.getUser());
            assertEquals("testuser", token.getUser().getUsername());
            assertEquals("test@example.com", token.getUser().getEmail());
        }

        @Test
        @DisplayName("Should allow changing associated user")
        void changeAssociatedUser() {
            token.setUser(user);

            User newUser = new User();
            newUser.setId(2L);
            newUser.setUsername("newuser");

            token.setUser(newUser);
            assertEquals(2L, token.getUser().getId());
            assertEquals("newuser", token.getUser().getUsername());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when same id")
        void equalsWithSameId() {
            PasswordResetToken token1 = new PasswordResetToken();
            token1.setId(1L);
            token1.setToken("token1");

            PasswordResetToken token2 = new PasswordResetToken();
            token2.setId(1L);
            token2.setToken("token1");

            assertEquals(token1, token2);
            assertEquals(token1.hashCode(), token2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when different ids")
        void notEqualsWithDifferentIds() {
            PasswordResetToken token1 = new PasswordResetToken();
            token1.setId(1L);

            PasswordResetToken token2 = new PasswordResetToken();
            token2.setId(2L);

            assertNotEquals(token1, token2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should include key fields in toString")
        void toStringContainsFields() {
            token.setId(1L);
            token.setToken("test-token");
            token.setExpiryDate(LocalDateTime.now().plusHours(24));

            String str = token.toString();
            assertNotNull(str);
            assertTrue(str.contains("1"));
        }
    }
}
