package com.fantasynhl.server.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestParam String email, @RequestParam String password) {
        User user = authService.register(email, password);
        return ResponseEntity.ok(user);
    }

    // Login existing user
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String email, @RequestParam String password) {
        User user = authService.login(email, password);
        return ResponseEntity.ok(user);
    }
}