package com.StreamLite.User_Service.controller;

import com.StreamLite.User_Service.dto.AuthRequest;
import com.StreamLite.User_Service.dto.AuthResponse;
import com.StreamLite.User_Service.dto.RegisterRequest;
import com.StreamLite.User_Service.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req, HttpServletResponse res) {
        return ResponseEntity.ok(authService.login(req, res));
    }

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(@CookieValue(name = "accessToken", required = false) String token) {
        System.out.println(token);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("no token found");
        }
        return ResponseEntity.ok(authService.extractEmailFromToken(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok("Logged out successfully");
    }
}
