package com.resumeai.service;

import com.resumeai.exception.ResourceNotFoundException;
import com.resumeai.model.User;
import com.resumeai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            // For development: return or create a default user
            return getOrCreateDevUser();
        }

        return userRepository.findByEmail(authentication.getName())
            .orElseGet(this::getOrCreateDevUser);
    }

    private User getOrCreateDevUser() {
        return userRepository.findByEmail("dev@resumeai.local")
            .orElseGet(() -> {
                User devUser = User.builder()
                    .name("Development User")
                    .email("dev@resumeai.local")
                    .password("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi") // "password"
                    .role(User.Role.ROLE_RECRUITER)
                    .build();
                return userRepository.save(devUser);
            });
    }
}
