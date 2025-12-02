package com.team02.spmpevaluator.dto;

/**
 * Request payload for user login.
 * <p>
 * This class does not rely on Lombok so tools without annotation
 * processing still see the getters/setters.
 */
public class LoginRequest {

    private String username;
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
