package com.smartwaste.backend.controller;

import com.smartwaste.backend.User;
import com.smartwaste.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================================================
    // GET ALL USERS
    // =====================================================

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // =====================================================
    // GET USER BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {

        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =====================================================
    // CREATE USER
    // =====================================================

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        // Always register as USER
        user.setRole("USER");

        // Default reward points
        if (user.getRewardPoints() == null) {
            user.setRewardPoints(0);
        }

        // Encrypt password
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        // Save user
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(savedUser);
    }
}