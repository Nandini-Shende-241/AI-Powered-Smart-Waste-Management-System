package com.smartwaste.backend.controller;

import com.smartwaste.backend.User;
import com.smartwaste.backend.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    private final HttpSessionSecurityContextRepository
            securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        try {

            // -------------------------------------------------
            // Validate input
            // -------------------------------------------------

            if (email == null || email.isBlank()
                    || password == null || password.isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Email and password are required"
                        ));
            }

            // -------------------------------------------------
            // Authenticate
            // -------------------------------------------------

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    password
                            )
                    );

            // -------------------------------------------------
            // Get user
            // -------------------------------------------------

            User user =
                    userRepository.findByEmail(email)
                            .orElseThrow();

            // =================================================
            // SAVE AUTHENTICATION IN SPRING SECURITY SESSION
            // =================================================

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);

            securityContextRepository.saveContext(
                    context,
                    request,
                    response
            );

            // =================================================
            // ALSO SAVE USER INFORMATION IN HTTP SESSION
            // =================================================

            HttpSession session =
                    request.getSession(true);

            session.setAttribute(
                    "userId",
                    user.getId()
            );

            session.setAttribute(
                    "userName",
                    user.getName()
            );

            session.setAttribute(
                    "userEmail",
                    user.getEmail()
            );

            session.setAttribute(
                    "userRole",
                    user.getRole()
            );

            // -------------------------------------------------
            // Return user information to frontend
            // -------------------------------------------------

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Login successful",

                            "id",
                            user.getId(),

                            "name",
                            user.getName(),

                            "email",
                            user.getEmail(),

                            "role",
                            user.getRole(),

                            "rewardPoints",
                            user.getRewardPoints()
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(401)
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid email or password"
                            )
                    );
        }
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request) {

        SecurityContextHolder.clearContext();

        HttpSession session =
                request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Logout successful"
                )
        );
    }
}