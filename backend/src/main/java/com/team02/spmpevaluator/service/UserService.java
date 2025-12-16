package com.team02.spmpevaluator.service;

import com.team02.spmpevaluator.dto.UserDTO;
import com.team02.spmpevaluator.entity.PasswordResetToken;
import com.team02.spmpevaluator.entity.Role;
import com.team02.spmpevaluator.entity.User;
import com.team02.spmpevaluator.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SPMPDocumentRepository documentRepository;
    private final TaskRepository taskRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final StudentProfessorAssignmentRepository assignmentRepository;
    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository tokenRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Registers a new user with the provided details.
     */
    public User registerUser(String username, String email, String password, String firstName,
                            String lastName, Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }


    /**
     * Finds a user by username.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by email.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by ID.
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Gets a user by ID (throws exception if not found).
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    /**
     * Updates user information (by ID).
     */
    public User updateUser(Long id, String firstName, String lastName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    /**
     * Updates an existing user entity.
     */
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found with id: " + user.getId());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Changes user password.
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Converts User entity to UserDTO.
     */
    public UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.isEnabled()
        );
    }

    /**
     * Gets all users by role.
     */
    public List<UserDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets all users.
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Disables/enables a user account.
     */
    public void toggleUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    /**
     * Locks a user account.
     */
    public void lockUser(Long userId) {
        toggleUserStatus(userId, false);
    }

    /**
     * Unlocks a user account.
     */
    public void unlockUser(Long userId) {
        toggleUserStatus(userId, true);
    }

    /**
     * Resets a user's password (admin function).
     */
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Deletes a user by ID along with all related records.
     * This handles foreign key constraints by deleting related records first.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        
        // Delete related records in order to satisfy foreign key constraints
        // 1. Delete user's notifications
        notificationRepository.deleteByUserId(id);
        
        // 2. Delete user's audit logs
        auditLogRepository.deleteByUserId(id);
        
        // 3. Delete user's documents (cascade will handle related entities like compliance scores)
        documentRepository.deleteByUploadedById(id);
        
        // 4. Delete tasks created by or assigned to the user
        taskRepository.deleteByCreatedById(id);
        taskRepository.deleteByAssignedToId(id);
        
        // 5. Delete student-professor assignments
        assignmentRepository.deleteByStudentId(id);
        assignmentRepository.deleteByProfessorId(id);
        
        // 6. Finally delete the user
        userRepository.deleteById(id);
    }

    // ============= FORGOT PASSWORD FUNCTIONALITY =============

    /**
     * Process the "Forgot Password" request.
     * Generates a reset token and sends email to user.
     */
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        // Generate a random token
        String token = UUID.randomUUID().toString();

        // Save token to DB (valid for 30 minutes)
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(myToken);

        // Send the email
        sendResetEmail(user.getEmail(), token);
    }

    /**
     * Helper to send password reset email.
     */
    private void sendResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("YOUR_EMAIL@gmail.com"); // Match the one in application.properties
            message.setTo(toEmail);
            message.setSubject("SPMP Evaluator - Password Reset Request");

            // This link points to your React Frontend
            String resetLink = "http://localhost:3000/login?token=" + token;

            message.setText("Click the link below to reset your password:\n" + resetLink);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Reset password using token from forgot password flow.
     */
    public void resetPasswordWithToken(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the token so it can't be used again
        tokenRepository.delete(resetToken);
    }

    public User processOAuthPostLogin(String email, String name) {
        Optional<User> existUser = userRepository.findByEmail(email);

        if (existUser.isPresent()) {
            return existUser.get();
        }

        User newUser = new User();
        newUser.setEmail(email);
        
        String username = email.split("@")[0];
        if (userRepository.existsByUsername(username)) {
            username += UUID.randomUUID().toString().substring(0, 4);
        }
        newUser.setUsername(username);
        
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        
        String[] nameParts = name.split(" ");
        newUser.setFirstName(nameParts.length > 0 ? nameParts[0] : name);
        newUser.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        
        newUser.setRole(Role.STUDENT);
        newUser.setEnabled(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(newUser);
    }
}