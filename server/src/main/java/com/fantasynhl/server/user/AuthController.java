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
    public ResponseEntity<User> register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        User user = authService.register(username, email, password);
        return ResponseEntity.ok(user);
    }

    // Login existing user
    @PostMapping("/login")
    public ResponseEntity<User> login(
            @RequestParam String username,
            @RequestParam String password) {

        User user = authService.login(username, password);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/forgot-username")
    public ResponseEntity<String> forgotUsername(
            @RequestParam String email) {

        authService.forgotUsername(email);

        return ResponseEntity.ok(
                "If an account exists with that email address, your username has been sent."
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestParam String email) {

        authService.forgotPassword(email);

        return ResponseEntity.ok(
                "If an account exists with that email address, a password reset link has been sent."
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        authService.resetPassword(token, newPassword);

        return ResponseEntity.ok(
                "Password has been reset successfully."
        );
    }
}