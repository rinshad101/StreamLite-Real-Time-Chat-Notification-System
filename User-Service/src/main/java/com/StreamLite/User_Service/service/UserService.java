package com.StreamLite.User_Service.service;

import com.StreamLite.User_Service.dto.AuthRequest;
import com.StreamLite.User_Service.dto.AuthResponse;
import com.StreamLite.User_Service.exception.UserAlredyExistException;
import com.StreamLite.User_Service.model.User;
import com.StreamLite.User_Service.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlredyExistException("User with email " + user.getEmail() + " already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public ResponseEntity<?> login(AuthRequest request, HttpServletResponse response) {
        try {
            if (request == null || request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest().body("Email and password must be provided");
            }
            System.out.println("Request Email: " + request.getEmail());

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("user not found"));

            System.out.println("User Found: " + user.getEmail());

            System.out.println("Received Login Request: " + request.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            System.out.println("Authentication Successful for: " + request.getEmail());

            String accessToken = jwtService.generateToken(user);
            System.out.println("Generated Token: " + accessToken);

            String refreshToken = jwtService.generateRefreshToken(user.getEmail());
            System.out.println("Generated Refresh Token: " + refreshToken);

            addCookie(response, "jwt", accessToken, 3600);
            addCookie(response, "refreshToken", refreshToken, 86400);

            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        } catch (Exception e) {
            System.out.println("Authentication Failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid credentials");
        }

    }

    public String extractEmailFromToken(String token) {
        return jwtService.extractEmail(token);
    }

    public Object logout(HttpServletResponse response) {
        clearCookie(response, "jwt");
        clearCookie(response, "refreshToken");
        return ResponseEntity.ok("logout successfully");
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
