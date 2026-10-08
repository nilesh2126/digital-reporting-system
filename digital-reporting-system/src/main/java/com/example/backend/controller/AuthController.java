package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users; this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();
        if (users.existsByUsername(username) || users.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username or email already exists."));
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(request.getPassword()));
        user.setRole("CITIZEN"); // Public registration can never choose a staff/admin role.
        users.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Registration successful. Please log in."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        User user = users.findByUsername(request.getUsername().trim()).orElse(null);
        if (user == null || !encoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid username or password."));
        }
        session.setAttribute("userId", user.getId());
        return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername(),
                "email", user.getEmail(), "role", user.getRole()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (!(id instanceof Long userId)) return ResponseEntity.status(401).body(Map.of("message", "Please log in."));
        User user = users.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(401).body(Map.of("message", "Please log in."));
        return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername(),
                "email", user.getEmail(), "role", user.getRole()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out."));
    }
}
