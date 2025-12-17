package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ErrorResponse.
 */
@DisplayName("ErrorResponse Tests")
class ErrorResponseTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            ErrorResponse dto = new ErrorResponse();

            assertNotNull(dto);
            assertNull(dto.getMessage());
            assertEquals(0, dto.getStatus());
            assertEquals(0L, dto.getTimestamp());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            long timestamp = System.currentTimeMillis();
            ErrorResponse dto = new ErrorResponse("Error message", 404, timestamp);

            assertEquals("Error message", dto.getMessage());
            assertEquals(404, dto.getStatus());
            assertEquals(timestamp, dto.getTimestamp());
        }

        @Test
        @DisplayName("Should create DTO with two-args constructor and auto-generate timestamp")
        void twoArgsConstructor_AutoGeneratesTimestamp() {
            long beforeCreation = System.currentTimeMillis();
            ErrorResponse dto = new ErrorResponse("Error occurred", 500);
            long afterCreation = System.currentTimeMillis();

            assertEquals("Error occurred", dto.getMessage());
            assertEquals(500, dto.getStatus());
            assertTrue(dto.getTimestamp() >= beforeCreation);
            assertTrue(dto.getTimestamp() <= afterCreation);
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get message")
        void setAndGetMessage() {
            ErrorResponse dto = new ErrorResponse();
            dto.setMessage("Test error message");
            assertEquals("Test error message", dto.getMessage());
        }

        @Test
        @DisplayName("Should set and get status")
        void setAndGetStatus() {
            ErrorResponse dto = new ErrorResponse();
            dto.setStatus(400);
            assertEquals(400, dto.getStatus());
        }

        @Test
        @DisplayName("Should set and get timestamp")
        void setAndGetTimestamp() {
            ErrorResponse dto = new ErrorResponse();
            long timestamp = 1234567890L;
            dto.setTimestamp(timestamp);
            assertEquals(timestamp, dto.getTimestamp());
        }
    }

    @Nested
    @DisplayName("HTTP Status Codes Tests")
    class HttpStatusCodesTests {

        @Test
        @DisplayName("Should handle 400 Bad Request")
        void status_BadRequest() {
            ErrorResponse dto = new ErrorResponse("Bad request", 400);
            assertEquals(400, dto.getStatus());
        }

        @Test
        @DisplayName("Should handle 401 Unauthorized")
        void status_Unauthorized() {
            ErrorResponse dto = new ErrorResponse("Unauthorized", 401);
            assertEquals(401, dto.getStatus());
        }

        @Test
        @DisplayName("Should handle 403 Forbidden")
        void status_Forbidden() {
            ErrorResponse dto = new ErrorResponse("Forbidden", 403);
            assertEquals(403, dto.getStatus());
        }

        @Test
        @DisplayName("Should handle 404 Not Found")
        void status_NotFound() {
            ErrorResponse dto = new ErrorResponse("Not found", 404);
            assertEquals(404, dto.getStatus());
        }

        @Test
        @DisplayName("Should handle 500 Internal Server Error")
        void status_InternalServerError() {
            ErrorResponse dto = new ErrorResponse("Internal server error", 500);
            assertEquals(500, dto.getStatus());
        }

        @Test
        @DisplayName("Should handle 503 Service Unavailable")
        void status_ServiceUnavailable() {
            ErrorResponse dto = new ErrorResponse("Service unavailable", 503);
            assertEquals(503, dto.getStatus());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal for same values")
        void equals_SameValues_ReturnsTrue() {
            long timestamp = System.currentTimeMillis();
            ErrorResponse dto1 = new ErrorResponse("Error", 404, timestamp);
            ErrorResponse dto2 = new ErrorResponse("Error", 404, timestamp);

            assertEquals(dto1, dto2);
            assertEquals(dto1.hashCode(), dto2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal for different messages")
        void equals_DifferentMessages_ReturnsFalse() {
            long timestamp = System.currentTimeMillis();
            ErrorResponse dto1 = new ErrorResponse("Error 1", 404, timestamp);
            ErrorResponse dto2 = new ErrorResponse("Error 2", 404, timestamp);

            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("Should not be equal for different status")
        void equals_DifferentStatus_ReturnsFalse() {
            long timestamp = System.currentTimeMillis();
            ErrorResponse dto1 = new ErrorResponse("Error", 404, timestamp);
            ErrorResponse dto2 = new ErrorResponse("Error", 500, timestamp);

            assertNotEquals(dto1, dto2);
        }

        @Test
        @DisplayName("Should not be equal for different timestamps")
        void equals_DifferentTimestamps_ReturnsFalse() {
            ErrorResponse dto1 = new ErrorResponse("Error", 404, 1000L);
            ErrorResponse dto2 = new ErrorResponse("Error", 404, 2000L);

            assertNotEquals(dto1, dto2);
        }
    }

    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {

        @Test
        @DisplayName("Should generate toString with all fields")
        void toString_ContainsAllFields() {
            ErrorResponse dto = new ErrorResponse("Test error", 404, 1234567890L);

            String result = dto.toString();

            assertNotNull(result);
            assertTrue(result.contains("message=Test error"));
            assertTrue(result.contains("status=404"));
            assertTrue(result.contains("timestamp=1234567890"));
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle empty message")
        void emptyMessage() {
            ErrorResponse dto = new ErrorResponse("", 400);
            assertEquals("", dto.getMessage());
        }

        @Test
        @DisplayName("Should handle null message")
        void nullMessage() {
            ErrorResponse dto = new ErrorResponse();
            dto.setMessage(null);
            assertNull(dto.getMessage());
        }

        @Test
        @DisplayName("Should handle zero status")
        void zeroStatus() {
            ErrorResponse dto = new ErrorResponse("Error", 0);
            assertEquals(0, dto.getStatus());
        }

        @Test
        @DisplayName("Should handle negative status")
        void negativeStatus() {
            ErrorResponse dto = new ErrorResponse();
            dto.setStatus(-1);
            assertEquals(-1, dto.getStatus());
        }
    }
}
