package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuthResponse.
 */
@DisplayName("AuthResponse Tests")
class AuthResponseTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            AuthResponse dto = new AuthResponse();

            assertNotNull(dto);
            assertNull(dto.getToken());
            assertEquals("Bearer", dto.getType());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            AuthResponse dto = new AuthResponse(
                    "jwt-token", "Bearer", 1L, "username",
                    "email@test.com", "John", "Doe", "STUDENT");

            assertEquals("jwt-token", dto.getToken());
            assertEquals("Bearer", dto.getType());
            assertEquals(1L, dto.getId());
            assertEquals("username", dto.getUsername());
            assertEquals("email@test.com", dto.getEmail());
            assertEquals("John", dto.getFirstName());
            assertEquals("Doe", dto.getLastName());
            assertEquals("STUDENT", dto.getRole());
        }

        @Test
        @DisplayName("Should create DTO with custom constructor (without type)")
        void customConstructor_SetsDefaultType() {
            AuthResponse dto = new AuthResponse(
                    "jwt-token", 1L, "username",
                    "email@test.com", "John", "Doe", "PROFESSOR");

            assertEquals("jwt-token", dto.getToken());
            assertEquals("Bearer", dto.getType());
            assertEquals(1L, dto.getId());
            assertEquals("username", dto.getUsername());
            assertEquals("email@test.com", dto.getEmail());
            assertEquals("John", dto.getFirstName());
            assertEquals("Doe", dto.getLastName());
            assertEquals("PROFESSOR", dto.getRole());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get token")
        void setAndGetToken() {
            AuthResponse dto = new AuthResponse();
            dto.setToken("new-token");
            assertEquals("new-token", dto.getToken());
        }

        @Test
        @DisplayName("Should set and get type")
        void setAndGetType() {
            AuthResponse dto = new AuthResponse();
            dto.setType("Custom");
            assertEquals("Custom", dto.getType());
        }

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            AuthResponse dto = new AuthResponse();
            dto.setId(100L);
            assertEquals(100L, dto.getId());
        }

        @Test
        @DisplayName("Should set and get username")
        void setAndGetUsername() {
            AuthResponse dto = new AuthResponse();
            dto.setUsername("testuser");
            assertEquals("testuser", dto.getUsername());
        }

        @Test
        @DisplayName("Should set and get email")
        void setAndGetEmail() {
            AuthResponse dto = new AuthResponse();
            dto.setEmail("test@example.com");
            assertEquals("test@example.com", dto.getEmail());
        }

        @Test
        @DisplayName("Should set and get firstName")
        void setAndGetFirstName() {
            AuthResponse dto = new AuthResponse();
            dto.setFirstName("Test");
            assertEquals("Test", dto.getFirstName());
        }

        @Test
        @DisplayName("Should set and get lastName")
        void setAndGetLastName() {
            AuthResponse dto = new AuthResponse();
            dto.setLastName("User");
            assertEquals("User", dto.getLastName());
        }

        @Test
        @DisplayName("Should set and get role")
        void setAndGetRole() {
            AuthResponse dto = new AuthResponse();
            dto.setRole("ADMIN");
            assertEquals("ADMIN", dto.getRole());
        }
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Type should default to Bearer")
        void type_DefaultsToBearer() {
            AuthResponse dto = new AuthResponse();
            assertEquals("Bearer", dto.getType());
        }

        @Test
        @DisplayName("Custom constructor preserves Bearer type")
        void customConstructor_PreservesBearerType() {
            AuthResponse dto = new AuthResponse("token", 1L, "user", "email@test.com", "F", "L", "ROLE");
            assertEquals("Bearer", dto.getType());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            AuthResponse dto1 = new AuthResponse("token", 1L, "user", "email@test.com", "First", "Last", "ROLE");
            AuthResponse dto2 = new AuthResponse("token", 1L, "user", "email@test.com", "First", "Last", "ROLE");

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different tokens")
        void equals_DifferentTokens_ReturnsFalse() {
            AuthResponse dto1 = new AuthResponse("token1", 1L, "user", "email@test.com", "F", "L", "R");
            AuthResponse dto2 = new AuthResponse("token2", 1L, "user", "email@test.com", "F", "L", "R");

            assertNotEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with all fields")
        void toString_ContainsAllFields() {
            AuthResponse dto = new AuthResponse("jwt-token", 1L, "testuser", "test@email.com", "John", "Doe",
                    "STUDENT");

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("token=jwt-token"));
            assertTrue(result.contains("username=testuser"));
            assertTrue(result.contains("role=STUDENT"));
        }
    }
}
