package com.resumeai.service;

import com.resumeai.dto.AuthDTO;
import com.resumeai.exception.BadRequestException;
import com.resumeai.exception.ResourceNotFoundException;
import com.resumeai.model.User;
import com.resumeai.repository.UserRepository;
import com.resumeai.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("An account with this email already exists");
        }

        User user = User.builder()
            .name(request.name().trim())
            .email(request.email().trim().toLowerCase())
            .password(passwordEncoder.encode(request.password()))
            .role(request.role() == null ? User.Role.ROLE_RECRUITER : request.role())
            .build();

        User saved = userRepository.save(user);

        return new AuthDTO.AuthResponse(
            jwtUtils.generateToken(saved.getEmail()),
            saved.getId(),
            saved.getName(),
            saved.getEmail(),
            saved.getRole().name()
        );
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new AuthDTO.AuthResponse(
            jwtUtils.generateToken(user.getEmail()),
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().name()
        );
    }
}
