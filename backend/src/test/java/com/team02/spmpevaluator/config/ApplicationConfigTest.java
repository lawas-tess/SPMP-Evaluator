package com.team02.spmpevaluator.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApplicationConfig.
 */
@DisplayName("ApplicationConfig Tests")
class ApplicationConfigTest {

    private ApplicationConfig applicationConfig;

    @BeforeEach
    void setUp() {
        applicationConfig = new ApplicationConfig();
    }

    @Nested
    @DisplayName("Password Encoder Bean Tests")
    class PasswordEncoderBeanTests {

        @Test
        @DisplayName("Should return non-null PasswordEncoder")
        void passwordEncoder_ReturnsNonNull() {
            PasswordEncoder encoder = applicationConfig.passwordEncoder();

            assertNotNull(encoder);
        }

        @Test
        @DisplayName("Should return BCryptPasswordEncoder instance")
        void passwordEncoder_ReturnsBCryptPasswordEncoder() {
            PasswordEncoder encoder = applicationConfig.passwordEncoder();

            assertInstanceOf(BCryptPasswordEncoder.class, encoder);
        }

        @Test
        @DisplayName("Should encode password correctly")
        void passwordEncoder_EncodesPassword() {
            PasswordEncoder encoder = applicationConfig.passwordEncoder();
            String rawPassword = "testPassword123";

            String encodedPassword = encoder.encode(rawPassword);

            assertNotNull(encodedPassword);
            assertNotEquals(rawPassword, encodedPassword);
            assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"));
        }

        @Test
        @DisplayName("Should match raw password with encoded password")
        void passwordEncoder_MatchesPassword() {
            PasswordEncoder encoder = applicationConfig.passwordEncoder();
            String rawPassword = "securePassword456";

            String encodedPassword = encoder.encode(rawPassword);
            boolean matches = encoder.matches(rawPassword, encodedPassword);

            assertTrue(matches);
        }

        @Test
        @DisplayName("Should not match wrong password")
        void passwordEncoder_DoesNotMatchWrongPassword() {
            PasswordEncoder encoder = applicationConfig.passwordEncoder();
            String rawPassword = "correctPassword";
            String wrongPassword = "wrongPassword";

            String encodedPassword = encoder.encode(rawPassword);
            boolean matches = encoder.matches(wrongPassword, encodedPassword);

            assertFalse(matches);
        }

        @Test
        @DisplayName("Should generate different hashes for same password")
        void passwordEncoder_GeneratesDifferentHashes() {
            PasswordEncoder encoder = applicationConfig.passwordEncoder();
            String password = "samePassword";

            String hash1 = encoder.encode(password);
            String hash2 = encoder.encode(password);

            assertNotEquals(hash1, hash2);
            // But both should match the original password
            assertTrue(encoder.matches(password, hash1));
            assertTrue(encoder.matches(password, hash2));
        }

        @Test
        @DisplayName("Should handle empty password")
        void passwordEncoder_HandlesEmptyPassword() {
            PasswordEncoder encoder = applicationConfig.passwordEncoder();
            String emptyPassword = "";

            String encodedPassword = encoder.encode(emptyPassword);

            assertNotNull(encodedPassword);
            assertTrue(encoder.matches(emptyPassword, encodedPassword));
        }

        @Test
        @DisplayName("Should handle special characters in password")
        void passwordEncoder_HandlesSpecialCharacters() {
            PasswordEncoder encoder = applicationConfig.passwordEncoder();
            String specialPassword = "P@$$w0rd!#%^&*()_+-=[]{}|;':\",./<>?";

            String encodedPassword = encoder.encode(specialPassword);

            assertNotNull(encodedPassword);
            assertTrue(encoder.matches(specialPassword, encodedPassword));
        }

        @Test
        @DisplayName("Should handle unicode characters in password")
        void passwordEncoder_HandlesUnicodeCharacters() {
            PasswordEncoder encoder = applicationConfig.passwordEncoder();
            String unicodePassword = "пароль密码パスワード🔐";

            String encodedPassword = encoder.encode(unicodePassword);

            assertNotNull(encodedPassword);
            assertTrue(encoder.matches(unicodePassword, encodedPassword));
        }

        @Test
        @DisplayName("Should handle long password within BCrypt limit")
        void passwordEncoder_HandlesLongPassword() {
            PasswordEncoder encoder = applicationConfig.passwordEncoder();
            // BCrypt has a 72-byte limit, so test with a password within that limit
            String longPassword = "a".repeat(70);
            String encodedPassword = encoder.encode(longPassword);

            assertNotNull(encodedPassword);
            assertTrue(encoder.matches(longPassword, encodedPassword));
        }

        @Test
        @DisplayName("Each call should return new encoder instance")
        void passwordEncoder_ReturnsNewInstance() {
            PasswordEncoder encoder1 = applicationConfig.passwordEncoder();
            PasswordEncoder encoder2 = applicationConfig.passwordEncoder();

            // Both should work independently
            String password = "testPassword";
            String encoded1 = encoder1.encode(password);
            String encoded2 = encoder2.encode(password);

            assertTrue(encoder1.matches(password, encoded1));
            assertTrue(encoder2.matches(password, encoded2));
            // Cross-encoder matching should also work
            assertTrue(encoder1.matches(password, encoded2));
            assertTrue(encoder2.matches(password, encoded1));
        }
    }
}
