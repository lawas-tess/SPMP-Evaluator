package com.team02.spmpevaluator.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TaskDTO.
 */
@DisplayName("TaskDTO Tests")
class TaskDTOTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create empty DTO with no-args constructor")
        void noArgsConstructor_CreatesEmptyDTO() {
            TaskDTO dto = new TaskDTO();

            assertNotNull(dto);
            assertNull(dto.getId());
            assertNull(dto.getTitle());
            assertNull(dto.getDescription());
            assertNull(dto.getDeadline());
            assertFalse(dto.isCompleted());
        }

        @Test
        @DisplayName("Should create DTO with all-args constructor")
        void allArgsConstructor_CreatesPopulatedDTO() {
            LocalDateTime deadline = LocalDateTime.of(2025, 12, 31, 23, 59);
            LocalDate completionDate = LocalDate.of(2025, 12, 25);
            LocalDateTime createdAt = LocalDateTime.of(2025, 12, 1, 10, 0);

            TaskDTO dto = new TaskDTO(
                    1L, "Complete documentation", "Write technical docs",
                    deadline, "HIGH", "IN_PROGRESS", false,
                    10L, "johndoe", "John", "Doe",
                    5L, "admin", completionDate, createdAt);

            assertEquals(1L, dto.getId());
            assertEquals("Complete documentation", dto.getTitle());
            assertEquals("Write technical docs", dto.getDescription());
            assertEquals(deadline, dto.getDeadline());
            assertEquals("HIGH", dto.getPriority());
            assertEquals("IN_PROGRESS", dto.getStatus());
            assertFalse(dto.isCompleted());
            assertEquals(10L, dto.getAssignedToUserId());
            assertEquals("johndoe", dto.getAssignedToUsername());
            assertEquals("John", dto.getAssignedToFirstName());
            assertEquals("Doe", dto.getAssignedToLastName());
            assertEquals(5L, dto.getCreatedByUserId());
            assertEquals("admin", dto.getCreatedByUsername());
            assertEquals(completionDate, dto.getCompletionDate());
            assertEquals(createdAt, dto.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void setAndGetId() {
            TaskDTO dto = new TaskDTO();
            dto.setId(100L);
            assertEquals(100L, dto.getId());
        }

        @Test
        @DisplayName("Should set and get title")
        void setAndGetTitle() {
            TaskDTO dto = new TaskDTO();
            dto.setTitle("Review SPMP document");
            assertEquals("Review SPMP document", dto.getTitle());
        }

        @Test
        @DisplayName("Should set and get description")
        void setAndGetDescription() {
            TaskDTO dto = new TaskDTO();
            dto.setDescription("Review and provide feedback");
            assertEquals("Review and provide feedback", dto.getDescription());
        }

        @Test
        @DisplayName("Should set and get deadline")
        void setAndGetDeadline() {
            TaskDTO dto = new TaskDTO();
            LocalDateTime deadline = LocalDateTime.of(2025, 12, 31, 17, 0);
            dto.setDeadline(deadline);
            assertEquals(deadline, dto.getDeadline());
        }

        @Test
        @DisplayName("Should set and get priority")
        void setAndGetPriority() {
            TaskDTO dto = new TaskDTO();
            dto.setPriority("MEDIUM");
            assertEquals("MEDIUM", dto.getPriority());
        }

        @Test
        @DisplayName("Should set and get status")
        void setAndGetStatus() {
            TaskDTO dto = new TaskDTO();
            dto.setStatus("COMPLETED");
            assertEquals("COMPLETED", dto.getStatus());
        }

        @Test
        @DisplayName("Should set and get completed")
        void setAndGetCompleted() {
            TaskDTO dto = new TaskDTO();
            dto.setCompleted(true);
            assertTrue(dto.isCompleted());

            dto.setCompleted(false);
            assertFalse(dto.isCompleted());
        }

        @Test
        @DisplayName("Should set and get assignedToUserId")
        void setAndGetAssignedToUserId() {
            TaskDTO dto = new TaskDTO();
            dto.setAssignedToUserId(50L);
            assertEquals(50L, dto.getAssignedToUserId());
        }

        @Test
        @DisplayName("Should set and get assignedToUsername")
        void setAndGetAssignedToUsername() {
            TaskDTO dto = new TaskDTO();
            dto.setAssignedToUsername("student1");
            assertEquals("student1", dto.getAssignedToUsername());
        }

        @Test
        @DisplayName("Should set and get assignedToFirstName")
        void setAndGetAssignedToFirstName() {
            TaskDTO dto = new TaskDTO();
            dto.setAssignedToFirstName("Jane");
            assertEquals("Jane", dto.getAssignedToFirstName());
        }

        @Test
        @DisplayName("Should set and get assignedToLastName")
        void setAndGetAssignedToLastName() {
            TaskDTO dto = new TaskDTO();
            dto.setAssignedToLastName("Smith");
            assertEquals("Smith", dto.getAssignedToLastName());
        }

        @Test
        @DisplayName("Should set and get createdByUserId")
        void setAndGetCreatedByUserId() {
            TaskDTO dto = new TaskDTO();
            dto.setCreatedByUserId(1L);
            assertEquals(1L, dto.getCreatedByUserId());
        }

        @Test
        @DisplayName("Should set and get createdByUsername")
        void setAndGetCreatedByUsername() {
            TaskDTO dto = new TaskDTO();
            dto.setCreatedByUsername("professor");
            assertEquals("professor", dto.getCreatedByUsername());
        }

        @Test
        @DisplayName("Should set and get completionDate")
        void setAndGetCompletionDate() {
            TaskDTO dto = new TaskDTO();
            LocalDate date = LocalDate.of(2025, 12, 20);
            dto.setCompletionDate(date);
            assertEquals(date, dto.getCompletionDate());
        }

        @Test
        @DisplayName("Should set and get createdAt")
        void setAndGetCreatedAt() {
            TaskDTO dto = new TaskDTO();
            LocalDateTime createdAt = LocalDateTime.of(2025, 12, 1, 9, 0);
            dto.setCreatedAt(createdAt);
            assertEquals(createdAt, dto.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Priority Values Tests")
    class PriorityValuesTests {

        @Test
        @DisplayName("Should handle LOW priority")
        void priorityLow() {
            TaskDTO dto = new TaskDTO();
            dto.setPriority("LOW");
            assertEquals("LOW", dto.getPriority());
        }

        @Test
        @DisplayName("Should handle MEDIUM priority")
        void priorityMedium() {
            TaskDTO dto = new TaskDTO();
            dto.setPriority("MEDIUM");
            assertEquals("MEDIUM", dto.getPriority());
        }

        @Test
        @DisplayName("Should handle HIGH priority")
        void priorityHigh() {
            TaskDTO dto = new TaskDTO();
            dto.setPriority("HIGH");
            assertEquals("HIGH", dto.getPriority());
        }

        @Test
        @DisplayName("Should handle CRITICAL priority")
        void priorityCritical() {
            TaskDTO dto = new TaskDTO();
            dto.setPriority("CRITICAL");
            assertEquals("CRITICAL", dto.getPriority());
        }
    }

    @Nested
    @DisplayName("Status Values Tests")
    class StatusValuesTests {

        @Test
        @DisplayName("Should handle PENDING status")
        void statusPending() {
            TaskDTO dto = new TaskDTO();
            dto.setStatus("PENDING");
            assertEquals("PENDING", dto.getStatus());
        }

        @Test
        @DisplayName("Should handle IN_PROGRESS status")
        void statusInProgress() {
            TaskDTO dto = new TaskDTO();
            dto.setStatus("IN_PROGRESS");
            assertEquals("IN_PROGRESS", dto.getStatus());
        }

        @Test
        @DisplayName("Should handle COMPLETED status")
        void statusCompleted() {
            TaskDTO dto = new TaskDTO();
            dto.setStatus("COMPLETED");
            dto.setCompleted(true);
            assertEquals("COMPLETED", dto.getStatus());
            assertTrue(dto.isCompleted());
        }

        @Test
        @DisplayName("Should handle CANCELLED status")
        void statusCancelled() {
            TaskDTO dto = new TaskDTO();
            dto.setStatus("CANCELLED");
            assertEquals("CANCELLED", dto.getStatus());
        }

        @Test
        @DisplayName("Should handle OVERDUE status")
        void statusOverdue() {
            TaskDTO dto = new TaskDTO();
            dto.setStatus("OVERDUE");
            assertEquals("OVERDUE", dto.getStatus());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null values")
        void nullValues() {
            TaskDTO dto = new TaskDTO(
                    null, null, null, null, null, null, false,
                    null, null, null, null, null, null, null, null);
            assertNull(dto.getId());
            assertNull(dto.getTitle());
            assertNull(dto.getDeadline());
        }

        @Test
        @DisplayName("Should handle empty strings")
        void emptyStrings() {
            TaskDTO dto = new TaskDTO();
            dto.setTitle("");
            dto.setDescription("");
            assertEquals("", dto.getTitle());
            assertEquals("", dto.getDescription());
        }

        @Test
        @DisplayName("Should handle past deadline")
        void pastDeadline() {
            TaskDTO dto = new TaskDTO();
            LocalDateTime pastDate = LocalDateTime.of(2020, 1, 1, 0, 0);
            dto.setDeadline(pastDate);
            assertEquals(pastDate, dto.getDeadline());
        }

        @Test
        @DisplayName("Should handle future deadline")
        void futureDeadline() {
            TaskDTO dto = new TaskDTO();
            LocalDateTime futureDate = LocalDateTime.of(2030, 12, 31, 23, 59);
            dto.setDeadline(futureDate);
            assertEquals(futureDate, dto.getDeadline());
        }

        @Test
        @DisplayName("Should handle unassigned task")
        void unassignedTask() {
            TaskDTO dto = new TaskDTO();
            dto.setTitle("Unassigned Task");
            dto.setAssignedToUserId(null);
            dto.setAssignedToUsername(null);
            assertNull(dto.getAssignedToUserId());
            assertNull(dto.getAssignedToUsername());
        }
    }

    @Nested
    @DisplayName("Modification Tests")
    class ModificationTests {

        @Test
        @DisplayName("Should allow modifying all fields after construction")
        void modifyAllFields() {
            TaskDTO dto = new TaskDTO();

            dto.setId(1L);
            dto.setTitle("Original");
            dto.setStatus("PENDING");
            dto.setCompleted(false);

            // Modify
            dto.setTitle("Modified");
            dto.setStatus("COMPLETED");
            dto.setCompleted(true);

            assertEquals("Modified", dto.getTitle());
            assertEquals("COMPLETED", dto.getStatus());
            assertTrue(dto.isCompleted());
        }

        @Test
        @DisplayName("Should allow reassigning task")
        void reassignTask() {
            TaskDTO dto = new TaskDTO();
            dto.setAssignedToUserId(10L);
            dto.setAssignedToUsername("user1");

            // Reassign
            dto.setAssignedToUserId(20L);
            dto.setAssignedToUsername("user2");

            assertEquals(20L, dto.getAssignedToUserId());
            assertEquals("user2", dto.getAssignedToUsername());
        }
    }
}
