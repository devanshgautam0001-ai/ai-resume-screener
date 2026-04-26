package com.resumeai.controller;

import com.resumeai.dto.ApiResponse;
import com.resumeai.dto.AuthDTO;
import com.resumeai.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthDTO.AuthResponse> register(@Valid @RequestBody AuthDTO.RegisterRequest request) {
        return new ApiResponse<>(true, "Registration successful", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthDTO.AuthResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request) {
        return new ApiResponse<>(true, "Login successful", authService.login(request));
    }
}
