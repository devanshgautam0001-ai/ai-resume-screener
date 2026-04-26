package com.resumeai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public class JobDTO {

    public record CreateJobRequest(
        @NotBlank String title,
        @NotBlank String company,
        String location,
        String employmentType,
        @NotNull Integer minExperienceYears,
        @NotBlank String description,
        List<String> requiredSkills
    ) {
    }

    public record JobResponse(
        Long id,
        String title,
        String company,
        String location,
        String employmentType,
        Integer minExperienceYears,
        String description,
        String status,
        List<String> requiredSkills,
        Instant createdAt
    ) {
    }
}
