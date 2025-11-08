package com.StreamLite.User_Service.controller;

import com.StreamLite.User_Service.model.User;
import com.StreamLite.User_Service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllNormalUsers() {
        List<User> allUsers = authService.getAllUsers();

        List<User> normalUsers = allUsers.stream()
                .filter(user -> !"ADMIN".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(normalUsers);
    }
}
