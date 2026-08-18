package com.fantasynhl.server.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                   PasswordEncoder passwordEncoder,
                   EmailService emailService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
}

    // Register a new user
    public User register(String username, String email, String password) {

        Optional<User> existingUsername = userRepository.findByUsername(username);
        if (existingUsername.isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Optional<User> existingEmail = userRepository.findByEmail(email);
        if (existingEmail.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        // Hash the password before storing it
        user.setPassword(passwordEncoder.encode(password));

        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    // Login existing user
    public User login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Compare the entered password against the stored BCrypt hash
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public void forgotUsername(String email) {

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            emailService.sendUsernameEmail(
                    user.get().getEmail(),
                    user.get().getUsername()
            );
        }
    }
}