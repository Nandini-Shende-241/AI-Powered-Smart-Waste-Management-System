package com.smartwaste.backend;

import com.smartwaste.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public LoginController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );

            User user = userRepository
                    .findByEmail(request.getEmail())
                    .orElseThrow();

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Login successful",
                            "userId", user.getId(),
                            "email", user.getEmail()
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }
    }

    public static class LoginRequest {

        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}