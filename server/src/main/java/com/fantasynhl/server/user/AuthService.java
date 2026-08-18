package com.fantasynhl.server.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthService(UserRepository userRepository,
                   PasswordEncoder passwordEncoder,
                   EmailService emailService,
                   PasswordResetTokenRepository passwordResetTokenRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
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

    public void forgotPassword(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        // Generate a secure random token
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // Store only the hashed token in the database
        String tokenHash = hashToken(rawToken);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(30);

        PasswordResetToken resetToken = new PasswordResetToken(
                user,
                tokenHash,
                expiresAt,
                now
        );

        passwordResetTokenRepository.save(resetToken);

        // The raw token is only sent to the user's email
        String resetLink =
                "https://hockey.trevor-dunn.com/reset-password?token="
                        + rawToken;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink
        );
    }

    public void resetPassword(String token, String newPassword) {

        String tokenHash = hashToken(token);

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("Reset token has already been used");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        User user = resetToken.getUser();

        // Hash the new password before storing it
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        // Make the reset token single-use
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm not available", e
            );
        }
    }
}