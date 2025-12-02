package com.team02.spmpevaluator.dto;

/**
 * Request payload for user registration.
 * <p>
 * Note: This class intentionally avoids Lombok so that IDEs and tools
 * without Lombok support can still see the getters and setters and
 * avoid false "cannot find symbol" errors.
 */
public class RegisterRequest {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    /**
     * Role name as string (e.g. "STUDENT", "PROFESSOR", "PM").
     */
    private String role;

    public RegisterRequest() {
    }

    public RegisterRequest(String username,
                           String email,
                           String password,
                           String firstName,
                           String lastName,
                           String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
