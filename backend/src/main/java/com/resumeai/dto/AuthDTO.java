package com.resumeai.dto;

import com.resumeai.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDTO {

    public record RegisterRequest(
        @NotBlank String name,
        @Email String email,
        @NotBlank String password,
        User.Role role
    ) {
    }

    public record LoginRequest(
        @Email String email,
        @NotBlank String password
    ) {
    }

    public record AuthResponse(
        String token,
        Long userId,
        String name,
        String email,
        String role
    ) {
    }
}
