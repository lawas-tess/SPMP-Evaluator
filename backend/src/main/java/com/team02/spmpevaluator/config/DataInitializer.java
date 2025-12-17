package com.team02.spmpevaluator.config;

import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * DataInitializer creates a default admin user on application startup if none exists.
 * This is useful for local development and testing.
 * 
 * For production, disable this by setting:
 * app.data-initializer.enabled=false in application.properties
 * 
 * Or use secure environment variables for admin credentials.
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${app.data-initializer.enabled:true}")
    private boolean initializerEnabled;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.email:admin@spmpevaluator.com}")
    private String adminEmail;

    @Bean
    public CommandLineRunner initializeData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!initializerEnabled) {
                logger.info("DataInitializer is disabled. Skipping admin user creation.");
                return;
            }

            // Check if any admin user exists
            boolean adminExists = userRepository.findByRole(Role.ADMIN)
                    .stream()
                    .findAny()
                    .isPresent();

            if (adminExists) {
                logger.info("Admin user already exists. Skipping admin user creation.");
                return;
            }

            // Create default admin user
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEnabled(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(admin);

            logger.info("==============================================");
            logger.info("DEFAULT ADMIN USER CREATED");
            logger.info("Username: {}", adminUsername);
            logger.info("Password: {}", adminPassword);
            logger.info("Email: {}", adminEmail);
            logger.info("==============================================");
            logger.warn("IMPORTANT: Change the default admin password in production!");
            logger.info("==============================================");
        };
    }
}
