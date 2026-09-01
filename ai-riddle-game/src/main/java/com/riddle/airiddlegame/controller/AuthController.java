package com.riddle.airiddlegame.controller;

import com.riddle.airiddlegame.dto.request.LoginRequest;
import com.riddle.airiddlegame.dto.request.RegisterRequest;
import com.riddle.airiddlegame.dto.response.ApiResponse;
import com.riddle.airiddlegame.dto.response.AuthResponse;
import com.riddle.airiddlegame.security.UserPrincipal;
import com.riddle.airiddlegame.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully!", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful!", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userPrincipal.getId());
        userData.put("username", userPrincipal.getUsername());
        userData.put("email", userPrincipal.getEmail());
        userData.put("authorities", userPrincipal.getAuthorities());
        return ResponseEntity.ok(ApiResponse.success(userData));
    }
}
