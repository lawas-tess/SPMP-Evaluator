package com.team02.spmpevaluator.config;

import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DataInitializer.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DataInitializer Tests")
class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer = new DataInitializer();
        // Set default values using reflection
        ReflectionTestUtils.setField(dataInitializer, "initializerEnabled", true);
        ReflectionTestUtils.setField(dataInitializer, "adminUsername", "admin");
        ReflectionTestUtils.setField(dataInitializer, "adminPassword", "admin123");
        ReflectionTestUtils.setField(dataInitializer, "adminEmail", "admin@spmpevaluator.com");
    }

    @Nested
    @DisplayName("Initialize Data Tests - Enabled")
    class InitializeDataEnabledTests {

        @Test
        @DisplayName("Should create admin user when no admin exists")
        void initializeData_NoAdminExists_CreatesAdminUser() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should not create admin user when admin already exists")
        void initializeData_AdminExists_DoesNotCreateAdmin() throws Exception {
            User existingAdmin = new User();
            existingAdmin.setRole(Role.ADMIN);
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of(existingAdmin));

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should set correct username for admin user")
        void initializeData_SetsCorrectUsername() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals("admin", userCaptor.getValue().getUsername());
        }

        @Test
        @DisplayName("Should set correct email for admin user")
        void initializeData_SetsCorrectEmail() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals("admin@spmpevaluator.com", userCaptor.getValue().getEmail());
        }

        @Test
        @DisplayName("Should set ADMIN role for admin user")
        void initializeData_SetsAdminRole() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals(Role.ADMIN, userCaptor.getValue().getRole());
        }

        @Test
        @DisplayName("Should encode password before saving")
        void initializeData_EncodesPassword() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode("admin123")).thenReturn("encodedAdminPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            verify(passwordEncoder).encode("admin123");
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals("encodedAdminPassword", userCaptor.getValue().getPassword());
        }

        @Test
        @DisplayName("Should set enabled to true for admin user")
        void initializeData_SetsEnabledTrue() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertTrue(userCaptor.getValue().isEnabled());
        }

        @Test
        @DisplayName("Should set first name to System")
        void initializeData_SetsFirstName() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals("System", userCaptor.getValue().getFirstName());
        }

        @Test
        @DisplayName("Should set last name to Administrator")
        void initializeData_SetsLastName() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals("Administrator", userCaptor.getValue().getLastName());
        }

        @Test
        @DisplayName("Should set createdAt timestamp")
        void initializeData_SetsCreatedAt() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertNotNull(userCaptor.getValue().getCreatedAt());
        }

        @Test
        @DisplayName("Should set updatedAt timestamp")
        void initializeData_SetsUpdatedAt() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertNotNull(userCaptor.getValue().getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Initialize Data Tests - Disabled")
    class InitializeDataDisabledTests {

        @Test
        @DisplayName("Should not create admin when initializer is disabled")
        void initializeData_Disabled_DoesNotCreateAdmin() throws Exception {
            ReflectionTestUtils.setField(dataInitializer, "initializerEnabled", false);

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            verify(userRepository, never()).findByRole(any());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should not encode password when disabled")
        void initializeData_Disabled_DoesNotEncodePassword() throws Exception {
            ReflectionTestUtils.setField(dataInitializer, "initializerEnabled", false);

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Nested
    @DisplayName("Custom Configuration Tests")
    class CustomConfigurationTests {

        @Test
        @DisplayName("Should use custom username from configuration")
        void initializeData_UsesCustomUsername() throws Exception {
            ReflectionTestUtils.setField(dataInitializer, "adminUsername", "customAdmin");
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals("customAdmin", userCaptor.getValue().getUsername());
        }

        @Test
        @DisplayName("Should use custom password from configuration")
        void initializeData_UsesCustomPassword() throws Exception {
            ReflectionTestUtils.setField(dataInitializer, "adminPassword", "customPassword123");
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode("customPassword123")).thenReturn("encodedCustomPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            verify(passwordEncoder).encode("customPassword123");
        }

        @Test
        @DisplayName("Should use custom email from configuration")
        void initializeData_UsesCustomEmail() throws Exception {
            ReflectionTestUtils.setField(dataInitializer, "adminEmail", "custom@example.com");
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertEquals("custom@example.com", userCaptor.getValue().getEmail());
        }
    }

    @Nested
    @DisplayName("CommandLineRunner Bean Tests")
    class CommandLineRunnerBeanTests {

        @Test
        @DisplayName("Should return non-null CommandLineRunner")
        void initializeData_ReturnsNonNullRunner() {
            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);

            assertNotNull(runner);
        }

        @Test
        @DisplayName("Should be callable multiple times")
        void initializeData_CallableMultipleTimes() throws Exception {
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(Collections.emptyList());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            // Second call - admin now exists
            User existingAdmin = new User();
            existingAdmin.setRole(Role.ADMIN);
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of(existingAdmin));

            runner.run();

            // Should only save once (first call)
            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Multiple Admin Handling Tests")
    class MultipleAdminHandlingTests {

        @Test
        @DisplayName("Should not create admin when multiple admins exist")
        void initializeData_MultipleAdminsExist_DoesNotCreateAdmin() throws Exception {
            User admin1 = new User();
            admin1.setRole(Role.ADMIN);
            User admin2 = new User();
            admin2.setRole(Role.ADMIN);
            when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of(admin1, admin2));

            CommandLineRunner runner = dataInitializer.initializeData(userRepository, passwordEncoder);
            runner.run();

            verify(userRepository, never()).save(any(User.class));
        }
    }
}
