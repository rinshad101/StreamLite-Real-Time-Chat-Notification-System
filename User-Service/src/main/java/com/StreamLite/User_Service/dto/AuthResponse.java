package com.StreamLite.User_Service.dto;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String email, String refreshToken, String role) {
        this.accessToken = accessToken;
        this.email = email;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
