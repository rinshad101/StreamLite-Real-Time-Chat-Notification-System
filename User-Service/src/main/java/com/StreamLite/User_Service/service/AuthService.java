package com.StreamLite.User_Service.service;

import com.StreamLite.User_Service.dto.AuthRequest;
import com.StreamLite.User_Service.dto.AuthResponse;
import com.StreamLite.User_Service.dto.RegisterRequest;
import com.StreamLite.User_Service.model.User;
import com.StreamLite.User_Service.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwtService, AuthenticationManager authManager) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    public String register(RegisterRequest req) {
        if (repo.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(req.getRole() != null ? req.getRole() : "ROLE_USER");

        repo.save(user);
        return "User registered successfully";
    }

    public AuthResponse login(AuthRequest req, HttpServletResponse res) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole(), true);
        String refreshToken = jwtService.generateToken(user.getEmail(), user.getRole(), false);

        addCookie(res, "accessToken", accessToken, true, 60 * 15);
        addCookie(res, "refreshToken", refreshToken, true, 60 * 60 * 24 * 7);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return response;
    }

    public String extractEmailFromToken(String token) {
        System.out.println(token);
        return jwtService.extractEmail(token);
    }

    public void logout(HttpServletResponse res) {
        clearCookie(res, "accessToken");
        clearCookie(res, "refreshToken");
    }

    private void addCookie(HttpServletResponse res, String name, String value, boolean httpOnly, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        res.addCookie(cookie);
    }

    private void clearCookie(HttpServletResponse res, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        res.addCookie(cookie);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }
}
