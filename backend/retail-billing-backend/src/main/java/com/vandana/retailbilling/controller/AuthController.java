package com.vandana.retailbilling.controller;

import com.vandana.retailbilling.dto.LoginRequest;
import com.vandana.retailbilling.dto.LoginResponse;
import com.vandana.retailbilling.dto.UserDTO;
import com.vandana.retailbilling.entity.User;
import com.vandana.retailbilling.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(UserDTO.fromEntity(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // JWT is stateless, logout is handled client-side by removing token
        return ResponseEntity.ok("Logged out successfully");
    }
}
