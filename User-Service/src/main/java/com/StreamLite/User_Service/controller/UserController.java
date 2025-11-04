package com.StreamLite.User_Service.controller;

import com.StreamLite.User_Service.dto.AuthRequest;
import com.StreamLite.User_Service.exception.UserAlredyExistException;
import com.StreamLite.User_Service.model.User;
import com.StreamLite.User_Service.repository.UserRepository;
import com.StreamLite.User_Service.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            User newuser = userService.saveUser(user);
            return ResponseEntity.ok(newuser);
        } catch (UserAlredyExistException e) {
            response.put("error", "user already exist");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request, HttpServletResponse response) {

        System.out.println("received email :" + request.getEmail());
        System.out.println("received password:" + request.getPassword());
        return userService.login(request, response);
    }

    @GetMapping("/current-user")
    public ResponseEntity<String> getCurrentUser(@CookieValue(name = "jwt", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("no token found");
        }
        return ResponseEntity.ok(userService.extractEmailFromToken(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        return ResponseEntity.ok(userService.logout(response));
    }
}
